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

import java.io.File;
import java.util.HashMap;

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
    public static final String COLUMN_PAID = "paid";
    public static final String COLUMN_COUNT="count";
    private static String DB_PATH = "/data/data/com.example.securityapplication/databases/";

    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER DEFAULT 1 PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT, "+
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_GENDER + " TEXT, " +
//                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_MOBILE + " TEXT, " +
//                    COLUMN_AADHAR + " TEXT, " +
                    COLUMN_LOCATION + " TEXT, " +
                    COLUMN_DOB + " TEXT, " +
                    COLUMN_IMEI + " TEXT, " +
                    COLUMN_TESTM + " BOOLEAN DEFAULT FALSE, "+
                    COLUMN_PAID  + " BOOLEAN DEFAULT FALSE, "+
                    COLUMN_COUNT  + " BOOLEAN DEFAULT TRUE "+")";

    private static final String SOS_TABLE = "sostable";
    public static final String COLUMN_C1 = "c1";
    public static final String COLUMN_C2 = "c2";
    public static final String COLUMN_C3 = "c3";
    public static final String COLUMN_C4 = "c4";
    public static final String COLUMN_C5 = "c5";

    private static final String CREATE_SOSTABLE_QUERY =
            "CREATE TABLE "+ SOS_TABLE +"(" +
                    COLUMN_C1 + " TEXT , " +
                    COLUMN_C2 + " TEXT ,"+
                    COLUMN_C3 + " TEXT ," +
                    COLUMN_C4 + " TEXT ," +
                    COLUMN_C5 + " TEXT " +")";
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
        try {
            SQLiteDatabase db = db = SQLiteDatabase.openDatabase(DB_PATH + "userinfo.db", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            Cursor cursor = db.rawQuery("select * FROM user", null);
            //Cursor cursor2 = db.rawQuery("select * FROM sostable", null);
            Log.d("SQL", "innumberofrow" + cursor.getCount());
            db.close();//Added close stmt
            return cursor.getCount();
        }catch (Exception e){
            Log.d("message",e.getMessage());
        }
        return 0;
    }

    /**Adding user*/
    public boolean addUser(User user){
        Log.d("SQL12","Add User is started");
        newU=user;
        this.user = user;
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
            contentValues.put(COLUMN_PAID, user.isPaid());
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
    public boolean addsosContacts(HashMap<String,String> SosC){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_C1, SosC.get("c1"));
        contentValues.put(COLUMN_C2, SosC.get("c2"));
        contentValues.put(COLUMN_C3, SosC.get("c3"));
        contentValues.put(COLUMN_C4, SosC.get("c4"));
        contentValues.put(COLUMN_C5, SosC.get("c5"));

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
        db.close();//Added close stmt
        return cursor;
    }


    //TO UPDATE TESTMODE
    public void updatetestmode(Boolean bool){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_TESTM,bool);
        db.update(TABLE_NAME,contentValues,COLUMN_ID + "=?",
                new String[]{"1"});
        Log.d("checking3","inside updatetestmode"+this.getTestmode());
        db.close();
    }
    /**Updating user*/
    public void updateUser(User user){
        newU=user;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, user.getName());
//        contentValues.put(COLUMN_EMAIL, user.getEmail());
//        contentValues.put(COLUMN_PASSWORD, user.getPassword());
        contentValues.put(COLUMN_GENDER, user.getGender());
        contentValues.put(COLUMN_MOBILE,user.getMobile());
        contentValues.put(COLUMN_LOCATION, user.getLocation());
        contentValues.put(COLUMN_IMEI, user.getImei());
        contentValues.put(COLUMN_DOB, user.getDob()); //ADDED DOB
        contentValues.put(COLUMN_EMAIL, user.getEmail());
        contentValues.put(COLUMN_PAID, user.isPaid());
        Log.d("Paid1234hellow","inupdate"+user.isPaid());
        db.update(TABLE_NAME,contentValues,COLUMN_ID + "=?", //Changed from Column_Email to Column_ID
                new String[]{"1"});
        Log.d("Paid1234hellow","inupdate"+user.isPaid());
        db.close();
    }

    /**Checking if user is present*/
    public boolean getTestmode(){
        SQLiteDatabase db = this.getReadableDatabase();
        String str="false";
        Cursor cursor = db.rawQuery("select "+ COLUMN_TESTM +" FROM "+TABLE_NAME, null);
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                str = cursor.getString(0);
            }
            db.close();//Added close stmt
            if(str.equals("1"))
                return true;
        }
        Log.d("checking44",str);
        return false;
    }
    public User getdb_user(){
        SQLiteDatabase db = null;
        User newuser = new User();
        try {
            db = SQLiteDatabase.openDatabase(DB_PATH + "userinfo.db", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            Cursor cursor = db.rawQuery("select * FROM user", null);
            Cursor cursor2 = db.rawQuery("select * FROM sostable", null);
            Log.d("Paid1234hello9", "noofrow" + cursor.getCount());
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    newuser.setName(cursor.getString(cursor.getColumnIndex("name")));
                    newuser.setDob(cursor.getString(cursor.getColumnIndex("dob")));
                    newuser.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                    newuser.setGender(cursor.getString(cursor.getColumnIndex("gender")));
                    newuser.setLocation(cursor.getString(cursor.getColumnIndex("location")));
                    newuser.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
                    newuser.setPaid(cursor.getString(cursor.getColumnIndex("paid")).equals("1"));
                     Log.d("Paid1234hello2",cursor.getString(cursor.getColumnIndex("paid"))+"jj"+newuser.isPaid());
                     db.close();
                }
            } else {
                Log.d("Paid1234hello", "hello2");
            }
        }catch (Exception se ) {
            Log.d("Paid1234hello", "hello3"+se.getMessage());
        }
        return newuser;
    }

    public Boolean get_count(){
        SQLiteDatabase db = null;
        Boolean bool=true;
        try {
            db = SQLiteDatabase.openDatabase(DB_PATH + "userinfo.db", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            Cursor cursor = db.rawQuery("select "+COLUMN_COUNT+" FROM user", null);
            Log.d("get_coutn", "noofrow" + cursor.getCount());
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    bool=Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_COUNT)));
                    db.close();
                }
            }
        }catch (Exception se ) {
            Log.d("Paid1234hello", "hello3"+se.getMessage());
        }
        Log.d("get_coutn", "boolean : " + bool);
        return bool;
    }
    public void set_count(boolean bool){
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(DB_PATH + "userinfo.db", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            ContentValues cv = new ContentValues();
            cv.put("count",bool);
            db.update(TABLE_NAME, cv, COLUMN_ID+" = 1", null);
        }catch (Exception se ) {
            Log.d("set_count", se.getMessage());
        }
    }

    public void delete_table(){

        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH + "userinfo.db", null, SQLiteDatabase.CREATE_IF_NECESSARY);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+SOS_TABLE);
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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME, null);
        if (cursor.getCount()!=0) {
            Log.d("Database", "Details loaded in Cursor");
        }
        else {
            Log.d("Database","No records Found");
        }
        db.close();//Added close stmt
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
    //forcefully deletes database using context to ensure creation of tables
    public void deleteDatabase(Context ctx){

        ctx.deleteDatabase(DB_name);
        Log.d("SQLite","on deleteDatabase : no. of rows = "+numberOfRows());
        Log.d("SQLiteDBHelper","on deleteDatabase: Deleted database");
    }
}