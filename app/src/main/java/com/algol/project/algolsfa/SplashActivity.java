package com.algol.project.algolsfa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Lykos on 30-Dec-18.
 */

public class SplashActivity extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context= SplashActivity.this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resumeApp();
            }
        },4000);
    }

    private void resumeApp() {
        SharedPreferences sharedPreferences= context.getSharedPreferences(Constants.LOGIN_CRED_KEY,Context.MODE_PRIVATE);
        String password= sharedPreferences.getString(getResources().getString(R.string.password),"");
        if(password.length() > 0) {
            // go to home activity
        }
        else {
            Intent loginIntent= new Intent(context,LoginActivity.class);
            startActivity(loginIntent);
        }
    }
}
