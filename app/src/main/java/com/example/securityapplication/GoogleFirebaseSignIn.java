package com.example.securityapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.example.securityapplication.Helper.FirebaseHelper;
import com.example.securityapplication.model.Device;
import com.example.securityapplication.model.User;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public class GoogleFirebaseSignIn implements Serializable {

    private Activity activity;
    private static final String TAG = "GoogleSignIn";
    //private static final int RC_SIGN_IN = 9001;

    private static volatile GoogleFirebaseSignIn googleFirebaseInstance;

    private String mImeiNumber;
    private Device device;
    private FirebaseHelper firebaseHelper;
    private FirebaseUser firebaseUser;

    //private constructor
    private GoogleFirebaseSignIn(){
        //Prevent form the reflection api.
        if (googleFirebaseInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public void init(Activity activity, String imei){

        this.activity = activity;
        this.mImeiNumber = imei;
        firebaseHelper = FirebaseHelper.getInstance();
        firebaseHelper.initFirebase();
    }

    public static GoogleFirebaseSignIn getInstance() {
        //Double check locking pattern
        if (googleFirebaseInstance == null) { //Check for the first time..if there is no instance available... create new one
            synchronized (GoogleFirebaseSignIn.class) { //Check for the second time to make ThreadSafe
                //if there is no instance available... create new one
                if (googleFirebaseInstance == null) {
                    googleFirebaseInstance = new GoogleFirebaseSignIn();
                    Log.d(TAG,"Created new googleFirebaseInstance");
                }
            }
        }
        else {
            Log.d(TAG,"googleFirebaseInstance Exists");
        }
        return googleFirebaseInstance;
    }

    //Make singleton from serialize and deserialize operation.
    protected GoogleFirebaseSignIn readResolve() {
        return getInstance();
    }

    public void linkGoogleAccount(GoogleSignInAccount acct) {
        Log.d(TAG,"Inside linkGoogleAccount");
        if (acct == null)
            return;
        // Link the anonymous user to the email credential
        //showProgressDialog();
        AuthCredential credential= GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        // [START link_credential]
        firebaseHelper.getFirebaseAuth().getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            // change isGoogleAccountLinked status in firebase database to true
                            changeLinkedStatus(firebaseUser);

                            //Toast.makeText(LinkAccountActivity.this, "Account Linked Successfully", Toast.LENGTH_SHORT).show();

                            return;
                        } else {
                            String[] exceptionSplitted = task.getException().toString().split(":");
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            //Toast.makeText(LinkAccountActivity.this, exceptionSplitted[exceptionSplitted.length-1], Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END link_credential]
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG,"Inside firebaseAuthWithGoogle");
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getEmail());
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseHelper.getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d(TAG, "onComplete: " + (isNew ? "new user" : "old user"));

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser firebaseUser = firebaseHelper.getFirebaseAuth().getCurrentUser();

                            // set imei and uid in firebase
                            device = new Device();
                            device.setUID(firebaseUser.getUid());
                            firebaseHelper.getDevicesDatabaseReference().child(mImeiNumber).setValue(device);
                            firebaseHelper.getUsersDatabaseReference().child(firebaseUser.getUid()).child("imei").setValue(mImeiNumber);
                            // change isGoogleAccountLinked status in firebase database to true
                            changeLinkedStatus(firebaseUser);
                            storeData(firebaseUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.layout.activity_main), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            setUser(null);
                        }
                    }
                });
    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // 1. Check whether user exists in database.
            // 2. If not then don't link with firebase, first redirect to sign up page.
            // 3. If yes then check for imei number of current device and registered device and also the auth provider of registered user
            // 4. If imei matches, then device is old device(old user) and user can login with firebase by firebaseAuthWithGoogle(account) but before that:
            //    If auth provider of current user and registered user are different then link both providers and then :
            //      user can login with firebase by firebaseAuthWithGoogle(account)
            // 5. If imei don't match, then device is new device(old user)
            //      and user needs to logout from old device.If logged out, change value of isLoggedIn status of old device in Database to false
            // 6.

            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //setUser(null);
        }
    }

    private void setUser(FirebaseUser firebaseUser){
        this.firebaseUser = firebaseUser;
        if (firebaseUser != null){
            Intent mHomeIntent = new Intent(activity,navigation.class);
            activity.startActivity(mHomeIntent);
            try {
                closeNow();
            }catch (Exception e){
                Log.d(TAG,"Exception on closing activity:"+e.getMessage());
                activity.finish();
            }
        }
    }

    private void closeNow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            activity.finishAffinity();
        }
        else{
            activity.finish();
        }
    }

    private void changeLinkedStatus(FirebaseUser firebaseUser){
        firebaseHelper = FirebaseHelper.getInstance();
        firebaseHelper.initFirebase();
        firebaseHelper.getUsersDatabaseReference().child(firebaseUser.getUid()).child("googleAccountLinked").setValue(true);
    }

    private void storeData(final FirebaseUser firebaseUser){
        if (firebaseUser != null) {
            final String uid = firebaseUser.getUid();
            firebaseHelper.getUsersDatabaseReference().child(uid).addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d("Paid12345","schin1"+user.getName()+user.isPaid());
                    SQLiteDBHelper db=new SQLiteDBHelper(activity);

                    db.addUser(user);
                    db.setUser(user);
                    if (user.getSosContacts() != null)
                        db.addsosContacts(user.getSosContacts(),1); //to fetch SOSContacts from Firebase

                    Log.d("Paid12345","schin"+user.getName()+ user.isPaid());
                    if(dataSnapshot.getValue(User.class).isPaid()){
                        Log.d("Paid12345","i am here");
                        home_fragment.setpaid(true);
                    }
                    else{
                        home_fragment.setpaid(false);
                    }
                    setUser(firebaseUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
