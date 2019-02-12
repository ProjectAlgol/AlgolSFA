package com.algol.project.algolsfa.helper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.algol.project.algolsfa.activities.LoginActivity;
import com.algol.project.algolsfa.others.Constants;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by swarnavo.dutta on 2/12/2019.
 */

public class SecureSQLiteHelper extends SQLiteOpenHelper {
    private static SecureSQLiteHelper dbHelper = null;
    public static final String TAG= "EncTest";
    private Context context;

    private SecureSQLiteHelper(Context context, String name, int version) {
        super(context, name, null, version);
        this.context= context;
    }

    public static SecureSQLiteHelper getHelper(Context context)
    /*
    * provides SQLiteHelper instance
    * */ {
        if (dbHelper == null) {
            dbHelper = new SecureSQLiteHelper(context, Constants.databaseAbsolutePath, Constants.databaseVersion);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(db.isOpen())
            Log.i(TAG,"Opened");
        else
            Log.i(TAG,"Not Opened");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getLocalAuthenticationStatus(String username, String password, String deviceIMEI)
    /*
    * authenticate the user locally using username, password and IMEI present in the local database
    * */ {
        SQLiteDatabase.loadLibs(context);
        SQLiteDatabase db= SQLiteDatabase.openOrCreateDatabase(new File(Constants.databaseAbsolutePath),"Hola Oso",null);
        String query = "Select Password, DeviceIMEI from " + Constants.DBRelation.SmanMaster.getName() + " where UserId= ?";
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
        SQLiteDatabase.loadLibs(context);
        SQLiteDatabase db= SQLiteDatabase.openOrCreateDatabase(new File(Constants.databaseAbsolutePath),"Hola Oso",null);
        String query = "select Module, Value from " + Constants.DBRelation.SettingsMaster.getName() + " where Module in ('DBStatus','DBValidity')";
        try {
            Cursor resultSet = db.rawQuery(query, null);
            if (resultSet.getCount() > 0) {
                resultSet.moveToFirst();
                if (resultSet.getString(resultSet.getColumnIndex("Value")).equalsIgnoreCase("Yes")) {
                    resultSet.moveToNext();
                    String dbValidityPeriod = resultSet.getString(resultSet.getColumnIndex("Value"));
                    resultSet.close();
                    query = "select case when (((select datetime('now','localtime')) >= (select DBCreationDateTime from " + Constants.DBRelation.SmanMaster.getName() + ")) and ((select datetime('now','localtime')) <= (select datetime((select strftime('%Y-%m-%d 23:59:59',(select DBCreationDateTime from " + Constants.DBRelation.SmanMaster.getName() + "))),'+" + dbValidityPeriod + " day')))) then 'Valid' else 'Invalid' end as Status";
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

    public boolean isPrivileged(String module)
    /*
    * checks whether the user has privilege for the provided module
    * */
    {
        SQLiteDatabase.loadLibs(context);
        SQLiteDatabase db= SQLiteDatabase.openOrCreateDatabase(new File(Constants.databaseAbsolutePath),"Hola Oso",null);
        String query = "select Status from " + Constants.DBRelation.PrivilegeMaster.getName() + " where Module = '" + module + "'";
        try {
            Cursor resultSet = db.rawQuery(query, null);
            if (resultSet.getCount() > 0) {
                resultSet.moveToFirst();
                return (resultSet.getString(resultSet.getColumnIndex("Status")).equalsIgnoreCase("yes"));
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}