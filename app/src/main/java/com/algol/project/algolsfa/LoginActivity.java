package com.algol.project.algolsfa;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algol.project.algolsfa.helper.AppUtility;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etUsername, etPassword;
    private ImageView ivMore, ivPasswordVisibility;
    private boolean isPasswordVisible;
    private Button btnLogin, btnForgotPassword;
    private SharedPreferences sharedPreferences;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        ivMore = findViewById(R.id.iv_more);
        ivMore.setOnClickListener(this);
        ivPasswordVisibility = findViewById(R.id.iv_password_visibility);
        ivPasswordVisibility.setOnClickListener(this);
        ivPasswordVisibility.setEnabled(false);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        btnLogin.setEnabled(false);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);
        btnForgotPassword.setOnClickListener(this);
        isPasswordVisible = false;
        context = LoginActivity.this;


        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPassword.getText().toString().trim().length() > 0) {
                    ivPasswordVisibility.setEnabled(true);
                    ivPasswordVisibility.setAlpha(1.00f);
                } else {
                    ivPasswordVisibility.setEnabled(false);
                    ivPasswordVisibility.setAlpha(0.25f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                toggleLoginButtonActivation();
            }
        });

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                toggleLoginButtonActivation();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        placeCredentials();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        placeCredentials();
    }

    private void placeCredentials() {
        /* place the username in the username text field if available */
        sharedPreferences = context.getSharedPreferences(Constants.LOGIN_CRED_KEY, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getResources().getString(R.string.username), "");
        if (username.length() > 0) {
            etUsername.setText(username);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                // begin login process
                login();
                break;
            case R.id.btn_forgot_password:
                // request for new password
                break;
            case R.id.iv_password_visibility:
                // show or hide password
                togglePasswordVisibility();
                break;
            case R.id.iv_more:
                // open more dialog
                openMoreOptionDialog();
                break;
            default:
                break;
        }
    }

    private void login() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(isPermissionRequired())
                requestLocationPermission();
            else
                processLogin();
        }
        else {
            processLogin();
        }
    }

    private void processLogin() {
        Toast.makeText(context,"Hang on... Logging you in",Toast.LENGTH_SHORT).show();
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // hiding password
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
            ivPasswordVisibility.setImageDrawable(getResources().getDrawable(R.drawable.show_password));
            isPasswordVisible = false;
        } else {
            // showing password
            etPassword.setTransformationMethod(null);
            ivPasswordVisibility.setImageDrawable(getResources().getDrawable(R.drawable.hide_password));
            isPasswordVisible = true;
        }
    }

    private void toggleLoginButtonActivation() {
        if (etUsername.getText().toString().trim().length() > 0 && etPassword.getText().toString().trim().length() > 0) {
            // enabling login
            btnLogin.setEnabled(true);
            btnLogin.setAlpha(1.00f);
        } else {
            // disabling login
            btnLogin.setEnabled(false);
            btnLogin.setAlpha(0.25f);
        }
    }

    private void openMoreOptionDialog() {
        Dialog moreOptionDialog = new Dialog(context);
        moreOptionDialog.setContentView(R.layout.dialog_more_options);
        moreOptionDialog.show();
    }

    @Override
    public void onBackPressed() {
        new AppUtility(context).minimizeApp();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean isPermissionRequired() {
        return (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context,Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_LOGIN_PERMISSION);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isPermissionReceived = true;
        switch (requestCode) {
            case Constants.REQUEST_LOGIN_PERMISSION:
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        isPermissionReceived = false;
                        break;
                    }
                }
                if(!isPermissionReceived)
                    requestLocationPermission();
                else
                    processLogin();
                break;
            default:
                break;
        }
    }
}
