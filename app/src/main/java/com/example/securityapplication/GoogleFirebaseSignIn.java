package com.example.securityapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.Serializable;

public class GoogleFirebaseSignIn implements Serializable {

    private FirebaseAuth mAuth;
    private MainActivity activity;
    private FirebaseUser user;
    private static final String TAG = "GoogleSignIn";
    //private static final int RC_SIGN_IN = 9001;

    private static volatile GoogleFirebaseSignIn googleFirebaseInstance;

    //private constructor
    private GoogleFirebaseSignIn(){
        //Prevent form the reflection api.
        if (googleFirebaseInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public void init(MainActivity activity, FirebaseAuth mAuth){

        this.activity = activity;
        this.mAuth = mAuth;

    }

    public static GoogleFirebaseSignIn getInstance() {
        //Double check locking pattern
        if (googleFirebaseInstance == null) { //Check for the first time..if there is no instance available... create new one
            synchronized (FaceBookLoginIn.class) { //Check for the second time to make ThreadSafe
                //if there is no instance available... create new one
                if (googleFirebaseInstance == null)
                    googleFirebaseInstance = new GoogleFirebaseSignIn();
            }
        }
        return googleFirebaseInstance;
    }

    //Make singleton from serialize and deserialize operation.
    protected GoogleFirebaseSignIn readResolve() {
        return getInstance();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
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
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            setUser(null);
                        }
                    }
                });
    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            setUser(null);
        }
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
