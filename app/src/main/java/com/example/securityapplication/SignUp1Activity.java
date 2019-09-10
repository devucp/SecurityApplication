package com.example.securityapplication;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp1Activity extends AppCompatActivity {

   // Database_Helper myDb;
    //Validation val = new Validation();
    private TextView text_view;
    private TextInputLayout textinputName,textinputDOB,textinputEmail,textinputPass,textinputCnfPass;
    private RadioGroup gender_grp;
    private RadioButton Radio_Gender;
   // private DatePickerDialog.OnDateSetListener setListener;
    private Button Btn_Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);
       // myDb = new Database_Helper(this);
       // java.util.Calendar calendar=Calendar.getInstance();
      //final int year=calendar.get(Calendar.YEAR);

        text_view = (TextView)findViewById(R.id.text_gender);
        textinputName = findViewById(R.id.textlayout_Name);
        textinputDOB = findViewById(R.id.textlayout_Dob);
        textinputEmail = findViewById(R.id.textlayout_Email);
        textinputPass = findViewById(R.id.textlayout_Pass);
        textinputCnfPass = findViewById(R.id.textlayout_CnfPass);
        gender_grp = (RadioGroup) findViewById(R.id.radiogrp);
        Btn_Submit = (Button)findViewById(R.id.btn_sub);

    }

//
//    private void ShowMessage(String title,String Message){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(Message);
//    }
//
//    public void Validater(View view) {
//        if (val.validateName(textinputName) & val.validateGender(gender_grp,text_view) & val.validateDob(textinputDOB) & val.validateEmail(textinputEmail) &
//                val.validatePassword(textinputPass) & val.validateCnfPassword(textinputPass,textinputCnfPass)){
//            AddData();
//        }
//        else {
//            Toast.makeText(this,"Enter Valid Credentials",Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void AddData() {
//        int selected_id = gender_grp.getCheckedRadioButtonId();
//        Radio_Gender = (RadioButton) findViewById(selected_id);
//        String gender = Radio_Gender.getText().toString().trim();
//        Boolean isInserted = myDb.insert_data(textinputName.getEditText().getText().toString().trim(),
//                gender,
//                textinputDOB.getEditText().getText().toString().trim(),
//                textinputEmail.getEditText().getText().toString().trim(),
//                textinputPass.getEditText().getText().toString().trim());
//        if (isInserted) {
//            textinputName.getEditText().setText(null);
//            gender_grp.clearCheck();
//            textinputDOB.getEditText().setText(null);
//            textinputEmail.getEditText().setText(null);
//            textinputPass.getEditText().setText(null);
//            textinputCnfPass.getEditText().setText(null);
//        }
//        else {
//            String UserEmail = textinputEmail.getEditText().getText().toString().trim();
//            boolean res = myDb.CheckUserEmail(UserEmail);
//            if (res){
//                Toast.makeText(this,"Email already taken",Toast.LENGTH_SHORT).show();
//            }
//            else{
//                Toast.makeText(this,"User Entry Unsuccessful",Toast.LENGTH_SHORT).show();
//            }
//        }
 //   }
}
