package com.algol.project.algolsfa.activities;

import android.Manifest;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algol.project.algolsfa.async.AsyncDownloader;
import com.algol.project.algolsfa.helper.SQLiteHelper;
import com.algol.project.algolsfa.interfaces.APIInvocationListener;
import com.algol.project.algolsfa.interfaces.DownloadListener;
import com.algol.project.algolsfa.others.Constants;
import com.algol.project.algolsfa.R;
import com.algol.project.algolsfa.helper.AppUtility;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, DownloadListener, APIInvocationListener {
    private EditText etUsername, etPassword;
    private ImageView ivPasswordVisibility;
    private boolean isPasswordVisible;
    private Button btnLogin, btnForgotPassword;
    private SharedPreferences sharedPreferences;
    private Context context;
    private SQLiteHelper dbHelper;
    private String username, password;
    private ProgressBar loginProgressBar;
    private TextView tvProgressBarDescription, tvProgress;
    private View layoutLoginProgressBar;
    private static final String TAG= "LOGIN_LOG";

    public static final int LOCALLY_AUTHENTIC = 0, LOCALLY_UNAUTHENTIC = 1, LOCALLY_UNAUTHORIZED = 2, LOCALLY_ANONYMOUS = 3;

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

        // progress bar objects
        layoutLoginProgressBar= findViewById(R.id.layout_progressbar_login);
        loginProgressBar = findViewById(R.id.progressbar);
        tvProgressBarDescription= findViewById(R.id.tv_progress_description);
        tvProgress= findViewById(R.id.tv_progress);

        if(!isFCMTokenAvailable()) {
            fetchFCMToken();
        }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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


    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processLogin()
    /* * checks the existence of the db and authenticates the user
       * If the database exists, it tries to authenticate the user locally.
            - For locally authentic user, if the DB is valid and the device is online, the privilege verification API is invoked and user gets logged in after successful DB download. If the device is offline, the user gets logged in offline mode. For an invalid DB, if the device is online, the user is asked to download the updated DB. If the device is offline, it simply prompts 'Invalid Database' message
            - For locally unauthentic user, the login fails
            - For user whose username doesn't match locally, a fresh login is performed (if the device is online) and for successful login the previous db gets overwritten.
       * If the DB doesn't exist in the first place and the device is online, the login API is invoked and upon successful login the DB is downloaded
    * */ {
        this.username = etUsername.getText().toString();
        this.password = etPassword.getText().toString();
        if (new File(Constants.databaseAbsolutePath).exists()) {
            dbHelper = SQLiteHelper.getHelper(context);
            int localAuthenticationStatus = dbHelper.getLocalAuthenticationStatus(username, password, AppUtility.getDeviceIMEI(context));
            if (localAuthenticationStatus == LOCALLY_AUTHENTIC) {
                if (dbHelper.isValidDB()) {
                    if (AppUtility.isAppOnline(context)) {
                        // verify privilege and login
                        login();
                    } else {
                        // prompt offline sign in alert and login
                        showLoginAlert(getResources().getString(R.string.login_alert_offline_signin), SweetAlertDialog.SUCCESS_TYPE);
                    }
                } else {
                    if (AppUtility.isAppOnline(context)) {
                        // prompt invalid DB and invoke login API
                        showLoginAlert(getResources().getString(R.string.login_alert_invalid_db_online), SweetAlertDialog.WARNING_TYPE);
                    } else {
                        showLoginAlert(getResources().getString(R.string.login_alert_invalid_db_offline), SweetAlertDialog.WARNING_TYPE);
                    }
                }
            } else if (localAuthenticationStatus == LOCALLY_UNAUTHENTIC) {
                showLoginAlert(getResources().getString(R.string.login_alert_invalid_password), SweetAlertDialog.ERROR_TYPE);
            } else if (localAuthenticationStatus == LOCALLY_UNAUTHORIZED) {
                showLoginAlert(getResources().getString(R.string.login_alert_imei_mismatch), SweetAlertDialog.ERROR_TYPE);
            } else if (localAuthenticationStatus == LOCALLY_ANONYMOUS) {
                if (AppUtility.isAppOnline(context)) {
                    // logging in with different user. Invoke Login API
                    Toast.makeText(context, "Hang on, Trying to login with different user...", Toast.LENGTH_SHORT).show();
                    downloadDatabase();
                } else {
                    showLoginAlert(getResources().getString(R.string.login_alert_invalid_username), SweetAlertDialog.ERROR_TYPE);
                }
            }
        } else {
            if (AppUtility.isAppOnline(context)) {
                Toast.makeText(context, "Database ain't available. Hang on while trying to log in...", Toast.LENGTH_SHORT).show();
                downloadDatabase();
            } else {
                showLoginAlert(getResources().getString(R.string.login_alert_go_online_and_try), SweetAlertDialog.WARNING_TYPE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showLoginAlert(String content, int alertType) {
        SweetAlertDialog loginAlert = new SweetAlertDialog(context, alertType);
        loginAlert.setCancelable(false);
        if (!AppUtility.isAppOnline(context))
            loginAlert.setTitleText("Offline Login");
        loginAlert.setContentText(content);
        if (content.equalsIgnoreCase(getResources().getString(R.string.login_alert_invalid_db_online))) {
            loginAlert.setConfirmButton("Yes", sweetAlertDialog -> {
                sweetAlertDialog.dismissWithAnimation();
                downloadDatabase();
            });
            loginAlert.setCancelButton("Not now", SweetAlertDialog::dismissWithAnimation);
        } else {
            loginAlert.setConfirmButton("Ok", sweetAlertDialog -> {
                if (content.equalsIgnoreCase(getResources().getString(R.string.login_alert_offline_signin))) {
                    login();
                } else {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
            loginAlert.showCancelButton(false);
        }
        loginAlert.show();
        if (content.equalsIgnoreCase(getResources().getString(R.string.login_alert_invalid_db_online))) {
            loginAlert.getButton(SweetAlertDialog.BUTTON_CONFIRM).setBackgroundResource(R.drawable.confirm_button_background);
            loginAlert.getButton(SweetAlertDialog.BUTTON_CANCEL).setBackgroundResource(R.drawable.cancel_button_background);
        } else {
            Button neutralButton = loginAlert.getButton(SweetAlertDialog.BUTTON_CONFIRM);
            neutralButton.setBackgroundResource(R.drawable.neutral_button_background);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void downloadDatabase() {
        // setting up progress bar
        tvProgressBarDescription.setText(getResources().getString(R.string.connecting_server));
        loginProgressBar.setVisibility(View.GONE);
        tvProgress.setVisibility(View.GONE);
        layoutLoginProgressBar.setVisibility(View.VISIBLE);

        File dbFolder = new File(Constants.databaseFolder);
        if (!dbFolder.exists())
            dbFolder.mkdir();
        AsyncDownloader asyncDownloader= new AsyncDownloader(context,Constants.FILE_DB,this);
        tvProgressBarDescription.setText(getResources().getString(R.string.downloading_db));
        loginProgressBar.setVisibility(View.VISIBLE);
        tvProgress.setVisibility(View.VISIBLE);
        asyncDownloader.execute(Constants.databaseURL,Constants.databaseAbsolutePath);
    }

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

    private boolean isPermissionRequired() {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE}, Constants.REQUEST_LOGIN_PERMISSION);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @Override
    public void onDownloadComplete(String fileType)
    /*
    * invoked when the file is successfully downloaded.
    * */
    {
        layoutLoginProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onDownloadFailed(String fileType, int error)
    /*
    * invoked when the file download fails
    * */
    {
        layoutLoginProgressBar.setVisibility(View.GONE);
        String message;
        switch (error) {
            case Constants.ERROR_BAD_NETWORK:
                message = "Bad Network";
                break;
            case Constants.ERROR_BAD_SERVER:
                message = "Bad Server";
                break;
            case Constants.ERROR_CONNECTION_TIME_OUT:
                message = "Connection Timeout";
                break;
            case Constants.ERROR_INVALID_FILE_DESTINATION:
                message = "Invalid File Destination";
                break;
            case Constants.ERROR_INVALID_URL:
                message = "Invalid URL";
                break;
            case Constants.ERROR_SERVER_RESPONSE:
                message = "Error in Server Response";
                break;
            case Constants.ERROR_UNEXPECTED:
                message = "Unexpected Error Occurred";
                break;
            default:
                message = "Unknown Error Occurred";
                break;
        }
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        File incompleteDB = new File(Constants.databaseAbsolutePath);
        if (incompleteDB.exists())
            incompleteDB.delete();
    }

    private boolean isFCMTokenAvailable() {
        sharedPreferences= context.getSharedPreferences(Constants.FCM_TOKEN_KEY,MODE_PRIVATE);
        String fcmToken= sharedPreferences.getString(getString(R.string.fcm_token),"");
        return (fcmToken.length() > 0);
    }

    private void fetchFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                task.getResult().getToken();
            }
            else {
                Log.i(TAG,task.getException().toString());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onProgressUpdate(String fileType, int progress) {
        loginProgressBar.setProgress(progress,true);
        tvProgress.setText(progress + " of 100");
    }

    @Override
    public void onResponseSuccess(String api, Object response) {

    }

    @Override
    public void onResponseFailure(int error) {

    }
}
