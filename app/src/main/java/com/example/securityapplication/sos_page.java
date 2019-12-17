package com.example.securityapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.securityapplication.Helper.FirebaseHelper;
import com.example.securityapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class sos_page extends AppCompatActivity {

    Intent intent;
    public  static final int RequestPermissionCode  = 1 ;
    Button btn_SosEdit, btn_SosSave;

    FirebaseHelper firebaseHelper;
    private TextInputEditText c1, c2, c3, c4, c5,current;
    private TextInputLayout cc1,cc2,cc3,cc4,cc5;
    private final static int CONTACT_PICKER_RESULT = 1001;

    private ContentValues values;
    private String sos_n1,sos_n2,sos_n3,sos_n4,sos_n5;
    private String temp_n1,temp_n2,temp_n3,temp_n4,temp_n5;
    private SQLiteDBHelper mydb;
    private Validation val;
    private User user;
    private Button btn_Edit,btn_Save;
    boolean c1added,c2added,c3added,c4added,c5added;

    private Intent ReturnIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_page);

        firebaseHelper = FirebaseHelper.getInstance();
        firebaseHelper.initFirebase();
        firebaseHelper.initContext(getApplicationContext());

        values = new ContentValues();
        mydb = new SQLiteDBHelper(this);
        user = new User();
        val = new Validation();

        EnableRuntimePermission();
        initViews();
        initInitialContacts();
        initTempValues();
        initListeners();


        c1.setEnabled(false);
        c2.setEnabled(false);
        c3.setEnabled(false);
        c4.setEnabled(false);
        c5.setEnabled(false);

        ReturnIntent = new Intent();

    }

    private void initViews() {

        btn_SosEdit = (Button) findViewById(R.id.sosedit);
        btn_SosSave = (Button) findViewById(R.id.sossave);
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

        btn_SosSave.setEnabled(false);
    }

    private void initInitialContacts() {
        try{
            Cursor res;
            res = mydb.getSosContacts();
            if (res.getCount() == 0){
                Toast.makeText(getApplicationContext(),
                        "No SOS Contact records Found",
                        Toast.LENGTH_LONG).show();
                Log.d("SOS Activity","No Contact Data found ");
                initfirstvalues();
                FillViews();
            }
            while (res.moveToNext()) {
                user.setSosc1(res.getString(0));
                user.setSosc2(res.getString(1));
                user.setSosc3(res.getString(2));
                user.setSosc4(res.getString(3));
                user.setSosc5(res.getString(4));

                Log.d("SOS Activity", "User Object set in SOS activity successfully " +
                        "Sosc1 = " + user.getSosc1() + " Sosc2 = " + user.getSosc2());

                sos_n1 = user.getSosc1();
                sos_n2 = user.getSosc2();
                sos_n3 = user.getSosc3();
                sos_n4 = user.getSosc4();
                sos_n5 = user.getSosc5();
                Log.d("SOS Activity", "Current SOS Contacts loaded into Variables; sos_n1="+sos_n1+"sos_n2="+sos_n2);
                FillViews();
            }

        }
        catch (RuntimeException e){
            Log.d("SOS Contact page","Encountered Null pointer Exception; Setting initial values to empty");
            sos_n1 = "";
            sos_n2 = "";
            sos_n3 = "";
            sos_n4 = "";
            sos_n5 = "";
        }
    }

    private void FillViews() {
        c1.setText(sos_n1);
        c2.setText(sos_n2);
        c3.setText(sos_n3);
        c4.setText(sos_n4);
        c5.setText(sos_n5);
        Log.d("SOS Activity","FillViews() sos_n1="+sos_n1+" value");
        Log.d("SOS Activity","SOS contact views filled with values c1="+c1.getText()+"value before this");
    }

    private void initfirstvalues() {
        sos_n1 = "";
        sos_n2 = "";
        sos_n3 = "";
        sos_n4 = "";
        sos_n5 = "";
        Log.d("SOS Activity","SOS Contact table loaded for 1st time, empty values set; sos_n1=" +sos_n1+" value");
    }

    private void initTempValues() {
        temp_n1 = "";
        temp_n2 = "";
        temp_n3 = "";
        temp_n4 = "";
        temp_n5 = "";
    }


    private void initListeners(){


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

        btn_SosSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent;
                intent = new Intent(sos_page.this,navigation.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);

                //btn_SosEdit.setBackground(getResources().getDrawable(R.drawable.btn_cus));

                //FOR c1
                Log.d("SosActivity","OnClick : Initial value for c1="+c1.getText());
                    if (c1.getText().toString().equals("")) {
                        //toast
                        Log.d("SOS Activity","C1 Empty");
                        Toast.makeText(getApplicationContext(), "1st contact is mandatory", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        temp_n1 = c1.getText().toString().trim();
                        if (!temp_n1.equals("")){
                            if (val.EditvalidatePhone(c1)){
                                if (temp_n1.equals(sos_n1)){
                                    //no change
                                    c1added = false;
                                }
                                if (!checkUnique(c1,1)){
                                    Log.d("SOS Activity", "OnClick : Duplicate c1 values found; c1="+c1.getText());
//                                    c2.setError("Duplicate Contact Found");
                                    c1added = false;
                                    return;
                                }
                                else {
                                    c1added = true;
//                                    user.setSosc1(temp_n1);
                                }
                            }
                            else {
//                                c1.setError("Enter a valid mobile number");
                                return;
                            }
                        }
                    }
                    //FOR C2
                    if (c2.getText().toString().equals("")) {
                        Log.d("Sos", "c2 found empty");
                        if (sos_n2.isEmpty()) {
                            //was empty previously too
                            c2added = false;
                            temp_n2="";
                        } else {
                            //update db .. was not empty previously
                            c2added = true;
                            temp_n2="";
//                            user.setSosc2(temp_n2);
                        }
                    } else {
                        temp_n2 = c2.getText().toString();
                        if (!temp_n2.equals("")){
                            if (val.EditvalidatePhone(c2)){
                                if (temp_n2.equals(sos_n2)) {
                                    //no change
                                    c2added = false;
                                }
                                if (!checkUnique(c2,2)) {
                                    //duplicate value raise alert
                                    Log.d("SOS Activity", "onClick : Duplicate c2 values found; c2="+c2.getText());
//                                    c2.setError("Duplicate Contact Found");
                                    c2added = false;
                                    return;
                                } else {
                                    //save c2
                                    c2.setError(null);
                                    c2added = true;
//                                    user.setSosc2(temp_n2);
                                }
                            }
                            else {
//                                c2.setError("Enter a valid Mobile number");
                                return;
                            }
                        }
                        else {
                            temp_n2.equals("");
                        }
                    }

                    //FOR C3
                    if (c3.getText().toString().equals("")) {
                        if (sos_n3.isEmpty()) {
                            //was empty previously
                            temp_n3="";
                            c3added = false;
                        } else {
                            c3added = true;
                            temp_n3="";
//                            user.setSosc3(temp_n3);
                        }
                    } else {
                        temp_n3 = c3.getText().toString();
                        if (!temp_n3.equals("")){
                            if (val.EditvalidatePhone(c3)){
                                if (temp_n3.equals(sos_n3)) {
                                    //no change
                                    c3added = false;
                                }
                                if (!checkUnique(c3,3)) {
                                    // duplicate value raise alert
                                    Log.d("SOS Activity","onClick : Duplicate C3 values found ; c3="+c3.getText());
//                                    c3.setError("Duplicate contact found");
                                    c3added = false;
                                    return;
                                } else {
                                    //save c3
                                    c3added = true;
                                    c3.setError(null);
//                                    user.setSosc3(temp_n3);
                                }
                            }
                            else {
//                                c3.setError("Enter a Valid Mobile Number");
                                return;
                            }
                        }
                    }
                    //FOR c4
                    if (c4.getText().toString().equals("")){
                        if (sos_n4.isEmpty()) {
                            //was empty previously
                            c4added = false;
                            temp_n4="";
                        } else {
                            //was not empty previously
                            c4added = true;
                            temp_n3="";
//                            user.setSosc4(temp_n4);
                        }
                    } else {
                        temp_n4 = c4.getText().toString();
                        if (!temp_n4.equals("")){
                           if (val.EditvalidatePhone(c4)){
                               if (temp_n4.equals(sos_n4)) {
                                   //no change
                                   c4added = false;
                               }
                               if (!checkUnique(c4,4)) {
                                   // duplicate value raise alert
                                   Log.d("SOS","onClick :Duplicate c4 values found; c4="+c4.getText());
//                                   c4.setError("Duplicate Contact found");
                                   c4added = false;
                                   return;
                               } else {
                                   //save c4
                                   c4added = true;
                                   c4.setError(null);
//                                   user.setSosc4(temp_n4);
                               }
                           }
                           else {
//                               c4.setError("Enter a valid mobile number");
                               return;
                           }
                        }
                    }
                    //FOR C5
                    if (c5.getText().toString().equals("")){
                        if (sos_n5.isEmpty()) {
                            //was empty previously
                            c5added = false;
                            temp_n5="";
                        }
                        else {
                            //was not empty previously
                            c5added = true;
                            temp_n5="";
//                            user.setSosc5(temp_n5);
                        }
                    } else {
                        temp_n5 = c5.getText().toString();
                        if (!temp_n5.equals("")){
                            if (val.EditvalidatePhone(c5)){
                                if (temp_n5.equals(sos_n5)) {
                                    //no change
                                    c5added = false;
                                }
                                if (!checkUnique(c5,5)) {
                                    // duplicate value raise alert
//                                    c5.setError("Duplicate contact found");
                                    Log.d("SOS","onClick :Duplicate c5 values found; c5="+c5.getText());
                                    c5added = false;
                                    return;
                                } else {
                                    //save c5
                                    c5.setError(null);
                                    c5added = true;
//                                    user.setSosc5(temp_n5);
                                }
                            }
                            else {
//                                c5.setError("Enter a valid mobile number");
                                return;
                            }
                        }
                    }

                    if (c1added||c2added||c3added||c4added||c5added){
                       user.setSosc1(temp_n1);
                       user.setSosc2(temp_n2);
                       user.setSosc3(temp_n3);
                       user.setSosc4(temp_n4);
                       user.setSosc5(temp_n5);

                       if (mydb.addsosContacts(user)){
                           c1.setEnabled(false);
                           c2.setEnabled(false);
                           c3.setEnabled(false);
                           c4.setEnabled(false);
                           c5.setEnabled(false);
                           btn_SosSave.setEnabled(false);
                           firebaseHelper.addsos_infirebase(FirebaseAuth.getInstance().getUid(),c1.getText().toString(),c2.getText().toString(),c3.getText().toString(),c4.getText().toString(),
                                   c5.getText().toString());
                           Toast.makeText(sos_page.this,"DATA saved successfully ",Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                       else {
                           Toast.makeText(sos_page.this,"SOS Contact could not be added",Toast.LENGTH_SHORT).show();
                           Log.d("SosActivity","Data was not entered");
                       }

                    }
                    else {
                        //nochange found move to next activity
                        startActivity(intent);
                    }



            }
        });

        btn_SosEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            btn_SosEdit.setBackground(getResources().getDrawable(R.drawable.btn_cus_edit));
            c1.setEnabled(true);
            btn_SosSave.setAlpha(1);
                cc1.setAlpha(1);
                cc2.setAlpha(1);
                cc3.setAlpha(1);
                cc4.setAlpha(1);
                cc5.setAlpha(1);
            c2.setEnabled(true);
            c3.setEnabled(true);
            c4.setEnabled(true);
            c5.setEnabled(true);
            btn_SosSave.setEnabled(true);
            }
        });
    }
    private boolean checkUnique(EditText contact,int i) {
        String check1 = c1.getText().toString();
        String check2 = c2.getText().toString();
        String check3 = c3.getText().toString();
        String check4 = c4.getText().toString();
        String check5 = c5.getText().toString();
        String input = contact.getText().toString();

        if (i==1){
            if (input.equals(check2)||input.equals(check3)||input.equals(check4)||input.equals(check5)){
                contact.setError("Duplicate mobile number found");
                return false;
            }
            else {
                return true;
            }
        }else if (i==2){
            if (input.equals(check1)||input.equals(check3)||input.equals(check4)||input.equals(check5)){
                contact.setError("Duplicate mobile number found");
                return false;
            }
            else {
                return true;
            }
        }else if (i==3){
            if (input.equals(check2)||input.equals(check1)||input.equals(check4)||input.equals(check5)){
                contact.setError("Duplicate mobile number found");
                return false;
            }
            else {
                return true;
            }
        }else if (i==4){
            if (input.equals(check2)||input.equals(check3)||input.equals(check1)||input.equals(check5)){
                contact.setError("Duplicate mobile number found");
                return false;
            }
            else {
                return true;
            }
        }else if (i==5){
            if (input.equals(check2)||input.equals(check3)||input.equals(check4)||input.equals(check1)){
                contact.setError("Duplicate mobile number found");
                return false;
            }
            else {
                return true;
            }
        }
        return true;
    }

    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(sos_page.this,
                Manifest.permission.READ_CONTACTS))
        {
            Log.d("SosActivity","Permissions: Permission granted");
        } else {
            ActivityCompat.requestPermissions(sos_page.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
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
                                TempNumberHolder=TempNumberHolder.replaceAll("\\D+","");
                                int len=TempNumberHolder.length();
                                if(len<10)
                                {
                                    current.setText("");
                                }
                                else{
                                    String tmp;
                                    tmp=TempNumberHolder.substring(len-10,len);
                                    current.setText(tmp);
                                }

                            }
                        }

                    }
                }
            break;
        }
    }

    @Override
    public void onBackPressed(){
        ReturnIntent.putExtra("ResultIntent",user);
        //Log.d("SignUp2 ","Returned Completed User Object"+user.getMobile()+user.getLocation());
        setResult(10,ReturnIntent);//to finish sing up 1 activity
        AlertDialog.Builder a_builder = new AlertDialog.Builder(sos_page.this);
        a_builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Message");
        alert.show();
    }

}
