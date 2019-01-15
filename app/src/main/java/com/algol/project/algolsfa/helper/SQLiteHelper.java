/*
* SQLiteHelper is a singleton class responsible to manage and handle the database calls. In other words, it helps to read from and write into the database.
* */
package com.algol.project.algolsfa.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public boolean isLocallyAuthentic(String username, String password)
    /*
    * authenticate the user locally using username and password present in the local database
    * */
    {
        SQLiteDatabase db= getReadableDatabase();
        String query= "Select UserId, Password from " + DBRelation.SmanMaster;
        try {
            Cursor resultSet= db.rawQuery(query,null);
            if(resultSet.getCount() > 0) {
                resultSet.moveToFirst();
                return (resultSet.getString(resultSet.getColumnIndex("UserId")).equalsIgnoreCase(username) && resultSet.getString(resultSet.getColumnIndex("Password")).equalsIgnoreCase(password));
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
