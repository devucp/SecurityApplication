package com.example.securityapplication;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.securityapplication.model.Device;
import com.example.securityapplication.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;


public class SignUp2 extends AppCompatActivity {
    private final AppCompatActivity activity = SignUp2.this;


    private TextInputEditText input_mobile;
//    private TextInputEditText input_aadhar;
    private AutoCompleteTextView input_location;
//    private TextView error_message;


    private Button btn_submit;
    private Intent ReturnIntent;
    private InputValidation inputValidation;
    private Validation validation;
    private SQLiteDBHelper DBHelper;
    private User user;
    private Device device;
    private String blockcharset = "~#^|$%&*!,.";
    //adding requestCode variable for requestPermission
    private int RC;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDevicesDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mEmailDatabaseReference;
    private DatabaseReference mMobileDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleFirebaseSignIn googleFirebaseSignIn;

    private String uid;

    private String TAG = "SignUp2";

    private TextView text_view;
    private RadioGroup gender_grp;
    private RadioButton Radio_Gender;
    private DatePickerDialog datePickerDialog;
    private TextInputEditText textinputName,textinputDOB;
    private TextInputEditText date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        initViews();
        initListeners();
        initObjects();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        initDatabaseReferences();

        mAuth=FirebaseAuth.getInstance();
        device = new Device();

        Resources res = getResources();
        String[] Locality = res.getStringArray(R.array.Locality);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Locality);
        input_location.setAdapter(adapter);
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

        //get single instance of user if logged in through google from user defined class GoogleFirebaseSignIn
        googleFirebaseSignIn = GoogleFirebaseSignIn.getInstance();
        //initialize user defined class GoogleFirebaseSignIn with Firebase user instance and TAG using user defined init method
        //googleFirebaseSignIn.init(this, mAuth, mFirebaseDatabase);

     /*   mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG,"Calling func sendVerificationEmail");
                    sendVerificationEmail(user);
                } else {
                    // User is signed out
                    Log.d(TAG,"User is signed out");
                }
            }
        };*/
    }

    private void signOut(){
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
            Toast.makeText(this, "Logged Out from Firebase", Toast.LENGTH_SHORT).show();
        }
    }

    /**Initialize Views*/
    private void initViews(){
        input_mobile = findViewById(R.id.input_mobile);
        input_location = findViewById(R.id.AutoCompleteTextView);
//        input_actv = findViewById(R.id.AutoCompleteTextView);
       // error_message = findViewById(R.id.error_message);
        btn_submit = findViewById(R.id.btn_submit);
        text_view = findViewById(R.id.text_gender);
        textinputName = findViewById(R.id.textlayout_Name);
        textinputDOB = findViewById(R.id.textlayout_Dob);
        date=findViewById(R.id.textlayout_Dob);
        gender_grp = findViewById(R.id.radiogrp);
    }

    private void initObjects(){
        inputValidation = new InputValidation(activity);
        DBHelper = new SQLiteDBHelper(activity);
        ReturnIntent = new Intent();
        user = getIntent().getParcelableExtra("User"); //getting the User object from previous signup activity
    }

    private void initDatabaseReferences(){
        //Initialize Database
        mDevicesDatabaseReference = mFirebaseDatabase.getReference().child("Devices");
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("Users");
        mMobileDatabaseReference = mFirebaseDatabase.getReference().child("Mobile");
        mEmailDatabaseReference = mFirebaseDatabase.getReference().child("Email");
    }

    private void initListeners(){
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c= java.util.Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(SignUp2.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(validation.validateName(textinputName) & validation.validateGender(gender_grp,text_view) & validation.validateDob(textinputDOB))){
                    Toast.makeText(SignUp2.this,"Enter Valid Credentials",Toast.LENGTH_SHORT).show();
                    return;
                }

                //Call the method to validate the fields
                boolean valid_mobile = false;
//                boolean valid_aadhar = false;
                boolean valid_location = false;
                boolean empty_mobile = inputValidation.is_Empty(input_mobile, "PLEASE ENTER MOBILE NO. ");
                if (!empty_mobile) {
                    if (!inputValidation.isMinLength(input_mobile, 10, getString(R.string.no_phone)) || !inputValidation.is_numeric(input_mobile)) {

                        input_mobile.setError(getString(R.string.no_phone));

                    }
                    else {
                        valid_mobile = true;
                        input_mobile.setError(null);
                    }
                }

                if (input_location.getText().toString().isEmpty()) {
                    input_location.setError(getString(R.string.no_location));
                }
                else{
                    valid_location = true;
                    input_location.setError(null);
                }
                /**NOTE:MOVED empty if statement here to ensure error messages are displayed before exiting
                 //if(empty){
                 //  return;

                 }*/

                //Moved IMEI reading code to this place so IMEI can be stored for user object
                boolean empty = inputValidation.all_Empty(input_mobile,input_location,getString(R.string.message));

                Log.d("SIgnUP2","Valid Mobile:"+valid_mobile+" Valid Location:"+valid_location);
                boolean valid = valid_mobile && valid_location;

                Log.d("SIgnUP2","Empty:"+empty+" Valid"+valid);

                int selected_id = gender_grp.getCheckedRadioButtonId();
                Radio_Gender = (RadioButton) findViewById(selected_id);
                String gender = Radio_Gender.getText().toString().trim(); //function .getEditText() have been removed as TextInputEditText doesn't require it.

                //NOTE: Allow database entry only if not empty AND valid
                if(!empty && valid){
                    String imei = null;
                    TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    String permission = Manifest.permission.READ_PHONE_STATE;
                    int res = getApplicationContext().checkCallingOrSelfPermission(permission);
                    if (res == PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            imei = tm.getImei(0);
                            Log.d("IMEI", "IMEI Number of slot 1 is:" + imei);

                        } else {
                            imei=tm.getDeviceId();
                            Log.d("SignUp2", "SDK Version not of required level");
                            Log.d("SignUp2", "Using getDeviceId()"+imei);

                        }
                    } else {
                        Log.d("SIgnUP2", "PERMISSION FOR READ STATE NOT GRANTED, REQUESTING PERMSISSION...");
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.READ_PHONE_STATE}, RC);
                    }

                    if (DBHelper.checkUser(input_mobile.getText().toString().trim()) ){

                        if (user.getEmail() == null || user.getPassword() == null){
                            // go back to signUp1
                            finishActivity(2);
                        }

                        user.setName(textinputName.getText().toString().trim());
                        user.setGender(gender);
                        user.setDob(textinputDOB.getText().toString().trim());
                        user.setMobile(input_mobile.getText().toString().trim());
                        user.setLocation(input_location.getText().toString().trim());
                        user.setImei(imei);//setting IMEI
                        user.setIsPaid(false);

                        // check if mobile number exists
                        setUidFromFirebase(user.getMobile());
                        //added conditional checking and showing respective Toast message

                    } else {
                        Log.d("SignUp2", "User exists ");
                        Toast.makeText(getApplicationContext(), "MOBILE NO. ALREADY EXISTS", Toast.LENGTH_LONG).show();
                    }
                }
                }
            }

        );
    }

    private void writeDataToFirebase(FirebaseUser firebaseUser){
        //push user to firebase database 'Users' node
        mUsersDatabaseReference.child(firebaseUser.getUid()).setValue(user);
        //push device to firebase database 'Devices' node
        mDevicesDatabaseReference.child(user.getImei()).setValue(device);
        //push email and mobile no. on root node
        String emailKey = TextUtils.join(",", Arrays.asList(user.getEmail().split("\\."))); //as key in firebase db cannot contain "."
        mEmailDatabaseReference.child(emailKey).setValue(firebaseUser.getUid());
        mMobileDatabaseReference.child(user.getMobile()).setValue(firebaseUser.getUid());

        Log.d("Pushed to db",mUsersDatabaseReference.getDatabase().toString());
        Log.d("Pushed to db",mDevicesDatabaseReference.getDatabase().toString());
        Log.d("Pushed to db",mEmailDatabaseReference.getDatabase().toString());
        Log.d("Pushed to db",mMobileDatabaseReference.getDatabase().toString());

    }

    private void AddUser(){
        //added conditional checking and showing respective Toast message
        if (DBHelper.addUser(user))
        {
            Log.d("User Email:",user.getEmail());
            Log.d("User imei:",user.getImei());

            // create the User
             mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(SignUp2.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Log.d(TAG,"new user ? ->"+task.getResult().getAdditionalUserInfo().isNewUser());
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                storeAndStartNextActivity(firebaseUser);

                            } else {
                                try{
                                    throw task.getException();
                                }
                                catch (FirebaseAuthUserCollisionException e){
                                    // user exists -> sign in the user
                                    mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                                            .addOnCompleteListener(SignUp2.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                        storeAndStartNextActivity(firebaseUser);
                                                    } else {
                                                        try{
                                                            throw task.getException();
                                                        }
                                                        catch (Exception e){
                                                            Log.d(TAG,"Exception:"+e.getMessage());
                                                            Toast.makeText(SignUp2.this, "Authentication failed.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                            });
                                }
                                catch (Exception e){
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure"+e.getMessage());
                                    Toast.makeText(SignUp2.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
        else
            Toast.makeText(getApplicationContext(), "SOMETHING WENT WRONG", Toast.LENGTH_LONG).show();

    }

    private void storeAndStartNextActivity(FirebaseUser firebaseUser){
        device.setUID(firebaseUser.getUid());

        // write data to firebase
        writeDataToFirebase(firebaseUser);
        Toast.makeText(getApplicationContext(), "YOU ARE NOW A SAVIOUR", Toast.LENGTH_LONG).show();
                                /*ReturnIntent.putExtra("ResultIntent",user);
                                Log.d("SignUp2 ","Returned Completed User Object"+user.getMobile()+user.getLocation());
                                setResult(10,ReturnIntent);//to finish sing up 1 activity
                                activity.finish();*/

        // check if user is signed in to google or facebook
        if (GoogleSignIn.getLastSignedInAccount(SignUp2.this) != null){
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(SignUp2.this);
            if (acct != null) {
                //link google acc to email acc
                googleFirebaseSignIn.linkGoogleAccount(acct);
                Log.d(TAG,"Logged in to google");
            }
        }
        else
            Log.d("isLoggedinGoogle:","Not logged in");

        Intent homeFragement = new Intent(SignUp2.this,home_fragment.class);
        startActivity(homeFragement);
    }

    private void setUidFromFirebase(final String mobile){
        mMobileDatabaseReference.child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot mobileNodeDataSnapshot) {
                Log.d("Mobile Data Snapshot:", mobileNodeDataSnapshot.toString());
                if (mobileNodeDataSnapshot.exists()) {
                    uid = mobileNodeDataSnapshot.getValue().toString();
                } else {
                    uid = null;
                }
                // validate before storing details..check if mobile no is registered
                validateBeforeStoring(mobile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validateBeforeStoring(String mobile){

        if (uid == null){
            // user details can be pushed to db
            AddUser();
        }
        else{
            // prompt user to enter different mobile no.
            Log.d(TAG, "Mobile no. already registered in firebase");
            Toast.makeText(SignUp2.this, "Mobile no. already registered. Enter different mobile number",Toast.LENGTH_LONG).show();
        }
    }
}





