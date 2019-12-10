package com.example.securityapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class sos_page extends AppCompatActivity {
    String n1 = "";
    String n2 = "";
    String n3 = "";
    String n4 = "";
    String n5 = "";

    private SQLiteDBHelper mydb;
    private Button btn_Edit,btn_Save;
    private TextInputEditText c1, c2, c3, c4, c5;
    boolean c1added,c2added,c3added,c4added,c5added;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_page);

         mydb = new SQLiteDBHelper(this);

        initViews();
        initListeners();

    }

    private void initListeners(){

        c1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                n1 = c1.getText().toString().trim();
                if (n1.length() !=10){
                    c1.setError("please enter a valid mobile no.");
                }
                else if (n1.length()==0) {
                    c1added = false;
                }
                else if (n1.length()==10){
                    c1added = true;
                }
            }
        });
        c2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                n2 = c2.getText().toString().trim();
                if (n2.length() !=10){
                    c2.setError("please enter a valid mobile no.");
                }
                else if (n2.length()==0){
                    c2added = false;
                }
                else if (n2.length()==10){
                    c2added = true;
                }
            }
        });
        c3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (c3.getText().toString().trim().length() !=10){
                    c3.setError("please enter a valid mobile no.");
                }
            }
        });
        c4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (c4.getText().toString().trim().length() !=10){
                    c4.setError("please enter a valid mobile no.");
                }
            }
        });
        c5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {

                if (c5.getText().toString().trim().length() !=10){
                    c5.setError("please enter a valid mobile no.");
                }
            }
        });


        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*if(c1.getText().length()==10)
                {
                    if((c2.getText().length()==0||c2.getText().length()==10)&&(c3.getText().length()==0||
                            c3.getText().length()==10)&&(c4.getText().length()==0||c4.getText().length()==10)&&(c5.getText().length()==0||
                            c5.getText().length()==10)) {

                        c1.setEnabled(false);
                        c2.setEnabled(false);
                        c3.setEnabled(false);
                        c4.setEnabled(false);
                        c5.setEnabled(false);
                        Toast.makeText(sos_page.this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
                        Intent intent;
                        intent = new Intent(sos_page.this,navigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(sos_page.this, "Please Field contact information Correctly", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(sos_page.this, "Emergency Contact 1 is mandodary", Toast.LENGTH_SHORT).show();
                    Toast.makeText(sos_page.this, "Emergency Contact 1 is mandatory", Toast.LENGTH_SHORT).show();
                }*/

                if (c2added){
                    //check if c2,c3,c4 or c5 are added or not
                    if (c2added||c3added){

                    }
                    else{
                        //other contacts are not added save c1
//                        addSos
                    }
                }
                else {
                    Toast.makeText(sos_page.this,"Please Enter atleast 1 Sos contact",Toast.LENGTH_LONG).show();
                    //if no contacts are added , do not proceed.
                    return;
                }

            }
        });

        btn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c1.setEnabled(true);
                c2.setEnabled(true);
                c3.setEnabled(true);
                c4.setEnabled(true);
                c5.setEnabled(true);
            }
        });
    }

    private void initViews() {

        btn_Save = (Button) findViewById(R.id.sossave);
        btn_Edit = (Button) findViewById(R.id.sosedit);

        c1 = (TextInputEditText) findViewById(R.id.sose1);
        c2 = (TextInputEditText) findViewById(R.id.sose2);
        c3 = (TextInputEditText) findViewById(R.id.sose3);
        c4 = (TextInputEditText) findViewById(R.id.sose4);
        c5 = (TextInputEditText) findViewById(R.id.sose5);
    }
}
