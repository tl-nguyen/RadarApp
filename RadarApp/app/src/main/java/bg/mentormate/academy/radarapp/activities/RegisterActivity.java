package bg.mentormate.academy.radarapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.CurrentLocation;
import bg.mentormate.academy.radarapp.models.Follow;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;

public class RegisterActivity extends ActionBarActivity implements View.OnClickListener {

    ParseFile mBlankAvatar;

    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtEmail;
    private Button mBtnRegister;
    private Button mBtnCancel;
    private ProgressBar mProgresBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        init();
    }

    private void init() {
        mEtUsername = (EditText) findViewById(R.id.etUsername);
        mEtPassword = (EditText) findViewById(R.id.etPassword);
        mEtEmail = (EditText) findViewById(R.id.etEmail);
        mBtnRegister = (Button) findViewById(R.id.btnRegister);
        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mProgresBar = (ProgressBar) findViewById(R.id.progressBar);

        mBtnRegister.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnRegister:
                register();
                break;
            case R.id.btnCancel:
                finish();
                break;
        }
    }

    private void register() {
        String username = mEtUsername.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        String email = mEtEmail.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            // The inputs are empty, show an alert
            AlertHelper.alert(this, getString(R.string.dialog_error_title),
                    getString(R.string.signup_invalid_inputs_message));
        } else {
            // Create the new user in Parse.com
            createUser(username, password, email);
        }
    }

    private void createUser(String username, String password, String email) {
        CreateUserTask createUserTask = new CreateUserTask();
        createUserTask.execute(username, password, email);
    }

    private byte[] getBitmapFromDrawableId(int id) {
        Drawable drawable = getResources().getDrawable(id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void hideProgressBar() {
        mProgresBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        mProgresBar.setVisibility(View.VISIBLE);
    }

    private void goToMain(User user) {
        LocalDb.getInstance().setCurrentUser(user);
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }

    private class CreateUserTask extends AsyncTask<String, ParseException, Void> {
        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected Void doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);

            // Retrieve user current Location
            CurrentLocation currentLocation = new CurrentLocation();
            Location location = getLocation();
            if (location != null) {
                currentLocation.setLocation(new ParseGeoPoint(location.getLatitude(),
                        location.getLongitude()));
            } else {
                currentLocation.setLocation(new ParseGeoPoint(0, 0));
            }

            // Create an empty follow row
            Follow emptyFollow = new Follow();
            emptyFollow.setFollowers(new ArrayList<User>());
            emptyFollow.setFollowings(new ArrayList<User>());

            // Putting ic_launcher as a default avatar
            if (mBlankAvatar == null) {
                mBlankAvatar = new ParseFile(
                        getBitmapFromDrawableId(R.drawable.ic_launcher));
            }

            try {
                // Save the new follow table
                emptyFollow.save();
                newUser.setFollow(emptyFollow);

                // Save the avatar file
                mBlankAvatar.save();
                newUser.setAvatar(mBlankAvatar);

                // Save empty location (0,9)
                currentLocation.save();
                newUser.setCurrentLocation(currentLocation);

                // sign-up the new user
                newUser.signUp();
                goToMain(newUser);
            } catch (final ParseException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertHelper.alert(RegisterActivity.this,
                                getString(R.string.dialog_error_title),
                                e.getMessage());
                    }
                });
            }

            return null;
        }

        private Location getLocation() {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            return location;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideProgressBar();
        }
    }
}
