package com.example.securityapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.securityapplication.model.User;

import static android.icu.text.MessagePattern.ArgType.SELECT;


public class SQLiteDBHelper extends SQLiteOpenHelper {

    private static final String DB_name = "userinfo.db";
    private static final int DB_version = 1;

    private static final String TABLE_NAME = "user";

    //public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_MOBILE = "mobile";
//    public static final String COLUMN_AADHAR = "aadhar";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_IMEI = "imei";
    public static final String COLUMN_DOB = "dob";

    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    //COLUMN_ID + " INTEGER, " +
                    COLUMN_NAME + " TEXT, "+
                    COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                    COLUMN_GENDER + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_MOBILE + " TEXT, " +
//                    COLUMN_AADHAR + " TEXT, " +
                    COLUMN_LOCATION + " TEXT, " +
                    COLUMN_DOB + " TEXT, " +
                    COLUMN_IMEI + " TEXT " + ")";



    private static final String SOS_TABLE = "sostable";
    public static final String COLUMN_SOSID = "cid";
    public static final String COLUMN_SOSNUMBER = "sos_mobile";

    private static final String CREATE_SOSTABLE_QUERY =
            "CREATE TABLE "+ SOS_TABLE +"(" +
                    COLUMN_SOSID + " VARCHAR(2), " +
                    COLUMN_SOSNUMBER + " TEXT "+")";

    /**Constructor*/

    public SQLiteDBHelper(Context context) {
        super(context, DB_name, null, DB_version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("SQL Query","Create user table query is:"+CREATE_TABLE_QUERY);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);

        Log.d("SQL Query","Create sostable with query :"+CREATE_SOSTABLE_QUERY);
        sqLiteDatabase.execSQL(CREATE_SOSTABLE_QUERY);
        //sqLiteDatabase.execSQL("create table "+TABLE_NAME+" (id int , name varchar(20),location varchar(20),mobile char(10),aadhar char(12),imei varchar(10), gender Varchar(6), dob Varchar(8), email varchar(30) , password varchar(16))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /**Adding user*/
    public boolean addUser(User user){
        try{SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, user.getName());
        contentValues.put(COLUMN_EMAIL, user.getEmail());
        contentValues.put(COLUMN_PASSWORD, user.getPassword());
        contentValues.put(COLUMN_GENDER, user.getGender());
        contentValues.put(COLUMN_MOBILE,user.getMobile());
//        contentValues.put(COLUMN_AADHAR, user.getAadhar());
        contentValues.put(COLUMN_LOCATION, user.getLocation());
        contentValues.put(COLUMN_IMEI, user.getImei());
        contentValues.put(COLUMN_DOB, user.getDob()); //ADDED DOB
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
        }catch(Exception e){
            Log.d("SQL","Exception occurred");
            e.printStackTrace();
            return false;
        }
    }

//    public boolean addsosContacts(){
//        SQLiteDatabase db = this.getWritableDatabase();
//
//    }

    /**Updating user*/
    /*public void updateUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, user.getName());
        contentValues.put(COLUMN_EMAIL, user.getEmail());
        contentValues.put(COLUMN_PASSWORD, user.getPassword());
        contentValues.put(COLUMN_GENDER, user.getGender());
        contentValues.put(COLUMN_MOBILE,user.getMobile());
        contentValues.put(COLUMN_LOCATION, user.getLocation());
        contentValues.put(COLUMN_IMEI, user.getImei());
        contentValues.put(COLUMN_DOB, user.getDob()); //ADDED DOB

        db.update(TABLE_NAME,contentValues,COLUMN_EMAIL + "=?",
                new String[]{String.valueOf(user.getEmail())});
        db.close();
    }*/

    /**Checking if user is present*/
    public boolean checkUser(String mobile){
        String[] columns = {
                COLUMN_MOBILE   //NOTE:changed to column Email
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_MOBILE + " = ?";
        String[] selectionArgs = {mobile};

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
        Log.d("DATABASE","Cursor count for Phone number "+cursorCount);
        if(cursorCount == 0){
            return true;
        }
        Log.d("DATABASE","Phone no exists:"+mobile);
        return false;

    }

    /**Delete User */
    /*public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        // NOTE: delete user record by Aadhar
        db.delete(TABLE_NAME, COLUMN_AADHAR + " = ?",
                new String[]{String.valueOf(user.getAadhar())});
        db.close();
    }*/

    public Cursor getAllData() {
        Cursor cursor = getReadableDatabase().rawQuery("select * from "+TABLE_NAME, null);
        if (cursor.getCount()!=0) {
            Log.d("Database", "Details loaded in Cursor");
        }
        else {
            Log.d("Database","No records Found");
        }
        return cursor;
    }
}
