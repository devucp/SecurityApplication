package com.example.securityapplication;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.securityapplication.Helper.FirebaseHelper;
import com.example.securityapplication.Helper.KeyboardHelper;
import com.example.securityapplication.model.Device;
import com.example.securityapplication.model.User;
//import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

//import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TelephonyManager telephonyManager;
    private String mImeiNumber;
    private TextView mStatus;
    private EditText mEmail;
    private EditText mPassword;
    private Button mSignInButton;
    private String TAG = "MainActivity";
    private Button signupbttn;

    private FirebaseHelper firebaseHelper;

    //GoogleFirebaseSignIn
    private SignInButton mGoogleSignInButton;
    private GoogleFirebaseSignIn googleFirebaseSignIn;
    private static final int RC_SIGN_IN = 9001;
    ProgressBar pgsBar;
    ProgressBar pgsBar1;

    private User user;
    private Device device;
    private String uid;

    //persistent service
    private Intent mSosPlayerIntent;

    //Permissions request code
    int RC;

    private static Hashtable<String,String> userData;
    private boolean isUserStatusChecked = false;

    public void pgbarshow()
    {
        mSignInButton.setText("");
        findViewById(R.id.pBar).setVisibility(VISIBLE);
        mSignInButton.getBackground().setAlpha(100);

        mSignInButton.setClickable(false);
        signupbttn.setClickable(false);

        mEmail.setEnabled(false);
        mPassword.setEnabled(false);
        findViewById(R.id.ForgetPassword).setEnabled(false);
        findViewById(R.id.checkbox).setEnabled(false);
        mGoogleSignInButton.setEnabled(false);
    }

    public void pgbarhide()
    {
        findViewById(R.id.pBar).setVisibility(GONE);
        mSignInButton.setText("SIGN IN");
        mSignInButton.getBackground().setAlpha(255);

        signupbttn.setClickable(true);
        mSignInButton.setClickable(true);

        mEmail.setEnabled(true);
        mPassword.setEnabled(true);
        findViewById(R.id.ForgetPassword).setEnabled(true);
        findViewById(R.id.checkbox).setEnabled(true);
        mGoogleSignInButton.setEnabled(true);
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if(i== R.id.signInButton){

            if(firebaseHelper.getFirebaseAuth().getCurrentUser()==null) {
                Log.d(TAG,"Current user is null");
                if(validateForm()){
                    if (!IsInternet.checkInternet(MainActivity.this))
                        return;

                    KeyboardHelper.hideSoftKeyboard(MainActivity.this, v);

                    userData = new Hashtable<>();

                    pgbarshow();

                    userData.put("email",mEmail.getText().toString());
                    userData.put("password",mPassword.getText().toString());
                    userData.put("SignInType", "email");
                    // Check if registered user sign's in using old device or new device using imei number.
                    setDeviceForSignIn(mImeiNumber);
                }
                else {
                    Log.d(TAG,"Invalid credentials");
                    Toast.makeText(MainActivity.this, "Enter valid credentials",Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (i==R.id.googleSignInButton){
            if (!IsInternet.checkInternet(MainActivity.this))
                return;

            Intent signInIntent = firebaseHelper.getGoogleSignInClient().getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
        else if (i==R.id.signUpButton){

            signupbttn.setText("");
            findViewById(R.id.pBar1).setVisibility(VISIBLE);
            mSignInButton.setClickable(false);

            signupbttn.getBackground().setAlpha(100);

            Intent signUpIntent = new Intent(this,SignUp1Activity.class);
            startActivityForResult(signUpIntent,1);

        }
        //Added Reset Passowrd Activity
        else if (i==R.id.ForgetPassword){
            Intent forgetPasswordIntent = new Intent(this,ResetPasswordActivity.class);
            startActivity(forgetPasswordIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize Activity
        initViews();
        initOnClickListeners();

        FirebaseApp.initializeApp(this);

        /**  Get FirebaseHelper Instance **/
        firebaseHelper = FirebaseHelper.getInstance();
        firebaseHelper.initFirebase();
        firebaseHelper.initContext(MainActivity.this);

        //Grant Device read Permissions
        deviceId();

        if (mImeiNumber == null)
            deviceId();

        /**  GOOGLE LOGIN  **/
        firebaseHelper.initGoogleSignInClient(getString(R.string.server_client_id));
        findViewById(R.id.googleSignInButton).setOnClickListener(this);
        //get single instance of user if logged in through google from user defined class GoogleFirebaseSignIn
        googleFirebaseSignIn = GoogleFirebaseSignIn.getInstance();
        //initialize user defined class GoogleFirebaseSignIn with Firebase user instance andTAGusing user defined init method
        initializeGoogleFirebaseSignIn();

        /** SosPlayer Service intent**/
        startService(new Intent(this, SosPlayer.class));

        /** DATABASE FORCEFUL CREATION**/
        //uncomment to forcefully delete database
//        SQLiteDBHelper sqLiteDBHelper= new SQLiteDBHelper(this);
//        sqLiteDBHelper.deleteDatabase(this);

    }

    public void onStart(){
        super.onStart();

        FirebaseUser currentUser = firebaseHelper.getFirebaseAuth().getCurrentUser();
        updateUI(currentUser);
    }

    private void initializeGoogleFirebaseSignIn(){
        if (mImeiNumber == null)
            deviceId();
        googleFirebaseSignIn.init(this, mImeiNumber);
    }

    public void initViews(){
        mEmail=findViewById(R.id.editEmail);
        mPassword=findViewById(R.id.editPassword);
        mStatus= findViewById(R.id.status);
        mSignInButton=findViewById(R.id.signInButton);
        mGoogleSignInButton=findViewById(R.id.googleSignInButton);
        signupbttn = findViewById(R.id.signUpButton);
        pgsBar = findViewById(R.id.pBar);
        pgsBar1= findViewById(R.id.pBar1);
    }

    public void initOnClickListeners(){
        findViewById(R.id.signInButton).setOnClickListener(this);
    }

    private void checkUserStatus(){
        Log.d(TAG,"Checkig user status...");
        if (mImeiNumber == null)
            deviceId();
        if (firebaseHelper.getFirebaseAuth().getCurrentUser() == null){
            Log.d(TAG,"Inside checkUserStatus:Current user is null");
            firebaseHelper.getDevicesDatabaseReference().child(mImeiNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot deviceDataSnapshot) {
                    Log.d(TAG,"Inside onDataChange");
                    if (deviceDataSnapshot.exists()){
                        device = deviceDataSnapshot.getValue(Device.class);
                        if (!device.getUID().equals("null")){
                            // for this, either same user will be already signed in(so this will be the case if data cleared from device)
                            // or other user may have formatted the mobile without logging out
                            // so user gets logged out without updating firebas database, so update firebase
                            // make Devices->imei->"null" and Users->uid->imei->"null"
                            // check internet connection
                            uid = device.getUID();
                            Log.d(TAG,"Formatted or uninstalled or cleared data behavior");
                            deviceId();
                            device = new Device();
                            device.setUID("null");
                            firebaseHelper.getDevicesDatabaseReference().child(mImeiNumber).setValue(device).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    firebaseHelper.getUsersDatabaseReference().child(uid).child("imei").setValue("null").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG,"Updated firebase");
                                            isUserStatusChecked = true;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                            Log.d(TAG,"Current user:"+firebaseHelper.getFirebaseAuth().getCurrentUser());
                            updateUI(firebaseHelper.getFirebaseAuth().getCurrentUser());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG,databaseError.getDetails());
                }
            });
        }
    }

    private void deviceId() {
        telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
            return;
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mImeiNumber = telephonyManager.getImei(0);
                Log.d("IMEI", "IMEI Number of slot 1 is:" + mImeiNumber);
            }
            else {
                mImeiNumber = telephonyManager.getDeviceId();
            }
            if (!isUserStatusChecked) {
                //incase user formats or uninstalls or clears user data
                checkUserStatus();
            }
        }

        //Log.d("MAinActivity","SMS intent");
        //check permissions

        while(!checkSMSPermission());
    }

    public  boolean checkSMSPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Required for sending SMS in case of SOS", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "PERMISSION FOR SEND SMS NOT GRANTED, REQUESTING PERMSISSION...");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, RC);
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED;
    }

    private void closeNow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            finishAffinity();
        }
        else{
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    deviceId();
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                    closeNow();
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            pgbarshow();
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                userData = new Hashtable<String,String>();
                userData.put("email",account.getEmail());
                userData.put("SignInType","google");
                setDeviceForSignIn(mImeiNumber);

            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(MainActivity.this, "Authentication failed. Try Again",Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                boolean hasBackPressed = data.getBooleanExtra("hasBackPressed",true);
                Log.d(TAG,"hasBackPressed:"+hasBackPressed);
                if (hasBackPressed){
                    LogOutUser();

                    pgsBar1.setVisibility(GONE);
                    signupbttn.getBackground().setAlpha(255);
                    signupbttn.setText("SIGN UP");
                    mSignInButton.getBackground().setAlpha(255);

                    if(pgsBar.getVisibility()==VISIBLE)
                    {
                        pgsBar.setVisibility(GONE);
                    }
                    mSignInButton.setClickable(true);
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void signIn(final String email, final String password){
         Log.d(TAG,"Signing IN user with email "+email+" and password "+password);

         firebaseHelper.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d(TAG, "onComplete: " + (isNew ? "new user" : "old user"));

                            final FirebaseUser firebaseUser = firebaseHelper.getFirebaseAuth().getCurrentUser();
                            Log.d(TAG, firebaseHelper.getDevicesDatabaseReference().toString());

                            deviceId();
                            device = new Device();
                            device.setUID(firebaseUser.getUid());
                            firebaseHelper.getDevicesDatabaseReference().child(mImeiNumber).setValue(device).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    firebaseHelper.getUsersDatabaseReference().child(firebaseUser.getUid()).child("imei").setValue(mImeiNumber).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            storeData(firebaseUser);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        } else {

                            try{
                                throw task.getException();
                            }
                            catch (FirebaseAuthInvalidCredentialsException e){
                                Log.d(TAG,"Exception:"+e.getMessage());
                                Toast.makeText(MainActivity.this, "Invalid Password",Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e){
                                Log.d(TAG,"Exception:"+e.getMessage());
                            }
                            updateUI(null);
                        }
                    }
                });
    }

    private void LogOutUser(){
        if (mImeiNumber == null)
            deviceId();
        firebaseHelper.firebaseSignOut();
        firebaseHelper.googleSignOut(MainActivity.this);
    }

    public void updateUI(FirebaseUser firebaseUser){
        if(firebaseUser==null){
            mStatus.setText(R.string.not_logged);
            mSignInButton.setText(R.string.sign_in_text);
            mEmail.setVisibility(VISIBLE);
            mPassword.setVisibility(VISIBLE);
            mGoogleSignInButton.setVisibility(VISIBLE);
            pgbarhide();
        }
        else {

            /** SosPlayer Service intent**/
            startService(new Intent(this, SosPlayer.class));
            Intent mHomeIntent = new Intent(MainActivity.this,navigation.class);
            startActivity(mHomeIntent);
            try {
                closeNow();
            }catch (Exception e){
                Log.d(TAG,"Exception on closing activity:"+e.getMessage());
                finish();
            }
        }
        Log.d(TAG,"UI updated successfully");
    }

    private void crossValidateUserData(){
        String email, password = null;
        String SignInType = userData.get("SignInType");

        if (SignInType.equals("email"))
            password = userData.get("password");
        email = userData.get("email");

        if (user != null) {
            //Log.d("password:", user.getPassword());
            if (!user.getEmail().equals(email)) {
                // Case1:Either email entered is invalid or different
                // Case2:prompt user that this device is stored under other user ...ask previous user to logout

                if (SignInType.equals("google")){
                    // logout user from google
                    firebaseHelper.googleSignOut(MainActivity.this);
                }
                pgbarhide();

            } else {
                if (SignInType.equals("email")) {
                    // Login the User through email
                    signIn(email, password);
                }
                else if (SignInType.equals("google")){
                    // login the user through google
                    initializeGoogleFirebaseSignIn();
                    googleFirebaseSignIn.firebaseAuthWithGoogle(GoogleSignIn.getLastSignedInAccount(MainActivity.this));
                }
            }
        } else {
            Log.d(TAG, user.toString());
            pgbarhide();
        }
    }

    private void setDeviceForSignIn(String imei){
        Log.d(TAG,"Inside setDeviceForSignIn method-Imei no.:"+imei);
        firebaseHelper.getDevicesDatabaseReference().child(imei).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot deviceDataSnapshot) {
                Log.d("Device Data Snapshot:", deviceDataSnapshot.toString());
                if (deviceDataSnapshot.exists()) {
                    device = deviceDataSnapshot.getValue(Device.class);
                } else {
                    device = null;
                }
                validateBeforeSignIn();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,databaseError.getDetails());
            }
        });
    }

    private void setUidFromFirebaseForSignIn(){
        Log.d(TAG,"Inside setUidFromFirebaseForSignIn");
        String email = userData.get("email");
        // replace "." with "," in email id to store in firebase db as key
        String commaSeperatedEmail = TextUtils.join(",", Arrays.asList(email.split("\\.")));
        Log.d(TAG,commaSeperatedEmail);
        Log.d(TAG,firebaseHelper.getEmailDatabaseReference().toString());
        firebaseHelper.getEmailDatabaseReference().child(commaSeperatedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot emailNodeDataSnapshot) {
                Log.d("Email Data Snapshot:", emailNodeDataSnapshot.toString());
                if (emailNodeDataSnapshot.exists()) {
                    //emailNode = emailNodeDataSnapshot.getValue(Email.class);
                    uid = emailNodeDataSnapshot.getValue().toString();
                    Log.d(TAG,uid);
                } else {
                    uid = "null";
                }
                isEmailRegistered();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isEmailRegistered(){
        Log.d(TAG,"Inside isEmailRegistered");
        if (uid.equals("null")){
            Log.d(TAG,"Account not registered");
            // prompt user to signUp
            if (userData.get("SignInType").equals("google")){
                Intent signUpIntent = new Intent(this,SignUp1Activity.class);
                startActivityForResult(signUpIntent,1);
                pgbarhide();
            }
            else
                Toast.makeText(MainActivity.this,"Account not registered",Toast.LENGTH_LONG).show();
            pgbarhide();
        }
        else {
            /*  case1: user tries to sign in from same device
                case2:user tries to sign in from other device and maybe registered or not
                Solution for both is same:
               check if user logged out from previous device
               find imei of previous device: Email node->email->uid->imei
               if uid under Devices node of previous device is null then logged out..else prompt user to log out
             */
            Log.d(TAG, uid);
            firebaseHelper.getUsersDatabaseReference().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
                    Log.d("User Data Snapshot:", userDataSnapshot.toString());
                    if (userDataSnapshot.exists()) {
                        user = userDataSnapshot.getValue(User.class);
                        if (user.getImei().equals("null")){
                            // user is logged out
                            Log.d(TAG,"No user not logged  in. Login the user");
                            crossValidateUserData();
                        }
                        else {
                            firebaseHelper.getDevicesDatabaseReference().child(user.getImei()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot deviceDataSnapshot) {
                                    Log.d("Device Data Snapshot:", deviceDataSnapshot.toString());
                                    if (deviceDataSnapshot.exists()) {
                                        device = deviceDataSnapshot.getValue(Device.class);
                                        if (device.getUID().equals("null")) {
                                            /* implies user logged out from old device
                                               Now login the user and set uid under imei of device node
                                            */
                                            Log.d(TAG, "User is logged out from old device..Now user can login from new device");
                                            device.setUID(uid);
                                            crossValidateUserData();

                                        } else {
                                            /* User not logged out from old device
                                            prompt user to log out from old device
                                            */
                                            Log.d(TAG, "User is LoggedIn in other device");
                                            Toast.makeText(MainActivity.this,
                                                    "You are logged in another device .Please logout from old device to continue", Toast.LENGTH_LONG).show();
                                            LogOutUser();
                                            pgbarhide();
                                        }
                                    } else {
                                        //user can login

                                        String SignInType = userData.get("SignInType");
                                        Log.d(TAG, "SignInType:" + SignInType);
                                        switch (SignInType) {
                                            case "email":
                                                signIn(userData.get("email"), userData.get("password"));
                                                break;
                                            case "google":
                                                initializeGoogleFirebaseSignIn();
                                                googleFirebaseSignIn.firebaseAuthWithGoogle(GoogleSignIn.getLastSignedInAccount(MainActivity.this));
                                                break;
                                            default:
                                                Log.d(TAG, "Invalid SignInType");
                                                return;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    } else {
                        user = null;
                        Log.d(TAG,"User is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    // Getting User failed, log a message
                    Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                    Toast.makeText(MainActivity.this, "Failed to load User Information.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void validateBeforeSignIn() {
        Log.d(TAG,"Inside validateBeforeSignIn");
        // Check if registered user sign's in using old device or new device using imei number.
        if (device != null) {
            // first get uid from imei
            final String uid = device.getUID();
            if (uid.equals("null")) {
                Log.d(TAG, uid);
                //setUserForSignIn(uid, userData);
                //to check if email is registered
                setUidFromFirebaseForSignIn();
            } else {
                // for this, either same user will be already signed in(so this will be the case if data cleared)
                // or other user may have formatted the mobile without logging out
                // implemented in checkUserStatus() function
                pgbarhide();
                Log.d(TAG,"Current user:"+firebaseHelper.getFirebaseAuth().getCurrentUser()+"this should not be the case");
            }
        } else {
            Log.d(TAG, "Imei not registered");
            //to check if email is registered
            setUidFromFirebaseForSignIn();
        }
    }

    /**Checks whether service is running or not**/
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onDestroy() {
//        stopService(mSosPlayerIntent);
        Log.i("Mainactivity destroyed", "onDestroy!");
        super.onDestroy();

    }

    public void storeData(final FirebaseUser firebaseUser){
        if (firebaseUser != null) {
            final String uid = firebaseUser.getUid();
            firebaseHelper.getUsersDatabaseReference().child(uid).addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    Log.d("Paid12345","schin1"+user.getName()+user.isPaid());
                    SQLiteDBHelper db=new SQLiteDBHelper(MainActivity.this);

                    db.addUser(user);
                    if (user.getSosContacts() != null)
                        db.addsosContacts(user.getSosContacts()); //to fetch SOSContacts from Firebase

                    Log.d("Paid12345","schin"+user.getName()+ user.isPaid());
                    if(dataSnapshot.getValue(User.class).isPaid()){
                        Log.d("Paid12345","i am here");
                        home_fragment.setpaid(true);
                    }
                    else{
                        home_fragment.setpaid(false);
                    }
                    updateUI(firebaseUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
