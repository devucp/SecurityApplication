package com.example.securityapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Database_Helper extends SQLiteOpenHelper {

    private static final String Database_Name = "Security_app2.db";
    private static final String Table_Name = "Signup_1";
    private static final String Col_1 = "Fname";
    private static final String Col_2 = "Gender";
    private static final String Col_3 = "DOB";
    private static final String Col_4 = "Email";
    private static final String Col_5 = "Password";

    public Database_Helper(Context context) {
        super(context, Database_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+Table_Name+" (Fname varchar(50), Gender Varchar(6), DOB Varchar(8), Email varchar(30) primary key, Password varchar(16), Confirm_Password varchar(16))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists "+Table_Name);
        onCreate(db);
    }

    private String EncodePass(final String Password){
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(Password.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Boolean insert_data(String fname, String gender, String dob, String Email, String password){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String EncPass = EncodePass(password);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Col_1, fname);
            contentValues.put(Col_2, gender);
            contentValues.put(Col_3, dob);
            contentValues.put(Col_4, Email);
            contentValues.put(Col_5, EncPass);
            long result = db.insert(Table_Name, null, contentValues);
            if (result == -1) {
                Log.d("SignUp Activity","Data not inserted");
                return false;
            }
            else {
                Log.d("SignUp Activity","Data inserted: "+fname+" "+gender+" "+dob+" "+Email+" "+EncPass);
                return true;
            }
        }
        catch (Exception E){
            E.printStackTrace();
            return false;
        }
    }

    public boolean CheckUserEmail(String UserEmail){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select Fname from "+Table_Name +" where Email = '"+UserEmail+"'" , null);
        if(res.getCount()>0) {
            return true;
        } else {
            return false;
        }
    }
}
