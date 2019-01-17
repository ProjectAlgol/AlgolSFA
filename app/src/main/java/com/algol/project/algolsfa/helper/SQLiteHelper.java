/*
* SQLiteHelper is a singleton class responsible to manage and handle the database calls. In other words, it helps to read from and write into the database.
* */
package com.algol.project.algolsfa.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.algol.project.algolsfa.activities.LoginActivity;
import com.algol.project.algolsfa.others.Constants;
import com.algol.project.algolsfa.others.Constants.DBRelation;

/**
 * Created by swarnavo.dutta on 1/15/2019.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    private static SQLiteHelper dbHelper = null;

    private SQLiteHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public static SQLiteHelper getHelper(Context context)
    /*
    * provides SQLiteHelper instance
    * */ {
        if (dbHelper == null) {
            dbHelper = new SQLiteHelper(context, Constants.databaseAbsolutePath, Constants.databaseVersion);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getLocalAuthenticationStatus(String username, String password, String deviceIMEI)
    /*
    * authenticate the user locally using username, password and IMEI present in the local database
    * */ {
        SQLiteDatabase db = getReadableDatabase();
        String query = "Select Password, DeviceIMEI from " + DBRelation.SmanMaster + " where UserId= ?";
        try {
            Cursor resultSet = db.rawQuery(query, new String[]{username});
            if (resultSet.getCount() > 0) {
                resultSet.moveToFirst();
                String dbPassword= resultSet.getString(resultSet.getColumnIndex("Password"));
                String dbIMEI= resultSet.getString(resultSet.getColumnIndex("DeviceIMEI"));
                resultSet.close();
                if (dbPassword.equalsIgnoreCase(password) && dbIMEI.equalsIgnoreCase(deviceIMEI))
                    return LoginActivity.LOCALLY_AUTHENTIC;
                else if(!dbIMEI.equalsIgnoreCase(deviceIMEI))
                    return LoginActivity.LOCALLY_UNAUTHORIZED;
                else
                    return LoginActivity.LOCALLY_UNAUTHENTIC;

            } else {
                return LoginActivity.LOCALLY_ANONYMOUS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return LoginActivity.LOCALLY_ANONYMOUS;
        }
    }

    public boolean isValidDB()
    /*
    * checks whether the database is valid based on the DB Status, Db Validity
    * */ {
        SQLiteDatabase db = getReadableDatabase();
        String query = "select Module, Value from " + DBRelation.SettingsMaster + " where Module in ('DBStatus','DBValidity')";
        try {
            Cursor resultSet = db.rawQuery(query, null);
            if (resultSet.getCount() > 0) {
                resultSet.moveToFirst();
                if (resultSet.getString(resultSet.getColumnIndex("Value")).equalsIgnoreCase("Yes")) {
                    resultSet.moveToNext();
                    String dbValidityPeriod = resultSet.getString(resultSet.getColumnIndex("Value"));
                    resultSet.close();
                    query = "select case when (((select datetime('now','localtime')) >= (select DBCreationDateTime from " + DBRelation.SmanMaster + ")) and ((select datetime('now','localtime')) <= (select datetime((select strftime('%Y-%m-%d 23:59:59',(select DBCreationDateTime from " + DBRelation.SmanMaster + "))),'+" + dbValidityPeriod + " day')))) then 'Valid' else 'Invalid' end as Status";
                    resultSet = db.rawQuery(query,null);
                    if (resultSet.getCount() > 0) {
                        resultSet.moveToFirst();
                        return (resultSet.getString(resultSet.getColumnIndex("Status")).equalsIgnoreCase("Valid"));
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
