package com.algol.project.algolsfa.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.algol.project.algolsfa.helper.SQLiteHelper;
import com.algol.project.algolsfa.others.Constants;
import com.algol.project.algolsfa.R;
import com.algol.project.algolsfa.helper.AppUtility;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etUsername, etPassword;
    private ImageView ivPasswordVisibility;
    private boolean isPasswordVisible;
    private Button btnLogin, btnForgotPassword;
    private SharedPreferences sharedPreferences;
    private Context context;
    private SQLiteHelper dbHelper;
    private String username, password;

    public static final int LOCALLY_AUTHENTIC = 0, LOCALLY_UNAUTHENTIC = 1, LOCALLY_ANONYMOUS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar tbLogin = findViewById(R.id.tb_login);
        setSupportActionBar(tbLogin);
        ActionBar loginActionBar = getSupportActionBar();
        loginActionBar.setDisplayShowTitleEnabled(false);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
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

            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
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

            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
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

            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings:
                showSettingsDialog();
                return true;
            case R.id.item_about:
                showAboutTheAppDialog();
                return true;
            case R.id.item_help:
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void placeCredentials() {
        /* place the username in the username text field if available */
        sharedPreferences = context.getSharedPreferences(Constants.LOGIN_CRED_KEY, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getResources().getString(R.string.username), "");
        if (username.length() > 0) {
            etUsername.setText(username);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                // begin login process
                initiateLogin();
                break;
            case R.id.btn_forgot_password:
                // request for new password
                break;
            case R.id.iv_password_visibility:
                // show or hide password
                togglePasswordVisibility();
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void initiateLogin() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionRequired())
                requestLocationPermission();
            else
                processLogin();
        } else {
            processLogin();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void processLogin()
    /* * authenticates the user
       * It the database exists and it is for
    * if device is online,
        - it checks whether the db exists for the user. If it does, the privilege verification api is invoked.
        - if it doesn't, the login api is invoked.
      if device is offline,
        - it checks whether the db exists for the user. If it does, a.
        - if it doesn't, the authentication fails.
    *
    * */ {
        this.username = etUsername.getText().toString();
        this.password = etPassword.getText().toString();
        if (new File(Constants.databaseAbsolutePath).exists()) {
            dbHelper = SQLiteHelper.getHelper(context);
            if (dbHelper.isValidDB(AppUtility.getDeviceIMEI(context))) {
                int localAuthenticationStatus= dbHelper.getLocalAuthenticationStatus(username, password);
                if (localAuthenticationStatus == LOCALLY_AUTHENTIC) {
                    if (AppUtility.isAppOnline(context)) {
                        // verify privilege and login
                        login();
                    } else {
                        // show offline sign in alert and login
                        showLoginAlert(getResources().getString(R.string.login_alert_offline_signin));
                    }
                }
                else if(localAuthenticationStatus == LOCALLY_UNAUTHENTIC) {
                    showLoginAlert(getResources().getString(R.string.login_alert_invalid_password));
                }
                else if(localAuthenticationStatus == LOCALLY_ANONYMOUS) {
                    if (AppUtility.isAppOnline(context)) {
                        // logging in with different user. Invoke Login API
                    } else {
                        showLoginAlert(getResources().getString(R.string.login_alert_invalid_username));
                    }
                }
            } else {
                if (AppUtility.isAppOnline(context)) {
                    // download latest db
                } else {
                    showLoginAlert(getResources().getString(R.string.login_alert_invalid_db));
                }
            }
        } else {
            if (AppUtility.isAppOnline(context)) {
                // invoke login and download db
            } else {
                showLoginAlert(getResources().getString(R.string.login_alert_go_online_and_try));
            }
        }
    }

    private void showLoginAlert(String content) {
        SweetAlertDialog loginAlert = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        loginAlert.setCancelable(false);
        loginAlert.setContentText(content);
        loginAlert.setConfirmButton("Ok", SweetAlertDialog::dismissWithAnimation);
        loginAlert.showCancelButton(false);
        loginAlert.show();
        Button neutralButton = loginAlert.getButton(SweetAlertDialog.BUTTON_CONFIRM);
        neutralButton.setBackgroundResource(R.drawable.neutral_button_background);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void login() {
        sharedPreferences = context.getSharedPreferences(Constants.LOGIN_CRED_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getResources().getString(R.string.username), username);
        editor.putString(getResources().getString(R.string.password), password);
        editor.apply();
        Intent homeIntent = new Intent(context, HomeActivity.class);
        startActivity(homeIntent);
        finish();
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

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
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

    private void showSettingsDialog() {
        // show settings dialog
    }

    private void showAboutTheAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog);
        builder.setCancelable(true);
        builder.setItems(aboutItems(), null);
        AlertDialog aboutDialog = builder.create();
        aboutDialog.getListView().setEnabled(false);
        Window dialogWindow = aboutDialog.getWindow();
        LayoutParams dialogParams = dialogWindow.getAttributes();
        dialogParams.alpha = 0.80f;
        dialogWindow.setAttributes(dialogParams);
        aboutDialog.show();
    }

    private CharSequence[] aboutItems() {
        ArrayList<String> items = new ArrayList<>();
        String version = "Version : ";
        try {
            version += getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        items.add(version);
        items.add("Developed by : " + getResources().getString(R.string.developer_name));
        items.add("\u00A9 " + getResources().getString(R.string.copyright_year) + " " + getResources().getString(R.string.copyright_org));
        return items.toArray(new String[0]);
    }

    @Override
    public void onBackPressed() {
        AppUtility.minimizeApp(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean isPermissionRequired() {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE}, Constants.REQUEST_LOGIN_PERMISSION);
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
                if (!isPermissionReceived)
                    requestLocationPermission();
                else
                    processLogin();
                break;
            default:
                break;
        }
    }
}
