package bg.mentormate.academy.radarapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.tools.AlertHelper;


public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private TextView mTvRegister;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        mEtUsername = (EditText) findViewById(R.id.etUsername);
        mEtPassword = (EditText) findViewById(R.id.etPassword);
        mBtnLogin = (Button) findViewById(R.id.btnLogin);
        mTvRegister = (TextView) findViewById(R.id.tvRegister);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mBtnLogin.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.tvRegister:
                goToRegister();
                break;
        }
    }

    private void login() {
        String username = mEtUsername.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            // The input are empty, show an alert
            AlertHelper.alert(this, getString(R.string.dialog_error_title),
                    getString(R.string.login_invalid_inputs_message));
        } else {
            showProgressBar();
            // Log the new user in Parse.com
            User.logInInBackground(username, password, new LogInCallback() {

                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    hideProgressBar();

                    if (e == null) {
                        // Logged in successfully
                        goToMain();
                    } else {
                        AlertHelper.alert(LoginActivity.this, getString(R.string.dialog_error_title),
                                e.getMessage());
                    }
                }
            });
        }
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void goToMain() {
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }

    private void goToRegister() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
