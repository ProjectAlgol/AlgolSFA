package com.algol.project.algolsfa.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.algol.project.algolsfa.helper.AppUtility;
import com.algol.project.algolsfa.helper.SQLiteHelper;
import com.algol.project.algolsfa.others.Constants;
import com.algol.project.algolsfa.R;

import java.io.File;

/**
 * Created by Lykos on 30-Dec-18.
 */

public class SplashActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = SplashActivity.this;
        new Handler().postDelayed(this::resumeApp, 4000);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void resumeApp() {
        Constants.init(context);
        Intent loginIntent = new Intent(context, LoginActivity.class);
        Intent homeIntent = new Intent(context, HomeActivity.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.LOGIN_CRED_KEY, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(getResources().getString(R.string.password), "").length() > 0) {
            if (new File(Constants.databaseAbsolutePath).exists()) {
                SQLiteHelper dbHelper = SQLiteHelper.getHelper(context);
                int localAuthenticationStatus = dbHelper.getLocalAuthenticationStatus(sharedPreferences.getString(getResources().getString(R.string.username), ""), sharedPreferences.getString(getResources().getString(R.string.password), ""), AppUtility.getDeviceIMEI(context));
                if (localAuthenticationStatus == LoginActivity.LOCALLY_AUTHENTIC) {
                    if (dbHelper.isValidDB()) {
                        startActivity(homeIntent);
                    } else {
                        startActivity(loginIntent);
                    }
                } else {
                    startActivity(loginIntent);
                }
            } else {
                startActivity(loginIntent);
            }
        } else {
            startActivity(loginIntent);
        }
        finish();
    }
}
