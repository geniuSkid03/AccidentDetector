package com.geniuskid.accidentdetector.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {

    public static final String KEY_NAME = "appname";
    public static final String KEY_ELAPS = "elapsedtime";
    private static final String TAG = "DbAdapter";
    private static final String DATABASE_NAME = "MyDB";
    private static final String DATABASE_TABLE = "appevoluationtable";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "create table appevoluationtable ( "
            + "appname text not null, elapsedtime text not null);";
    private Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DbAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
                System.out.println("successfully db created");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }


    //---opens the database---
    public DbAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    //---closes the database---
    public void close() {
        DBHelper.close();
    }


    //---insert a contact into the database---
    public long insertContact(String appname, String elapse) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, appname);
        initialValues.put(KEY_ELAPS, elapse);
        System.out.println("successfully db inserted" + appname + ":" + elapse);
        return db.insert(DATABASE_TABLE, null, initialValues);

    }

    public Cursor getAllContacts() {
        return db.query(DATABASE_TABLE, new String[]{KEY_NAME, KEY_ELAPS},
                null, null, null, null, null, null);
    }


    public void deleteTable() {
        // TODO Auto-generated method stub
        db.execSQL("delete from " + DATABASE_TABLE);
    }
}
