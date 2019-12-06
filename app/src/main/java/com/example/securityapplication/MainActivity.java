package com.example.securityapplication;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.securityapplication.model.Device;
import com.example.securityapplication.model.Email;
import com.example.securityapplication.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

//import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TelephonyManager telephonyManager;
    private String mImeiNumber;
    private FirebaseAuth mAuth;
    private TextView mStatus;
    private EditText mEmail;
    private EditText mPassword;
    private Button mSignInButton;
    private String TAG = "MainActivity";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDevicesDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mEmailDatabaseReference;

    private Button mLinkAccountButton;
    private Button mVerifyEmailButton;

    //FaceBookLogin
    private LoginButton mFaceBookLoginButton;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private FaceBookLoginIn faceBookLoginIn;

    //GoogleFirebaseSignIn
    private SignInButton mGoogleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleFirebaseSignIn googleFirebaseSignIn;
    private static final int RC_SIGN_IN = 9001;

    private boolean isValidUser;
    private User user;
    private Device device;
    private String uid;

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i== R.id.signInButton){
            if(mAuth.getCurrentUser()==null) {
                if(validateForm()){
                    Hashtable<String,String> userData = new Hashtable<>();
                    userData.put("email",mEmail.getText().toString());
                    userData.put("password",mPassword.getText().toString());
                    userData.put("SignInType", "email");
                    // Check if registered user sign's in using old device or new device using imei number.
                    setDeviceForSignIn(mImeiNumber, userData);
                }
                else {
                    Log.d(TAG,"Invalid credentials");
                    Toast.makeText(MainActivity.this, "Enter valid credentials",Toast.LENGTH_SHORT).show();
                }
                //signIn(mEmail.getText().toString(), mPassword.getText().toString());
            }
            else {
                signOut();
                updateUI(null);
            }
        }
        else if (i==R.id.googleSignInButton){
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
        else if (i==R.id.signUpButton){
            // check if imei is registered
            setDeviceForSignUp(mImeiNumber);
        }
        else if (i==R.id.linkAccountButton){
            Intent linkAccountIntent = new Intent(this,LinkAccountActivity.class);
            startActivity(linkAccountIntent);
        }
        else if (i==R.id.verifyEmailButton){
            if (validateForm()) {
                mVerifyEmailButton.setEnabled(false);
                verifyEmailId(mEmail.getText().toString());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Grant Device read Permissions
        deviceId();

        //initialize Activity
        initViews();
        initOnClickListeners();

        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        initDataBaseReferences();

        /**  GOOGLE LOGIN  **/

        findViewById(R.id.googleSignInButton).setOnClickListener(this);

        //get single instance of user if logged in through google from user defined class GoogleFirebaseSignIn
        googleFirebaseSignIn = GoogleFirebaseSignIn.getInstance();
        //initialize user defined class GoogleFirebaseSignIn with Firebase user instance andTAGusing user defined init method
        googleFirebaseSignIn.init(this, mAuth, mFirebaseDatabase);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        /**  END GOOGLE LOGIN  **/


        /**   FACEBOOK LOGIN   **/

        //get single instance of user if logged in through facebook from user defined class FaceBookLoginIn
        faceBookLoginIn = FaceBookLoginIn.getInstance();
        //initialize user defined class FaceBookLoginIn with Firebase user instance andTAGusing user defined init method
        faceBookLoginIn.init(MainActivity.this, mAuth, mFirebaseDatabase);

        callbackManager = CallbackManager.Factory.create();
        mFaceBookLoginButton = findViewById(R.id.facebook_login_button);
        mFaceBookLoginButton.setPermissions(Arrays.asList(EMAIL));

        // Callback registration
        mFaceBookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FB", "facebook:onSuccess:" + loginResult);
                faceBookLoginIn.handleFacebookAccessToken(loginResult.getAccessToken());
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(faceBookLoginIn.isLoggedIn())
                    updateUI(currentUser);
                else
                    updateUI(null);
            }

            @Override
            public void onCancel() {
                Log.d("FB", "facebook:onCancel");
                updateUI(null);
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("FB", "facebook:onError", exception);
                updateUI(null);
            }
        });
        /**  END FACEBOOK LOGIN  **/

        /** SosPlayer Service intent**/
        startService(new Intent(this, SosPlayer.class));
    }

    public void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void initViews(){
        mEmail=findViewById(R.id.editEmail);
        mPassword=findViewById(R.id.editPassword);
        mStatus= findViewById(R.id.status);
        mSignInButton=findViewById(R.id.signInButton);
        mGoogleSignInButton=findViewById(R.id.googleSignInButton);
        mLinkAccountButton=findViewById(R.id.linkAccountButton);
        mVerifyEmailButton=findViewById(R.id.verifyEmailButton);
    }

    public void initOnClickListeners(){
        findViewById(R.id.signInButton).setOnClickListener(this);
        findViewById(R.id.linkAccountButton).setOnClickListener(this);
        findViewById(R.id.verifyEmailButton).setOnClickListener(this);
    }

    private void initDataBaseReferences(){
        //Initialize Database
        mDevicesDatabaseReference = mFirebaseDatabase.getReference().child("Devices");
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("Users");
        mEmailDatabaseReference = mFirebaseDatabase.getReference().child("Email");
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
        }
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
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mImeiNumber = telephonyManager.getImei(0);
                        Log.d("IMEI", "IMEI Number of slot 1 is:" + mImeiNumber);
                    }
                    else {
                        mImeiNumber = telephonyManager.getDeviceId();
                    }
                } else {
                    closeNow();
                    Toast.makeText(this, "Without permission we check", Toast.LENGTH_LONG).show();
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
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Hashtable<String,String> userData = new Hashtable<String,String>();
                userData.put("email",account.getEmail());
                userData.put("SignInType","google");
                setDeviceForSignIn(mImeiNumber, userData);

            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(MainActivity.this, "Authentication failed. Try Again",Toast.LENGTH_SHORT).show();
                updateUI(null);
            }

            /*try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                updateUI(null);
            }
            googleFirebaseSignIn.handleSignInResult(task);*/
        }
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                boolean hasBackPressed = data.getBooleanExtra("hasBackPressed",false);
                if (hasBackPressed){
                    signOut();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void signIn(final String email, final String password){
        Log.d(TAG,"Signing IN user with email "+email+" and password "+password);
         mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d(TAG, "onComplete: " + (isNew ? "new user" : "old user"));

                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, mDevicesDatabaseReference.toString());
                            //mDatabaseReference.setValue(user.getUid());
                            updateUI(user);
                        } else {
                            updateUI(null);
                        }
                    }
                });
    }

    private void signOut(){

        // first make uid under imei null
        deviceId();
        device = new Device();
        device.setUID("null");
        mDevicesDatabaseReference.child(mImeiNumber).setValue(device);

        //Firebase signOut
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
            Toast.makeText(this, "Logged Out from Firebase", Toast.LENGTH_SHORT).show();
        }
        //Google signOut
        if(GoogleSignIn.getLastSignedInAccount(this) != null) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //updateUI(null);
                            Toast.makeText(MainActivity.this,"Logged Out from Google",Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        //Facebook signOut
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedInFaceBook = accessToken != null && !accessToken.isExpired();
        if(isLoggedInFaceBook) {
            LoginManager.getInstance().logOut();
            Toast.makeText(this,"Logged Out from Facebook",Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUI(FirebaseUser user){
        if(user==null){
            mStatus.setText(R.string.not_logged);
            mSignInButton.setText(R.string.sign_in_text);
            mEmail.setVisibility(View.VISIBLE);
            mPassword.setVisibility(View.VISIBLE);
            mGoogleSignInButton.setVisibility(View.VISIBLE);
            mFaceBookLoginButton.setVisibility(View.VISIBLE);
            mLinkAccountButton.setVisibility(View.GONE);
            mVerifyEmailButton.setVisibility(View.GONE);
        }
        else if(user!=null){
            mStatus.setText(R.string.logged);
            mSignInButton.setText(R.string.sign_out_text);
            mEmail.setVisibility(View.GONE);
            mPassword.setVisibility(View.GONE);
            mGoogleSignInButton.setVisibility(View.GONE);
            mFaceBookLoginButton.setVisibility(View.GONE);
            mLinkAccountButton.setVisibility(View.VISIBLE);
            mVerifyEmailButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
            Log.d(TAG,mAuth.getCurrentUser().toString());
            for (int i = 1; i < mAuth.getCurrentUser().getProviderData().size(); i++) {
                Log.d(TAG, mAuth.getCurrentUser().getUid());
                Log.d(TAG, mAuth.getCurrentUser().getProviderData().get(i).getProviderId());
                Log.d(TAG, mAuth.getCurrentUser().getProviderData().get(i).getEmail());
                //Log.d(TAG,mAuth.getCurrentUser().getProviderData().get(i).getDisplayName());
            }
        }
    }

    private void verifyEmailId(String email){
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified())
            Log.d(TAG, "User Verified");
        else
            Log.d(TAG, "User not verified");
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Re-enable button
                        findViewById(R.id.verifyEmailButton).setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setUserForSignIn(String uid, final Hashtable<String,String> userData){
        mUsersDatabaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
                Log.d("User Data Snapshot:", userDataSnapshot.toString());
                if (userDataSnapshot.exists()) {
                    user = userDataSnapshot.getValue(User.class);
                } else {
                    user = null;
                }
                crossValidateUserData(userData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                Toast.makeText(MainActivity.this, "Failed to load User Information.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDeviceForSignIn(String imei, final Hashtable<String,String> userData){
        Log.d(TAG,"Inside setDeviceForSignIn method-Imei no.:"+imei);
        mDevicesDatabaseReference.child(imei).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot deviceDataSnapshot) {
                Log.d("Device Data Snapshot:", deviceDataSnapshot.toString());
                if (deviceDataSnapshot.exists()) {
                    device = deviceDataSnapshot.getValue(Device.class);
                } else {
                    device = null;
                }

                validateBeforeSignIn(userData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setDeviceForSignUp(String imei){
        mDevicesDatabaseReference.child(imei).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot deviceDataSnapshot) {
                Log.d("Device Data Snapshot:", deviceDataSnapshot.toString());
                if (deviceDataSnapshot.exists()) {
                    device = deviceDataSnapshot.getValue(Device.class);
                } else {
                    device = null;
                }
                validateBeforeSignUp1();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUidFromFirebaseForSignIn(final Hashtable<String ,String> userData){
        String email = userData.get("email");
        // replace "." with "," in email id to store in firebase db as key
        String commaSeperatedEmail = TextUtils.join(",", Arrays.asList(email.split("\\.")));
        Log.d(TAG,commaSeperatedEmail);
        Log.d(TAG,mEmailDatabaseReference.toString());
        mEmailDatabaseReference.child(commaSeperatedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
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
                isEmailRegistered(userData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void crossValidateUserData(Hashtable<String,String> userData){
        String email=null, password = null;
        String SignInType = userData.get("SignInType");

        switch (SignInType) {
            case "email":
                password = userData.get("password").toString();
            case "google":
                email = userData.get("email").toString();
                break;
            case "facebook":

                break;
            default:
                Log.d(TAG, "Invalid SignInType");
                return;
        }

        if (user != null) {
            Log.d("password:", user.getPassword());
            if (!user.getEmail().equals(email)) {
                // Case1:Either email entered is invalid or different
                // Case2:prompt user that this device is stored under other user ...ask previous user to logout

                if (SignInType.equals("google")){
                    // logout from google
                    signOut();
                }

            } else if (SignInType.equals("email") && !user.getPassword().equals(password)) {
                // Password is invalid..prompt user to re-enter password
                Log.d(TAG, "Invalid password");
                Toast.makeText(MainActivity.this,"Invalid Password",Toast.LENGTH_LONG).show();
            } else {
                if (SignInType.equals("email")) {
                    // Login the User through email
                    signIn(email, password);
                }
                else if (SignInType.equals("google")){
                    // login the user through google
                    //googleFirebaseSignIn.firebaseAuthWithGoogle(GoogleSignIn.getLastSignedInAccount(MainActivity.this));
                    googleFirebaseSignIn.linkGoogleAccount(GoogleSignIn.getLastSignedInAccount(MainActivity.this));
                }
                else if (SignInType.equals("facebook")){

                }
            }
        } else {
            Log.d(TAG, user.toString());
        }
    }

    private void isEmailRegistered(final Hashtable<String,String> userData){
        Log.d(TAG,"Inside isEmailRegistered");
        if (uid.equals("null")){
            // prompt user to signUp
            Log.d(TAG,"Email not registered");
            //Toast.makeText(MainActivity.this,"SignUp to Register",Toast.LENGTH_LONG).show();
            Intent signUpIntent = new Intent(this,SignUp1Activity.class);
            startActivity(signUpIntent);
        }
        else {
            /*  case1: user tries to sign in from same device
                case2:user tries to sign in from other device
                Solution for both is same:
               check if user logged out from previous device
               find imei of previous device: Email node->email->uid->imei
               if uid under Devices node of previous device is null then logged out..else prompt user to log out
             */
            Log.d(TAG, uid);
            mUsersDatabaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
                    Log.d("User Data Snapshot:", userDataSnapshot.toString());
                    if (userDataSnapshot.exists()) {
                        user = userDataSnapshot.getValue(User.class);
                        mDevicesDatabaseReference.child(user.getImei()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot deviceDataSnapshot) {
                                Log.d("Device Data Snapshot:", deviceDataSnapshot.toString());
                                if (deviceDataSnapshot.exists()) {
                                    device = deviceDataSnapshot.getValue(Device.class);
                                    if (device.getUID().equals("null")){
                                        /* implies user logged out from old device
                                           Now login the user and set uid under imei of device node
                                        */
                                        Log.d(TAG,"User is logged out from old device..Now user can login from new device");
                                        deviceId();
                                        device.setUID(uid);
                                        mDevicesDatabaseReference.child(mImeiNumber).setValue(device);

                                        String SignInType = userData.get("SignInType");
                                        Log.d(TAG,"SignInType:"+SignInType);
                                        switch (SignInType) {
                                            case "email":
                                                signIn(userData.get("email"),userData.get("password"));
                                                break;
                                            case "google":
                                                //googleFirebaseSignIn.firebaseAuthWithGoogle(GoogleSignIn.getLastSignedInAccount(MainActivity.this));
                                                googleFirebaseSignIn.linkGoogleAccount(GoogleSignIn.getLastSignedInAccount(MainActivity.this));
                                                break;
                                            case "facebook":

                                                break;
                                            default:
                                                Log.d(TAG, "Invalid SignInType");
                                                return;
                                        }

                                    }
                                    else {
                                        /* User not logged out from old device
                                        prompt user to log out from old device
                                        */
                                        Log.d(TAG, "User is LoggedIn in other device");
                                        Toast.makeText(MainActivity.this,
                                                "You are logged in another device .Please log Out from old device to continue", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    device = null;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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

    private void validateBeforeSignIn(Hashtable<String,String> userData) {
        String email=null;
        String SignInType = userData.get("SignInType");

        switch (SignInType) {
            case "email":
            case "google":
                email = userData.get("email").toString();
                break;
            case "facebook":

                break;
            default:
                Log.d(TAG, "Invalid SignInType");
                return;
        }

        // Check if registered user sign's in using old device or new device using imei number.
        if (device != null) {
            // implies user tries to log in from same device as registered
            // now check if email and password matches with credentials stored in db
            // first get uid from imei
            final String uid = device.getUID();
            if (!uid.equals("null")) {
                Log.d(TAG, uid);
                setUserForSignIn(uid, userData);
            } else {
                // check if email is registered
                setUidFromFirebaseForSignIn(userData);
            }
        } else {
            Log.d(TAG, "Imei not registered");
            // check if email is registered
            setUidFromFirebaseForSignIn(userData);
        }
    }

    private void validateBeforeSignUp1(){

        if (device == null){
            // go for signUp1
            Intent signUpIntent = new Intent(this,SignUp1Activity.class);
            startActivity(signUpIntent);

        }
        else {
            final String uid = device.getUID();
            if (uid.equals("null")){
                // go for signUp1
                Intent signUpIntent = new Intent(this,SignUp1Activity.class);
                startActivity(signUpIntent);
            }
            else {
                // user cannot register or
                Toast.makeText(MainActivity.this,"User is already registered",Toast.LENGTH_LONG).show();
            }
        }
    }
}


/*
package com.example.securityapplication;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.securityapplication.model.Device;
import com.example.securityapplication.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FirebaseValidation {

    private String TAG = "FirebaseValidation";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDevicesDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mEmailDatabaseRefernece;

    private FirebaseValidation(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        initDatabase();
    }

    private void initDatabase(){
        mDevicesDatabaseReference = mFirebaseDatabase.getReference().child("Devices");
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("Users");
        mEmailDatabaseRefernece = mFirebaseDatabase.getReference().child("Email");
    }

    public boolean validateUserAndDevice(String SignInType) {
        // 1.Check if registered user sign's in using old device or new device using imei number.

        mDevicesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot deviceDataSnapshot) {
                if (deviceDataSnapshot.exists()) {
                    Log.d(TAG, "Devices node exists in json tree");
                    Log.d("MainActiity", deviceDataSnapshot.toString());
                    Log.d(TAG, "From mobile: " + mImeiNumber);
                    Log.d(TAG, "From firebase: " + deviceDataSnapshot.hasChild(mImeiNumber));
                    if (deviceDataSnapshot.hasChild(mImeiNumber)) {
                        // implies user tries to log in from same device as registered
                        // now check if email and password matches with credentials stored in db
                        // first get uid from imei
                        Device deviceUser = deviceDataSnapshot.child(mImeiNumber).getValue(Device.class);
                        final String uid = deviceUser.getUID();
                        if (uid == null) {

                            //check if email is registered
                            mEmailDatabaseRefernece.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot emailDataSnapshot) {
                                    Log.d("Email Data Snapshot:", emailDataSnapshot.toString());
                                    if (emailDataSnapshot.exists()) {
                                        if (!emailDataSnapshot.hasChild(email)) {
                                            // prompt user to signUp
                                        } else {
                                            /* user tries to sign in from other device
                                             check if user logged out from previous device
                                             find imei of previous device: Email node->email->uid->imei
                                             if uid under Devices node of previous device is null then logged out..else prompt user to log out
                                             */
                                         /*   uid = emailDataSnapshot.getValue(email);
                                                    Log.d(TAG, uid);
                                                    mUsersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
        Log.d("User Data Snapshot:", userDataSnapshot.toString());
        if (userDataSnapshot.exists()) {
        if (userDataSnapshot.hasChild(uid)) {
        User oldUser = userDataSnapshot.child(uid).getValue(User.class);
        Device oldDeviceUser = deviceDataSnapshot.child(oldUser.getImei()).getValue(Device.class);
        if (oldDeviceUser.getUID() == null) {*/
                                                                /* implies user logged out from old device
                                                                    Now login the user
                                                                 *//*
        return true;
        } else {
                                                                /* User not logged out from old device
                                                                    prompt user to log out from old device
                                                                 *//*
        Log.d(TAG, "LoggedIn in other device");
        }
        } else {
        //
        }
        } else {
        Log.d(TAG, "Users node does not exists in json tree");
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
        }

@Override
public void onCancelled(@NonNull DatabaseError databaseError) {

        }
        });
        } else {
        //Log.d(TAG, "Inside dataSnapshot.hasChild(mImeiNumber) method");
        Log.d(TAG, uid);
        mUsersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
        Log.d("User Data Snapshot:", userDataSnapshot.toString());
        if (userDataSnapshot.exists()) {
        if (userDataSnapshot.hasChild(uid)) {
        User user = userDataSnapshot.child(uid).getValue(User.class);
        Log.d("password:", user.getPassword());
        if (!user.getEmail().equals(email)) {
        // Either email is invalid or prompt user that this device is stored
        // under other user ...ask previous user to logout
        } else if (!user.getPassword().equals(password)) {
        // Password is invalid..prompt user to re-enter password
        Log.d(TAG, "Invalid password");
        } else {
        // Login the User
        return true;
        }
        } else {
        // remove uid from imei in devices node
        }
        } else {
        Log.d(TAG, "Users node does not exists in json tree");
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

        } else {
        Log.d(TAG, "Imei not registered");
        //check if email is registered
        mEmailDatabaseRefernece.addListenerForSingleValueEvent(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot emailDataSnapshot) {
        Log.d("Email Data Snapshot:", emailDataSnapshot.toString());
        if (emailDataSnapshot.exists()) {
        if (!emailDataSnapshot.hasChild(email)) {
        // prompt user to signUp
        } else {
                                            /* user tries to sign in from other device
                                             check if user logged out from previous device
                                             find imei of previous device: Email node->email->uid->imei
                                             if uid under Devices node of previous device is null then logged out..else prompt user to log out
                                             *//*
        uid = emailDataSnapshot.getValue(email);
        Log.d(TAG, uid);
        mUsersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
        Log.d("User Data Snapshot:", userDataSnapshot.toString());
        if (userDataSnapshot.exists()) {
        if (userDataSnapshot.hasChild(uid)) {
        User oldUser = userDataSnapshot.child(uid).getValue(User.class);
        Device oldDeviceUser = deviceDataSnapshot.child(oldUser.getImei()).getValue(Device.class);
        if (oldDeviceUser.getUID() == null) {
                                                                /* implies user logged out from old device
                                                                    Now login the user
                                                                 *//*
        return true;
        } else {
                                                                /* User not logged out from old device
                                                                    prompt user to log out from old device
                                                                 *//*
        Log.d(TAG, "LoggedIn in other device");
        }
        } else {
        //
        }
        } else {
        Log.d(TAG, "Users node does not exists in json tree");
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
        }

@Override
public void onCancelled(@NonNull DatabaseError databaseError) {

        }
        });
        }
        } else {
        Log.d(TAG, "Devices node does not exists in json tree");
        //mDevicesDatabaseReference.child(mImeiNumber).child("uid").setValue("CM2RyALv19Sk6iAQVPlHB538auv1");
        }
        }

@Override
public void onCancelled(@NonNull DatabaseError databaseError) {
        // Getting Device failed, log a message
        Log.w(TAG, "loadDevice:onCancelled", databaseError.toException());
        Toast.makeText(MainActivity.this, "Failed to load Device Information.", Toast.LENGTH_SHORT).show();
        }
        });
        }
        }

* */