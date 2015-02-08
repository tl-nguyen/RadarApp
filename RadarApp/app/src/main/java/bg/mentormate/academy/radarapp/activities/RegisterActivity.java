package bg.mentormate.academy.radarapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.DialogHelper;

public class RegisterActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtEmail;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    private void init() {
        mEtUsername = (EditText) findViewById(R.id.etUsername);
        mEtPassword = (EditText) findViewById(R.id.etPassword);
        mEtEmail = (EditText) findViewById(R.id.etEmail);
        mBtnRegister = (Button) findViewById(R.id.btnRegister);

        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnRegister:
                register();
                break;
        }
    }

    private void register() {
        String username = mEtUsername.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        String email = mEtEmail.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            // The input are empty, show an alert
            DialogHelper.showAlert(this, getString(R.string.dialog_error_title),
                    getString(R.string.signup_invalid_inputs_message));
        }
        else {
            // Create the new user in Parse.com
            createUser(username, password, email);
        }
    }

    private void createUser(String username, String password, String email) {
        final User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.setCurrentLocation(new ParseGeoPoint(0f, 0f));
        newUser.setFollowers(new ArrayList<User>());
        newUser.setFollowing(new ArrayList<User>());

        // Putting ic_launcher as a default avatar
        final ParseFile blankAvatar = new ParseFile(
                getBitmapFromDrawableId(R.drawable.ic_launcher));

        // Save the avatar file
        blankAvatar.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                newUser.setAvatar(blankAvatar);

                // sign-up the new user
                newUser.signUpInBackground(new SignUpCallback() {

                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // Signed up successfully
                            goToMain();
                        }
                        else {
                            DialogHelper.showAlert(RegisterActivity.this, getString(R.string.dialog_error_title), e.getMessage());
                        }
                    }
                });
            }
        });
    }

    private byte[] getBitmapFromDrawableId(int id) {
        Drawable drawable = getResources().getDrawable(id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void goToMain() {
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }
}
