package com.example.securityapplication;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    TextView textName,textEmail,textPhone,textAddress,textGender,textDob,textAadhaar;
    String ansName,ansEmail,ansPhone,ansAddress,ansGender,ansDob,ansAadhaar;
    SQLiteDBHelper mydb ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mydb = new SQLiteDBHelper(this);
        initviews();
        FetchAllData();
        DisplayData();

    }
    private void FetchAllData(){
        Cursor res;
        res = mydb.getAllData("7276625281"); //dummy identifier
        if (res.getCount() == 0){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No User Data Found",
                    Toast.LENGTH_LONG);
            toast.show();
            Log.d("Profile","No Data found");
        }
//        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()){
            ansName = res.getString(1);
            ansEmail = res.getString(2);
            ansGender = res.getString(3);
            ansPhone = res.getString(5);
            ansAadhaar = res.getString(6);
            ansAddress = res.getString(7);
            ansDob = res.getString(8);
        }
    }
    private void DisplayData() {
        textName.setText(ansName);
        textAadhaar.setText(ansAadhaar);
        textDob.setText(ansDob);
        textGender.setText(ansGender);
        textAddress.setText(ansAddress);
        textEmail.setText(ansEmail);
        textPhone.setText(ansPhone);
        Log.d("Profile","Message displayed Successfully");
    }

    private void initviews() {
        textName = findViewById(R.id.text_Name);
        textEmail = findViewById(R.id.text_Email);
        textPhone = findViewById(R.id.text_Phone);
        textAddress = findViewById(R.id.text_Address);
        textGender = findViewById(R.id.text_Gender);
        textDob = findViewById(R.id.text_DOB);
        textAadhaar = findViewById(R.id.text_Aadhaar);
    }

}
