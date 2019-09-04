package com.example.securityapplication;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp_1 extends AppCompatActivity {

    Database_Helper myDb;
    Validation val = new Validation();
    private TextView text_view;
    private TextInputLayout textinputName,textinputGenderVal,textinputDOB,textinputEmail,textinputPass,textinputCnfPass;
    private RadioGroup gender_grp;
    private RadioButton Radio_Gender;
    private Button Btn_Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_1);
        myDb = new Database_Helper(this);

        text_view = (TextView)findViewById(R.id.text_gender);
        textinputName = findViewById(R.id.textlayout_Name);
        textinputDOB = findViewById(R.id.textlayout_Dob);
        textinputEmail = findViewById(R.id.textlayout_Email);
        textinputPass = findViewById(R.id.textlayout_Pass);
        textinputCnfPass = findViewById(R.id.textlayout_CnfPass);
        gender_grp = (RadioGroup) findViewById(R.id.radiogrp);
        Btn_Submit = (Button)findViewById(R.id.btn_sub);

    }

    public void Validater(View view) {
        if (val.validateName(textinputName) & val.validateGender(gender_grp,text_view) & val.validateDob(textinputDOB) & val.validateEmail(textinputEmail) &
                val.validatePassword(textinputPass) & val.validateCnfPassword(textinputPass,textinputCnfPass)){
            AddData();
        }
        else {
            Toast.makeText(this,"Enter Valid Credentials",Toast.LENGTH_SHORT).show();
        }
    }

    private void AddData() {

        int selected_id = gender_grp.getCheckedRadioButtonId();
        Radio_Gender = (RadioButton) findViewById(selected_id);
        String gender = Radio_Gender.getText().toString().trim();
        Boolean isInserted = myDb.insert_data(textinputName.getEditText().getText().toString().trim(),
                gender,
                textinputDOB.getEditText().getText().toString().trim(),
                textinputEmail.getEditText().getText().toString().trim(),
                textinputPass.getEditText().getText().toString().trim(),
                textinputCnfPass.getEditText().getText().toString().trim());
        if (isInserted) {
            textinputName.getEditText().setText(null);
            gender_grp.clearCheck();
            textinputDOB.getEditText().setText(null);
            textinputEmail.getEditText().setText(null);
            textinputPass.getEditText().setText(null);
            textinputCnfPass.getEditText().setText(null);
        }
        else {
            Toast.makeText(this,"Email already taken",Toast.LENGTH_SHORT).show();
        }
    }
}
