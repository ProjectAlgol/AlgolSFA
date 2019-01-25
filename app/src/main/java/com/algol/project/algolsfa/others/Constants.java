package com.algol.project.algolsfa.others;

import android.content.Context;

import com.algol.project.algolsfa.BuildConfig;
import com.algol.project.algolsfa.R;

import java.io.File;

/**
 * Created by Lykos on 30-Dec-18.
 */

public class Constants {
    private static final String keyPrefix= BuildConfig.APPLICATION_ID + ".";
    public static final String LOGIN_CRED_KEY= keyPrefix + "LoginCredentials";
    public static String databaseAbsolutePath= null, databaseFolder= null;
    public static int databaseVersion;
    //public static String databaseURL= "https://mystuffs.000webhostapp.com/SQLite/algolsfa.db";
    public static String databaseURL= "http://dev.gobizmo.in/Saleslite/SalesliteAX/AXTransactionAPI/SQLite/13878/coke.DB";
    // permission codes
    public static final int REQUEST_LOGIN_PERMISSION= 1; // Permissions: Location, External drive read and write
    public static final int REQUEST_CAMERA_PERMISSION= 2; // Permissions: Image Capture, Bar code and QR code scan

    // file types
    public static final String FILE_DB= "Database", FILE_APP= "Application";

    // privilege purposes
    public enum UserPrivilege {
        OrderAndVisit("Order and Visit"), Delivery("Delivery"), Analytics("Analytics"), PlannedVisit("Planned Visit"), UnplannedVisit("Unplanned Visit"), NewOutletAddition("New Outlet Addition"), MultiSurvey("Multi Survey");
        private String action;
        UserPrivilege(String action) {
            this.action= action;
        }
        public String getAction() {
            return action;
        }
    }

    // database tables
    public enum DBRelation {
        PrivilegeMaster("PrivilegeMaster"), SmanMaster("SmanMaster"), SettingsMaster("SettingsMaster");
        private String action;
        DBRelation(String action) {
            this.action= action;
        }
        public String getAction() {
            return action;
        }
    }

    public static void init(Context context)
    /*
    * initializes the context specific constant values
    * */
    {
        databaseFolder= android.os.Environment.getExternalStorageDirectory() + File.separator + context.getResources().getString(R.string.database_dir) + File.separator;
        databaseAbsolutePath = databaseFolder + context.getResources().getString(R.string.database_name);
        databaseVersion= context.getResources().getInteger(R.integer.database_version);
    }
}
