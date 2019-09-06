package com.example.securityapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static final String DB_Name = "info.db";
    private static final int DB_version =1;

    public static final String TABLE_NAME = "profile";
    public  static final String COLUMN_ID = "user_id";
    public  static final String COLUMN_NAME = "fullName";
    public  static final String COLUMN_EMAIL = "email";
    public  static final String COLUMN_PASSWORD = "password";
    private  static final String COLUMN_AADHAR_UID = "aadhar_uid";
    public  static final String COLUMN_LOC = "location";
    public  static final String COLUMN_MOBILE = "mobile";
    private static final String COLUMN_IMEI = "imei";

    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, "+
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_AADHAR_UID + " TEXT, " +
                    COLUMN_LOC + " TEXT, " +
                    COLUMN_MOBILE + " TEXT, " +
                    COLUMN_IMEI + " TEXT " + ")";


    public SQLiteDBHelper(Context context) {
        super(context, DB_Name, null, DB_version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //Insertion
    public boolean insertData(String fullName,String email,String password, String aadhar_uid,
                              String location, String mobile, String imei)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME,fullName);
        contentValues.put(COLUMN_EMAIL,email);
        contentValues.put(COLUMN_PASSWORD,password);
        contentValues.put(COLUMN_AADHAR_UID,aadhar_uid);
        contentValues.put(COLUMN_LOC,location);
        contentValues.put(COLUMN_MOBILE,mobile);
        contentValues.put(COLUMN_IMEI,imei);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    //Fetching all data
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }


}
