package com.example.securityapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

import static com.facebook.AccessTokenManager.TAG;

//Singleton class
public class FaceBookLoginIn implements Serializable{

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private MainActivity activity;
    private FirebaseUser user;

    private static volatile FaceBookLoginIn faceBookInstance;

    //private constructor
    private FaceBookLoginIn(){
        //Prevent form the reflection api.
        if (faceBookInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public void init(MainActivity activity, FirebaseAuth mAuth, FirebaseDatabase mFirebaseDatabase){

                    this.activity = activity;
                    this.mAuth = mAuth;
                    this.mFirebaseDatabase = mFirebaseDatabase;
    }

    public static FaceBookLoginIn getInstance() {
        //Double check locking pattern
        if (faceBookInstance == null) { //Check for the first time..if there is no instance available... create new one
            synchronized (FaceBookLoginIn.class) { //Check for the second time to make ThreadSafe
                //if there is no instance available... create new one
                if (faceBookInstance == null)
                    faceBookInstance = new FaceBookLoginIn();
            }
        }
        return faceBookInstance;
    }

    //Make singleton from serialize and deserialize operation.
    protected FaceBookLoginIn readResolve() {
        return getInstance();
    }


    public void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        // Whether user is registered or not is checked by firebase
        // We need to check if registered user sign's in using old device
        // If registered user uses new device to login then :
        // 1.need to logout from old device and change value of isLoggedIn status in Database to false
        // 2.now user can login from new device.
        // 3.If user logs in from new device then change the value of imei number in database to the imei number of
        // new device only and only if user logs in in the new device..till then the imei attribute will hold the value of imei number of old device.
        // 4.now user can login from new device.
        // 5.If user logs in from new device then change the value of imei number in database to the imei number of
        // new device only and only if user logs in in the new device..till then the imei attribute will hold the value of imei number of old device.

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            /*mDatabaseReference = mFirebaseDatabase.getReference().child("users");
                            mDatabaseReference.setValue(user.getUid());*/
                            setUser(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            setUser(null);
                        }
                    }
                });
    }

    public void linkAccount(AccessToken token) {

        // Link the anonymous user to the email credential
        //showProgressDialog();
        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        // [START link_credential]
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(activity, "Authenticated successfully.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END link_credential]
    }

    private void setUser(FirebaseUser user){
        this.user = user;
        activity.updateUI(user);
    }

    public boolean isLoggedIn(){
        //hideProgressDialog();
        if(this.user != null)
            return true;
        else
            return false;
    }
}