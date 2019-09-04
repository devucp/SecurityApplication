package com.example.securityapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database_Helper extends SQLiteOpenHelper {

    private static final String Database_Name = "Security_app2.db";
    private static final String Table_Name = "Signup_1";
    private static final String Col_1 = "Fname";
    private static final String Col_2 = "Gender";
    private static final String Col_3 = "DOB";
    private static final String Col_4 = "Email";
    private static final String Col_5 = "Password";
    private static final String Col_6 = "Confirm_Password";

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
    public Boolean insert_data(String fname, String gender, String dob, String Email, String password, String cnf_password){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Col_1, fname);
            contentValues.put(Col_2, gender);
            contentValues.put(Col_3, dob);
            contentValues.put(Col_4, Email);
            contentValues.put(Col_5, password);
            contentValues.put(Col_6, cnf_password);
            long result = db.insert(Table_Name, null, contentValues);
            if (result == -1)
                return false;
            else
                return true;
        }
        catch (Exception E){
            return false;
        }
    }
}
