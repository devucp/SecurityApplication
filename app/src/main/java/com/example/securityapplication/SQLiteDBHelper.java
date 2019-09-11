package com.example.securityapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.securityapplication.model.User;

import static android.icu.text.MessagePattern.ArgType.SELECT;


public class SQLiteDBHelper extends SQLiteOpenHelper {

    private static final String DB_name = "info.db";
    private static final int DB_version = 1;

    private static final String TABLE_NAME = "user";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_MOBILE = "mobile";
    public static final String COLUMN_AADHAR = "aadhar";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_IMEI = "imei";

    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, "+
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_GENDER + "TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_MOBILE + " TEXT, " +
                    COLUMN_AADHAR + " TEXT, " +
                    COLUMN_LOCATION + " TEXT, " +
                    COLUMN_IMEI + " TEXT " + ")";


    /**Constructor*/

    public SQLiteDBHelper(Context context) {
        super(context, DB_name, null, DB_version);
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

    /**Adding user*/
    public boolean addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, user.getName());
        contentValues.put(COLUMN_EMAIL, user.getEmail());
        contentValues.put(COLUMN_PASSWORD, user.getPassword());
        contentValues.put(COLUMN_GENDER, user.getGender());
        contentValues.put(COLUMN_MOBILE,user.getMobile());
        contentValues.put(COLUMN_AADHAR, user.getAadhar());
        contentValues.put(COLUMN_LOCATION, user.getLocation());
        contentValues.put(COLUMN_IMEI, user.getImei());

        long result = db.insert(TABLE_NAME,null, contentValues);
        db.close();
        if (result == -1){
            Log.d("Database","User object NOT ADDED");
            return false;
        }
        else{
            Log.d("Database","User object added successfully");
            return true;
        }
    }

    /**Updating user*/
    public void updateUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, user.getName());
        contentValues.put(COLUMN_EMAIL, user.getEmail());
        contentValues.put(COLUMN_PASSWORD, user.getPassword());
        contentValues.put(COLUMN_GENDER, user.getGender());
        contentValues.put(COLUMN_MOBILE,user.getMobile());
        contentValues.put(COLUMN_AADHAR, user.getAadhar());
        contentValues.put(COLUMN_LOCATION, user.getLocation());
        contentValues.put(COLUMN_IMEI, user.getImei());

        db.update(TABLE_NAME,contentValues,COLUMN_ID + "=?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    /**Checking if user is present*/
    public boolean checkUser(String aadhar){
        String[] columns = {
                COLUMN_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_AADHAR + " = ?";
        String[] selectionArgs = {aadhar};

        Cursor cursor = db.query(TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if(cursorCount > 0){
            return false;
        }
        return true;

    }

    /**Delete User */
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        // delete user record by id
        db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }




}
