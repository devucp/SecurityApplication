package com.example.securityapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.securityapplication.model.User;

import static android.icu.text.MessagePattern.ArgType.SELECT;


public class SQLiteDBHelper extends SQLiteOpenHelper {

    private static final String DB_name = "userinfo.db";
    private static final int DB_version = 1;
    User newU;
    private static final String TABLE_NAME = "user";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_MOBILE = "mobile";
//    public static final String COLUMN_AADHAR = "aadhar";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_IMEI = "imei";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_TESTM = "testmode";

    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER DEFAULT 1 , " +
                    COLUMN_NAME + " TEXT, "+
                    COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                    COLUMN_GENDER + " TEXT, " +
//                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_MOBILE + " TEXT, " +
//                    COLUMN_AADHAR + " TEXT, " +
                    COLUMN_LOCATION + " TEXT, " +
                    COLUMN_DOB + " TEXT, " +
                    COLUMN_IMEI + " TEXT, " +
                    COLUMN_TESTM + " BOOLEAN DEFAULT FALSE " + ")";



    private static final String SOS_TABLE = "sostable";
    public static final String COLUMN_C1 = "c1";
    public static final String COLUMN_C2 = "c2";
    public static final String COLUMN_C3 = "c3";
    public static final String COLUMN_C4 = "c4";
    public static final String COLUMN_C5 = "c5";


    private static final String CREATE_SOSTABLE_QUERY =
            "CREATE TABLE "+ SOS_TABLE +"(" +
                    COLUMN_C1 + " TEXT DEFAULT '', " +
                    COLUMN_C2 + " TEXT DEFAULT '',"+
                    COLUMN_C3 + " TEXT DEFAULT ''," +
                    COLUMN_C4 + " TEXT DEFAULT ''," +
                    COLUMN_C5 + " TEXT DEFAULT ''" +")";
    private User user;


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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SOS_TABLE);

        onCreate(sqLiteDatabase);
    }
    //it return number of rows in the table
    public int numberOfRows(){
        SQLiteDatabase db=this.getReadableDatabase();
        int numRows= (int) DatabaseUtils.queryNumEntries(db,TABLE_NAME);
        return numRows;

    }

    /**Adding user*/
    public boolean addUser(User user){
        newU=user;
        try{SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, user.getName());
        contentValues.put(COLUMN_EMAIL, user.getEmail());
//        contentValues.put(COLUMN_PASSWORD, user.getPassword());
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
            int k=numberOfRows();
            Log.d("NUMBER OF ROWS",String.valueOf(k));
            return true;
            }
        }catch(Exception e){
            Log.d("SQL","Exception occurred");
            e.printStackTrace();
            return false;
        }
    }
    public boolean addsosContacts(User user){
        SQLiteDatabase db = this.getWritableDatabase();
       ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_C1, user.getSosc1());
        contentValues.put(COLUMN_C2, user.getSosc2());
        contentValues.put(COLUMN_C3, user.getSosc3());
        contentValues.put(COLUMN_C4, user.getSosc4());
        contentValues.put(COLUMN_C5, user.getSosc5());

        long result = db.insert(SOS_TABLE,null,contentValues);
        db.close();
        if (result == -1){
            Log.d("SOS_Database","SOS contacts not added");
            return false;
        }
        else{
            Log.d("SOS_Database","SOS contacts added successfully");
            return true;
        }
    }

    public Cursor getSosContacts(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+SOS_TABLE, null);
        if (cursor.getCount()!=0) {
            Log.d("Database", "Sos Contact Details loaded in Cursor");
        }
        else {
            Log.d("Database","No Sos contact records Found");
        }
        return cursor;
    }


//    public boolean addsosContacts(){
//        SQLiteDatabase db = this.getWritableDatabase();
//
//    }

    //TO UPDATE TESTMODE
    public void updatetestmode(Boolean bool){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_TESTM,bool);
        db.update(TABLE_NAME,contentValues,COLUMN_ID + "=?",
                new String[]{"1"});
        Log.d("checking3","indide updatetestmode"+this.getTestmode());
        db.close();
    }
    /**Updating user*/
    public void updateUser(User user){
        newU=user;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, user.getName());
        contentValues.put(COLUMN_EMAIL, user.getEmail());
//        contentValues.put(COLUMN_PASSWORD, user.getPassword());
//        contentValues.put(COLUMN_GENDER, user.getGender());
        contentValues.put(COLUMN_MOBILE,user.getMobile());
        contentValues.put(COLUMN_LOCATION, user.getLocation());
//        contentValues.put(COLUMN_IMEI, user.getImei());
//        contentValues.put(COLUMN_DOB, user.getDob()); //ADDED DOB

        db.update(TABLE_NAME,contentValues,COLUMN_EMAIL + "=?",
                new String[]{String.valueOf(user.getEmail())});
        db.close();
    }

    /**Checking if user is present*/
    public boolean getTestmode(){
        String str="false";
        Cursor cursor = getReadableDatabase().rawQuery("select "+ COLUMN_TESTM +" FROM "+TABLE_NAME, null);
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                str = cursor.getString(0);
            }
            if(str.equals("1"))
                return true;
        }
        Log.d("checking4",str);
        return false;
    }

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

    //Fetch data for ProfileActivity
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

    /**Delete User */

    /*public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        // NOTE: delete user record by Aadhar
        db.delete(TABLE_NAME, COLUMN_AADHAR + " = ?",
                new String[]{String.valueOf(user.getAadhar())});
        db.close();
    }*/


    //Sets the user object for this class
    public void setUser(User user){
        this.user=user;
        Log.d("DH.java","User object set");
    }
    //Returns the User with filled fields
    public User getUser() {
        Log.d("DH.java", "User object sent");
        return user;
    }
}
