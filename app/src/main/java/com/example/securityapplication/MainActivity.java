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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i== R.id.signInButton){
            if(mAuth.getCurrentUser()==null) {
                isValidUser = validateUserAndDevice("email");
                if (isValidUser) {
                    signIn(mEmail.getText().toString(), mPassword.getText().toString());
                }
                else {

                }
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
            Intent signUpIntent = new Intent(this,SignUp1Activity.class);
            startActivity(signUpIntent);
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
    private boolean validateUserAndDevice(String SignInType){
        if(!validateForm()){
            return false;
        }
        else {
            // 1.Check if registered user sign's in using old device or new device using imei number.
            mDevicesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot deviceDataSnapshot) {
                    if (deviceDataSnapshot.exists()){
                        Log.d(TAG, "Devices node exists in json tree");
                        Log.d("MainActiity", deviceDataSnapshot.toString());
                        Log.d(TAG,"From mobile: "+mImeiNumber);
                        Log.d(TAG,"From firebase: "+deviceDataSnapshot.hasChild(mImeiNumber));
                        if (deviceDataSnapshot.hasChild(mImeiNumber)){
                            // implies user tries to log in from same device as registered
                            // now check if email and password matches with credentials stored in db
                            // first get uid from imei
                            Device deviceUser = deviceDataSnapshot.child(mImeiNumber).getValue(Device.class);
                            final String uid = deviceUser.getUID();
                            if (uid == null){
                                // new user -> ask to sign up
                                Toast.makeText(MainActivity.this, "User not registered", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                //Log.d(TAG, "Inside dataSnapshot.hasChild(mImeiNumber) method");
                                Log.d(TAG, uid);
                                mUsersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
                                        Log.d("User Data Snapshot:",userDataSnapshot.toString());
                                        if (userDataSnapshot.exists()){
                                            if (userDataSnapshot.hasChild(uid)){
                                                User user = userDataSnapshot.child(uid).getValue(User.class);
                                                Log.d("password:",user.getPassword());
                                                if (user.getPassword() == null){
                                                    // user never logged in through email account..Give error
                                                    Toast.makeText(MainActivity.this, "You do not have email account", Toast.LENGTH_SHORT).show();
                                                }
                                                else if (user.getEmail().equals(email) && user.getPassword().equals(password)){
                                                    Log.d(TAG,"Log user in");
                                                    // Login the User
                                                    return true;
                                                }
                                                else {
                                                    Toast.makeText(MainActivity.this,"Invalid Email or Password", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else{
                                                // remove uid from imei in devices node
                                            }
                                        }
                                        else {
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
                        else {
                            Log.d(TAG, "Imei not registered");
                            /*// check if email exists
                            if (email exists condition){
                                //--Yet to be decided--//
                                // Implies user is registered but uses new device to login and new device is not registered
                                // In that case, user needs to logout from old device, so prompt the user to logout from old device with buttons allow and deny.
                                // If user clicks allow button, then:
                                    // 1.log out user from old device;
                                    // 2.change value of isLoggedIn status of old device in Database to false
                                // Now user can login from new device.
                                // Now if user tries to login from new device then check
                            }
                            else {
                                //Implies user is not registered
                                Toast.makeText(MainActivity.this, "User not Registered", Toast.LENGTH_SHORT).show();
                            }*/

                        }
                    }
                    else {
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
        initDataBase();

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

        if (getImei().equals(""))
            deviceId();

        FirebaseUser currentUser = mAuth.getCurrentUser();updateUI(currentUser);

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

    private void initDataBase(){
        //Initialize Database
        mDevicesDatabaseReference = mFirebaseDatabase.getReference().child("Devices");
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("Users");
        Log.d(TAG,mDevicesDatabaseReference.toString());
    }

    private void deviceId() {
        telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
            return;
        }
    }

    private String getImei(){
        String permission = Manifest.permission.READ_PHONE_STATE;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        if (res == PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mImeiNumber = telephonyManager.getImei(0);
            }
            else
            {
                mImeiNumber = telephonyManager.getDeviceId();
            }
            Log.d("IMEI","IMEI Number of slot 1 is:"+mImeiNumber);
            return mImeiNumber;
        }
        else{
            Log.d("SIgnUP2","PERMISSION FOR READ STATE NOT GRANTED, REQUESTING PERMSISSION...");
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},101);
            return "";
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
                Intent signUpIntent = new Intent(this,SignUp1Activity.class);
                startActivityForResult(signUpIntent,1);

            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
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
        //Firebase signOut
        mAuth.signOut();
        Toast.makeText(this,"Logged Out from Firebase",Toast.LENGTH_SHORT).show();
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
}
