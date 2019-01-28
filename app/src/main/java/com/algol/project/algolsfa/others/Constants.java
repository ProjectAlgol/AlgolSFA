package com.algol.project.algolsfa.others;

import android.content.Context;

import com.algol.project.algolsfa.BuildConfig;
import com.algol.project.algolsfa.R;

import java.io.File;

/**
 * Created by Lykos on 30-Dec-18.
 */

public class Constants {
    private static final String keyPrefix = BuildConfig.APPLICATION_ID + ".";
    public static final String LOGIN_CRED_KEY = keyPrefix + "LoginCredentials";
    public static String databaseAbsolutePath = null, databaseFolder = null;
    public static int databaseVersion;
    //public static String databaseURL= "https://mystuffs.000webhostapp.com/SQLite/Shona.JPG";
    public static String testFilePath = null;
    public static String databaseURL = "https://mystuffs.000webhostapp.com/SQLite/algolsfa.db";
    public static String apiBase= "http://dev.gobizmo.in/Saleslite/SalesliteAX/AXTransactionAPI/API/";
    public static long downloadBufferSize = 10240; // 10 KB

    /* Custom Errors & success codes for File Download and API calls*/
    public static final int ERROR_INVALID_URL = 0, ERROR_BAD_SERVER = 1, ERROR_BAD_NETWORK = 2, ERROR_UNEXPECTED = 3, ERROR_CONNECTION_TIME_OUT = 4, ERROR_INVALID_FILE_DESTINATION = 5, ERROR_SERVER_RESPONSE = 6;
    public static final int DOWNLOAD_SUCCESS = 7, API_SUCCESS = 8;

    // permission codes
    public static final int REQUEST_LOGIN_PERMISSION = 1; // Permissions: Location, External drive read and write
    public static final int REQUEST_CAMERA_PERMISSION = 2; // Permissions: Image Capture, Bar code and QR code scan

    // file types
    public static final String FILE_DB = "Database", FILE_APP = "Application";

    // privilege purposes
    public enum UserPrivilege {
        OrderAndVisit("Order and Visit"),
        Delivery("Delivery"),
        Analytics("Analytics"),
        PlannedVisit("Planned Visit"),
        UnplannedVisit("Unplanned Visit"),
        NewOutletAddition("New Outlet Addition"),
        MultiSurvey("Multi Survey");
        private String value;

        UserPrivilege(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // database tables
    public enum DBRelation {
        PrivilegeMaster("PrivilegeMaster"),
        SmanMaster("SmanMaster"),
        SettingsMaster("SettingsMaster");
        private String name;

        DBRelation(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // APIs
    public enum API {
        Login(""),
        VerifyPrivilege(""),
        PlaceOrder(""),
        SubmitStock(""),
        SubmitInvoice(""),
        SubmitGeoCode(""),
        SubmitUserLocationPeriodically("");
        private String url;

        API(String url) {
            this.url = url;
        }

        public String getURL() {
            return url;
        }
    }

    public static void init(Context context)
    /*
    * initializes the context specific constant values
    * */ {
        databaseFolder = android.os.Environment.getExternalStorageDirectory() + File.separator + context.getResources().getString(R.string.database_dir) + File.separator;
        databaseAbsolutePath = databaseFolder + context.getResources().getString(R.string.database_name);
        databaseVersion = context.getResources().getInteger(R.integer.database_version);
        testFilePath = android.os.Environment.getExternalStorageDirectory() + File.separator + context.getResources().getString(R.string.database_dir) + File.separator + "Shona.jpg";
    }
}
