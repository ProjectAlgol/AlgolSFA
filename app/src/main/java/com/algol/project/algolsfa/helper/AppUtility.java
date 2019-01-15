/*
* This class is intended for several app regarding utilities. Instantiate the class with context object as constructor parameter
* */

package com.algol.project.algolsfa.helper;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Lykos on 01-Jan-19.
 */

public class AppUtility {
    private Context context;

    public AppUtility(Context context) {
        this.context = context;
    }

    public static void minimizeApp(Context context) {
        Intent intentMinimize = new Intent(Intent.ACTION_MAIN);
        intentMinimize.addCategory(Intent.CATEGORY_HOME);
        intentMinimize.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentMinimize);
    }

    public static boolean isAppOnline(Context context) {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
