package com.example.securityapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

import static com.facebook.AccessTokenManager.TAG;

//Singleton class
public class FaceBookLoginIn implements Serializable{

    private FirebaseAuth mAuth;
    private MainActivity activity;
    private FirebaseUser user;

    //private OnFaceBookLoginListener callback;
    private static volatile FaceBookLoginIn faceBookInstance;

    //private constructor
    private FaceBookLoginIn(){
        //Prevent form the reflection api.
        if (faceBookInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public void init(MainActivity activity, FirebaseAuth mAuth){

                    this.activity = activity;
                    this.mAuth = mAuth;
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

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            setUser(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            setUser(null);
                        }
                    }
                });
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