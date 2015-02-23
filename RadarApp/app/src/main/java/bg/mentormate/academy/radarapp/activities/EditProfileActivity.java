package bg.mentormate.academy.radarapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.User;

public class EditProfileActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int SELECT_PHOTO = 100;
    private static final int DEFAULT_IMG_SIZE_X = 100;
    private static final int DEFAULT_IMG_SIZE_Y = 100;
    private static final int DEFAULT_QUALITY_FACTOR = 90;
    private static final int GET_CAM_IMG = 0;
    private static final int GET_GAL_IMG = 100;

    private String currentPhotoPath;
    private ParseFile mNewAvatar;
    private LocalDb mLocalDb;
    private User mUser;
    boolean mAvatarChanged;
    private Bitmap mAvatarBitmap;

    private ParseImageView mPivAvatar;
    private Button mBtnSaveChanges;
    private Button mBtnChangeAvatar;
    private Button mBtnTakePic;
    private EditText mEtChangePassword;
    private EditText mEtConfirmPassword;
    private EditText mEtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        mAvatarChanged = false;

        mLocalDb = LocalDb.getInstance();
        mUser = mLocalDb.getCurrentUser();

        mPivAvatar = (ParseImageView) findViewById(R.id.ivAvatar);
        mEtChangePassword = (EditText) findViewById(R.id.etChangePassword);
        mEtConfirmPassword = (EditText) findViewById(R.id.etConfirmPass);
        mEtEmail = (EditText) findViewById(R.id.editChangeEmail);
        mEtEmail.setHint(mUser.getEmail());

        mBtnSaveChanges = (Button) findViewById(R.id.btnSaveChanges);
        mBtnChangeAvatar = (Button) findViewById(R.id.btnChangeAvatar);
        mBtnTakePic = (Button) findViewById(R.id.btnTakePic);

        mBtnSaveChanges.setOnClickListener(this);
        mBtnChangeAvatar.setOnClickListener(this);
        mBtnTakePic.setOnClickListener(this);

        mPivAvatar.setParseFile(mUser.getAvatar());
        mPivAvatar.loadInBackground();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnSaveChanges:
                onSaveClicked();
                break;
            case R.id.btnChangeAvatar:
                onChangeAvatarClicked();
                break;
            case R.id.btnTakePic:
                onTakePictureClicked();
                break;
        }
    }

    private void onTakePictureClicked() {
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
    }

    private void onChangeAvatarClicked() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    private void onSaveClicked() {
        boolean emailChanged = false;
        boolean passChanged = false;
        boolean passChangeAttempt = false;

        String newEmail = mEtEmail.getText().toString();
        String pass = mEtChangePassword.getText().toString();
        String confirmPass = mEtConfirmPassword.getText().toString();

        if(mAvatarChanged){
           mNewAvatar = new ParseFile(getBitmapAsByteArray(mAvatarBitmap));
           mUser.setAvatar(mNewAvatar);
        }

        if(!newEmail.isEmpty() && newEmail.contains("@") && newEmail.contains(".")){
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

        if(emailChanged || passChanged || mAvatarChanged){
            mUser.saveInBackground();
            if(passChangeAttempt) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.detailsHaveBeenSaved),
                        Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),
                        getString(R.string.allDetailsHaveBeenSaved),
                        Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    private byte[] getBitmapAsByteArray(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == -1) {
            switch (requestCode) {
                case GET_CAM_IMG:
                    retrieveAvatarFromCamera();
                    break;
                case GET_GAL_IMG:
                    retrieveAvatarFromGallery(intent);
                    break;
            }
        }
    }

    private void retrieveAvatarFromCamera() {
        if (currentPhotoPath != null) {
            mAvatarBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            if (mAvatarBitmap != null) {
                mAvatarBitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_QUALITY_FACTOR, byteArrayOutputStream);
                byte[] image = byteArrayOutputStream.toByteArray();
                InputStream stream = new ByteArrayInputStream(image);
                mAvatarChanged = true;
                mAvatarBitmap = BitmapFactory.decodeStream(stream);
                mAvatarBitmap = getResizedBitmap(mAvatarBitmap, DEFAULT_IMG_SIZE_X, DEFAULT_IMG_SIZE_Y);
                mPivAvatar.setImageBitmap(mAvatarBitmap);
                mPivAvatar.setVisibility(View.VISIBLE);
            }

            mAvatarChanged = true;
        }
    }

    private void retrieveAvatarFromGallery(Intent intent) {
        mAvatarBitmap = getImageFromGallery(intent);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (mAvatarBitmap != null) {
            mAvatarBitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_QUALITY_FACTOR, byteArrayOutputStream);
            byte[] image = byteArrayOutputStream.toByteArray();
            InputStream stream = new ByteArrayInputStream(image);

            mAvatarBitmap = BitmapFactory.decodeStream(stream);
            mAvatarBitmap = getResizedBitmap(mAvatarBitmap, DEFAULT_IMG_SIZE_X, DEFAULT_IMG_SIZE_Y);
            mPivAvatar.setImageBitmap(mAvatarBitmap);
            mPivAvatar.setVisibility(View.VISIBLE);

            mAvatarChanged = true;
        }
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
