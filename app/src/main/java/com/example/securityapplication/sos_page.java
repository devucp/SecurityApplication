package com.example.securityapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class sos_page extends AppCompatActivity {
    Intent intent;
    public  static final int RequestPermissionCode  = 1 ;
    Button save, edit;
    String n1 = "1123456789";
    String n2 = "";
    String n3 = "";
    String n4 = "";
    String n5 = "";

    TextInputEditText c1, c2, c3, c4, c5,current;
    TextInputLayout cc1,cc2,cc3,cc4,cc5;
    private final static int CONTACT_PICKER_RESULT = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_page);

        EnableRuntimePermission(); //ASK FOR PERMISSIONS ON RUNTIME. WASN't present previously

        save = (Button) findViewById(R.id.sossave);
        edit = (Button) findViewById(R.id.sosedit);
        c1 = (TextInputEditText) findViewById(R.id.sose1);
        c2 = (TextInputEditText) findViewById(R.id.sose2);
        c3 = (TextInputEditText) findViewById(R.id.sose3);
        c4 = (TextInputEditText) findViewById(R.id.sose4);
        c5 = (TextInputEditText) findViewById(R.id.sose5);
        cc1 = (TextInputLayout) findViewById(R.id.sosl1);
        cc2 = (TextInputLayout) findViewById(R.id.sosl2);
        cc3 = (TextInputLayout) findViewById(R.id.sosl3);
        cc4 = (TextInputLayout) findViewById(R.id.sosl4);
        cc5 = (TextInputLayout) findViewById(R.id.sosl5);

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
        c1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (c1.getRight() - c1.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        current=c1;
                        intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, 7);
                    }
                }
                return false;
            }
        });
        c2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (c2.getRight() - c2.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        current=c2;
                        intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, 7);
                    }
                }
                return false;
            }
        });
        c3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (c3.getRight() - c3.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        current=c3;
                        intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, 7);
                    }
                }
                return false;
            }
        });
        c4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (c4.getRight() - c4.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        current=c4;
                        intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, 7);
                    }
                }
                return false;
            }
        });
        c5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (c5.getRight() - c5.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        current=c5;
                        intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, 7);
                    }
                }
                return false;
            }
        });



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
                        edit.setBackground(getResources().getDrawable(R.drawable.btn_cus));
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

                edit.setBackground(getResources().getDrawable(R.drawable.btn_cus_edit));

            c1.setEnabled(true);
                cc1.setAlpha(1);
                cc2.setAlpha(1);
                cc3.setAlpha(1);
                cc4.setAlpha(1);
                cc5.setAlpha(1);
            c2.setEnabled(true);
            c3.setEnabled(true);
            c4.setEnabled(true);
            c5.setEnabled(true);

            }
        });

    }
    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(sos_page.this,
                Manifest.permission.READ_CONTACTS))
        {

            Toast.makeText(sos_page.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(sos_page.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(sos_page.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(sos_page.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    @Override
    public void onActivityResult(int RequestCode, int ResultCode, Intent ResultIntent) {

        super.onActivityResult(RequestCode, ResultCode, ResultIntent);

        switch (RequestCode) {

            case (7):
                if (ResultCode == Activity.RESULT_OK) {

                    Uri uri;
                    Cursor cursor1, cursor2;
                    String TempNameHolder, TempNumberHolder, TempContactID, IDresult = "" ;
                    int IDresultHolder ;

                    uri = ResultIntent.getData();

                    cursor1 = getContentResolver().query(uri, null, null, null, null);

                    if (cursor1.moveToFirst()) {

                        TempNameHolder = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));

                        IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        IDresultHolder = Integer.valueOf(IDresult) ;

                        if (IDresultHolder == 1) {

                            cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID, null, null);

                            while (cursor2.moveToNext()) {

                                TempNumberHolder = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                Log.i("contact_name",TempNameHolder);
                                int len=TempNumberHolder.length();
                                String tmp;
                                tmp=TempNumberHolder.substring(len-10,len);
                                current.setText(tmp);

                            }
                        }

                    }
                }
                break;
        }
    }


}
