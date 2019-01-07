package com.algol.project.algolsfa.others;

import com.algol.project.algolsfa.BuildConfig;

/**
 * Created by Lykos on 30-Dec-18.
 */

public class Constants {
    private static String keyPrefix= BuildConfig.APPLICATION_ID + ".";
    public static final String LOGIN_CRED_KEY= keyPrefix + "LoginCredentials";

    // permission codes
    public static final int REQUEST_LOGIN_PERMISSION= 1; // Permissions: Location, External drive read and write
    public static final int REQUEST_CAMERA_PERMISSION= 2; // Permissions: Image Capture, Bar code and QR code scan
}