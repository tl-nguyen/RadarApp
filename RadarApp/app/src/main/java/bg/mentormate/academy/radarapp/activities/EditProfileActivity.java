package bg.mentormate.academy.radarapp.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;

public class EditProfileActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int SELECT_PHOTO = 100;
    ParseFile mNewAvatar;

    private LocalDb mLocalDb;
    private User mUser;
    private ImageView mEditAvatar;
    private Button saveChangesBtn;
    private Button changeAvatarBtn;
    private Button takePiButton;
    private EditText editPassword;
    private EditText confirmPassword;
    private EditText editEmail;
    private ImageView mIvAvatar;

    private Bitmap imageBitmap;
    private String currentPhotoPath;

    private final int DEFAULT_IMG_SIZE_X = 100;
    private final int DEFAULT_IMG_SIZE_Y = 100;
    private final int DEFAULT_QUALITY_FACTOR = 90;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        avatarChanged = false;

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mEditAvatar = (ImageView) findViewById(R.id.imageView);
        mLocalDb = LocalDb.getInstance();
        mUser = mLocalDb.getCurrentUser();

        editPassword = (EditText) findViewById(R.id.editPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmPass);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editEmail.setHint(mUser.getEmail());

        saveChangesBtn = (Button) findViewById(R.id.saveChangesBtn);
        changeAvatarBtn = (Button) findViewById(R.id.saveAvatarBtn);
        takePiButton = (Button) findViewById(R.id.takePicBtn);

        saveChangesBtn.setOnClickListener(this);
        changeAvatarBtn.setOnClickListener(this);
        takePiButton.setOnClickListener(this);

        mUser.getAvatar().getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    Bitmap imgBitmap = BitmapFactory.decodeByteArray(
                            bytes,
                            0,
                            bytes.length);

                    mEditAvatar.setImageBitmap(imgBitmap);
                } else {
                    showErrorAlert(e);
                }
            }
        });
    }

    private void showErrorAlert(ParseException e) {
        AlertHelper.alert(this, getString(R.string.dialog_error_title), e.getMessage());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    boolean avatarChanged = false;

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.saveChangesBtn:
                boolean emailChanged = false;
                boolean passChanged = false;
                boolean passChangeAttempt = false;

                String newEmail = editEmail.getText().toString();
                String pass = editPassword.getText().toString();
                String confirmPass = confirmPassword.getText().toString();

                if(avatarChanged){
                   mNewAvatar = new ParseFile(getBitmapAsByteArray(bitmap));
                   mUser.setAvatar(mNewAvatar);
                }

                if(newEmail != "" && newEmail.contains("@") && newEmail.contains(".")){
                    mUser.setEmail(newEmail.trim());
                    emailChanged = true;
                }

                if(!pass.isEmpty() && !confirmPass.isEmpty()) {
                    if (pass.equals(confirmPass)) {
                        mUser.setPassword(pass.trim());
                        passChanged = true;

                    } else {
                        passChangeAttempt = true;
                    }
                }

                if(emailChanged || passChanged || avatarChanged){
                    mUser.saveInBackground();
                    if(passChangeAttempt) {
                        Toast.makeText(getApplicationContext(), getString(R.string.detailsHaveBeenSaved), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), getString(R.string.allDetailsHaveBeenSaved), Toast.LENGTH_SHORT).show();
                        this.finish();
                    }
                }

                break;
            case R.id.saveAvatarBtn:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);

                break;
            case R.id.takePicBtn:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = Constants.JPEG_FILE_PREFIX + timeStamp;

                File albumFolder = null;
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    albumFolder = new File(Environment.getExternalStorageDirectory() + Constants.CAMERA_DIR + Constants.ALBUM_NAME);
                    if (albumFolder != null) {
                        if (!albumFolder.mkdirs()) {
                            if (!albumFolder.exists()) {
                                Log.d("Camera:", "failed to create directory");
                                albumFolder = null;
                            }
                        }
                    }
                } else {
                    Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
                }

                File photoFile = new File(albumFolder, imageFileName + Constants.JPEG_FILE_SUFFIX);
                currentPhotoPath = photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

                startActivityForResult(takePictureIntent, Constants.ACTION_TAKE_PHOTO);
                break;
        }


    }

    private byte[] getBitmapAsByteArray(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    final int GET_CAM_IMG = 0;
    final int GET_GAL_IMG = 100;
    Bitmap bitmap;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == -1) {
            Bitmap bmp_image = null;

            switch (requestCode) {
                case GET_CAM_IMG:
                    if (currentPhotoPath != null) {
                        bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                        //bitmap = getResizedBitmap(bitmap, DEFAULT_IMG_SIZE_X, DEFAULT_IMG_SIZE_Y);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        if (bitmap != null) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_QUALITY_FACTOR, byteArrayOutputStream);
                            byte[] image = byteArrayOutputStream.toByteArray();
                            InputStream stream = new ByteArrayInputStream(image);
                            avatarChanged = true;
                            bitmap = BitmapFactory.decodeStream(stream);
                            bitmap = getResizedBitmap(bitmap, DEFAULT_IMG_SIZE_X, DEFAULT_IMG_SIZE_Y);
                            mEditAvatar.setImageBitmap(bitmap);
                            //mEditAvatar.setImageBitmap(bitmap);
                            mEditAvatar.setVisibility(View.VISIBLE);
                        }

                        avatarChanged = true;
                    }

                    //bmp_image = getImageFromCamera(intent);
                    break;
                case GET_GAL_IMG:
                    bitmap = getImageFromGallery(intent);
                    //bitmap = getResizedBitmap(bitmap, DEFAULT_IMG_SIZE_X, DEFAULT_IMG_SIZE_Y);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_QUALITY_FACTOR, byteArrayOutputStream);
                        byte[] image = byteArrayOutputStream.toByteArray();
                        InputStream stream = new ByteArrayInputStream(image);

                        bitmap = BitmapFactory.decodeStream(stream);
                        bitmap = getResizedBitmap(bitmap, DEFAULT_IMG_SIZE_X, DEFAULT_IMG_SIZE_Y);
                        mEditAvatar.setImageBitmap(bitmap);
                        mEditAvatar.setVisibility(View.VISIBLE);

                        avatarChanged = true;
                    }

                    break;
            }


        }

    }

    private Bitmap getImageFromCamera(Intent intent) {
        Uri selectedImage = intent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(
                selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return BitmapFactory.decodeFile(filePath);
    }

    private Bitmap getImageFromGallery(Intent intent) {
        Uri selectedImageUri = intent.getData();
        Bitmap bmp_image = null;

        if (Build.VERSION.SDK_INT < 19) {
            String selectedImagePath = getPath(selectedImageUri);

            bmp_image = BitmapFactory.decodeFile(selectedImagePath);
        } else {
            ParcelFileDescriptor parcelFileDescriptor;
            try {
                parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                bmp_image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bmp_image;
    }

    private String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}
