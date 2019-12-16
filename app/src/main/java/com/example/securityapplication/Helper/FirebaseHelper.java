package com.example.securityapplication.Helper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.securityapplication.GoogleFirebaseSignIn;
import com.example.securityapplication.MainActivity;
import com.example.securityapplication.ProfileActivity;
import com.example.securityapplication.R;
import com.example.securityapplication.SignUp2;
import com.example.securityapplication.model.Device;
import com.example.securityapplication.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDevicesDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mEmailDatabaseReference;
    private DatabaseReference mMobileDatabaseReference;
    private static final String TAG = "FirebaseHelper";
    private Context context;
    private User user;
    private Device device;
    private GoogleSignInClient mGoogleSignInClient;
    private static volatile FirebaseHelper firebaseHelperInstance;

    public void initFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        initDataBaseReferences();
        Log.d(TAG,"Firebase Initialization complete");
    }

    public void initContext(Context context){
        this.context = context;
    }

    public static FirebaseHelper getInstance() {
        //Double check locking pattern
        if (firebaseHelperInstance == null) { //Check for the first time..if there is no instance available... create new one
            synchronized (GoogleFirebaseSignIn.class) { //Check for the second time to make ThreadSafe
                //if there is no instance available... create new one
                if (firebaseHelperInstance == null) {
                    firebaseHelperInstance = new FirebaseHelper();
                    Log.d(TAG,"Created new FirebaseHelperInstance");
                }
            }
        }
        else {
            Log.d(TAG,"FirebaseHelperInstance Exists");
        }
        return firebaseHelperInstance;
    }

    //Make singleton from serialize and deserialize operation.
    protected FirebaseHelper readResolve() {
        return getInstance();
    }

    private void initDataBaseReferences(){
        Log.d(TAG,"Initializing Databse References... ");
        //Initialize Database
        mDevicesDatabaseReference = mFirebaseDatabase.getReference().child("Devices");
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("Users");
        mEmailDatabaseReference = mFirebaseDatabase.getReference().child("Email");
        mMobileDatabaseReference = mFirebaseDatabase.getReference().child("Mobile");
    }

    public void initGoogleSignInClient(String server_client_id){
        Log.d(TAG,"Initializing Google SignIN Client");
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(server_client_id)
                .requestEmail()
                .requestProfile()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public void makeDeviceImeiNull(String imei){
        // first make uid under imei null in Devices and imei under uid null in Users
        device = new Device();
        device.setUID("null");
        mDevicesDatabaseReference.child(imei).setValue(device);
    }

    public void makeUserImeiNull(){
        mUsersDatabaseReference.child(mAuth.getUid()).child("imei").setValue("null");
    }

    public void firebaseSignOut(String imei){
        Log.d(TAG,"Firebase SignOut(String imei) called");
        makeDeviceImeiNull(imei);

        //Firebase signOut
        if (mAuth.getCurrentUser() != null) {
            makeUserImeiNull();
            mAuth.signOut();
            Toast.makeText(context, "Logged Out from Firebase", Toast.LENGTH_SHORT).show();
        }
    }

    public void firebaseSignOut(){
        Log.d(TAG,"Firebase SignOut() called");
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
            //Toast.makeText(this, "Logged Out from Firebase", Toast.LENGTH_SHORT).show();
        }
    }

    public void googleSignOut(Activity activity){
        Log.d(TAG,"Google SignOut called");
        if(GoogleSignIn.getLastSignedInAccount(context) != null) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //updateUI(null);
                            //Toast.makeText(MainActivity.this,"Logged Out from Google",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public DatabaseReference getUsersDatabaseReference(){
        return mUsersDatabaseReference;
    }

    public DatabaseReference getDevicesDatabaseReference(){
        return mDevicesDatabaseReference;
    }

    public DatabaseReference getEmailDatabaseReference(){
        return mEmailDatabaseReference;
    }

    public DatabaseReference getMobileDatabaseReference(){
        return mMobileDatabaseReference;
    }

    public GoogleSignInClient getGoogleSignInClient(){
        return mGoogleSignInClient;
    }

    public FirebaseAuth getFirebaseAuth() { return mAuth; }

    public  FirebaseDatabase getFirebaseDatabase() { return mFirebaseDatabase; }
}
