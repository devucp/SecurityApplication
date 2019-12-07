package com.example.securityapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class sos_page extends AppCompatActivity {
    Button save, edit;
    String n1 = "1123456789";
    String n2 = "";
    String n3 = "";
    String n4 = "";
    String n5 = "";

    TextInputEditText c1, c2, c3, c4, c5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_page);
        save = (Button) findViewById(R.id.sossave);
        edit = (Button) findViewById(R.id.sosedit);
        c1 = (TextInputEditText) findViewById(R.id.sose1);
        c2 = (TextInputEditText) findViewById(R.id.sose2);
        c3 = (TextInputEditText) findViewById(R.id.sose3);
        c4 = (TextInputEditText) findViewById(R.id.sose4);
        c5 = (TextInputEditText) findViewById(R.id.sose5);
        if (n1.length() > 9)
            c1.setText(n1);
        if (n2.length() > 9)
            c2.setText(n2);
        if (n3.length() > 9)
            c3.setText(n3);
        if (n4.length() > 9)
            c4.setText(n4);
        if (n5.length() > 9)
            c5.setText(n5);

        c1.setEnabled(false);
        c2.setEnabled(false);
        c3.setEnabled(false);
        c4.setEnabled(false);
        c5.setEnabled(false);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c1.getText().length()==10)
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
                }

            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
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


}
