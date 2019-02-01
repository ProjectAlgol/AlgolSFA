package com.algol.project.algolsfa.others.fcm;

import android.content.SharedPreferences;
import android.util.Log;

import com.algol.project.algolsfa.R;
import com.algol.project.algolsfa.others.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by swarnavo.dutta on 1/31/2019.
 */

public class CustomFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG= "FCM_TEST";
    @Override
    public void onNewToken(String fcmToken) {
        Log.i(TAG,"Token: " + fcmToken);
        if(fcmToken != null && fcmToken.length() > 0) {
            SharedPreferences sharedPreferences= getApplicationContext().getSharedPreferences(Constants.FCM_TOKEN_KEY,MODE_PRIVATE);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putString(getString(R.string.fcm_token),fcmToken);
            editor.apply();
        }
        else {
            Log.i(TAG,"Failed to fetch FCM token");
        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage != null) {
            Map<String,String> messageData= remoteMessage.getData();
        }
    }
}
