package com.example.securityapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;

/*
        *NOTE: Earlier TextInputLayout was used as parameter for all validation function
        *They have been changed to TextInputEditText
        * and the functions TextInputLayout.getEditText() have been replaced to just TextInputEditText.getText()
*/
public class SignUp1Activity extends AppCompatActivity {
//
   Database_Helper myDb;
    Validation val = new Validation();
    private Button Btn_Submit;
    //Added user object to send to next
    private User user;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEmailDatabaseReference;
    private String uid;

    private Button verifyEmailButton;
    private VerifyEmail verifyEmail;
    private TextInputEditText textinputEmail,textinputPass,textinputCnfPass; // was earlier TextInputLayout

    private String TAG = "SignUp1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);


       myDb = new Database_Helper(this);
       java.util.Calendar calendar=Calendar.getInstance();
      final int year=calendar.get(Calendar.YEAR);

      //removed most the view castings as they're unnecessary
        textinputEmail = findViewById(R.id.textlayout_Email);
        textinputPass =  findViewById(R.id.textlayout_Pass);
        textinputCnfPass = findViewById(R.id.textlayout_CnfPass);
        Btn_Submit = findViewById(R.id.btn_sub);
        verifyEmailButton = findViewById(R.id.btn_verify);

        user=new User();

        // check if user is signed in to google or facebook
        if (GoogleSignIn.getLastSignedInAccount(this) != null){
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                //String personGivenName = acct.getGivenName();
                //String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                //String personId = acct.getId();
                //Uri personPhoto = acct.getPhotoUrl();
                Log.d("Usernanme",personName);
                Log.d("Email",personEmail);
                if (personEmail != null) {
                    textinputEmail.setText(personEmail);
                    textinputEmail.setEnabled(false);
                }
            }
        }
        else
            Log.d("isLoggedinGoogle","Not logged in");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        initDatabase();
    }

    private void initDatabase(){
        mEmailDatabaseReference = mFirebaseDatabase.getReference().child("Email");
    }

    private void ShowMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
    }

    public Hashtable<String, String> Validater(String userSelected) {
        if (val.validateEmail(textinputEmail) & val.validatePassword(textinputPass) & val.validateCnfPassword(textinputPass,textinputCnfPass)){
            Hashtable<String,String> userData = new Hashtable<>();
            userData.put("email",textinputEmail.getText().toString().trim());
            userData.put("password", textinputPass.getText().toString().trim());
            userData.put("userSelected", userSelected);
            return userData;
        }
        else {
            Toast.makeText(this,"Enter Valid Credentials",Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void verifyEmailId(View view){

        Hashtable<String,String> userData =  Validater("verifyEmailId");
        if (userData != null){
            createUserAndVerifyEmail(userData);
        }
    }

    public void signUp(View view){

        // disable screen and show spinner
        //
        Hashtable<String,String> userData = Validater("signUp");
        if (userData != null){
            setUidFromFirebase(userData);
        }
    }

    private void createUserAndVerifyEmail(final Hashtable<String,String> userData){

        mAuth.createUserWithEmailAndPassword(userData.get("email"), userData.get("password"))
                .addOnCompleteListener(SignUp1Activity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            // check is email verified if clicked on signup and send verify email if clicked on verifyBtn
                            checkIsEmailVerified(firebaseUser, userData.get("userSelected"));

                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e){
                                //signIn the user
                                signIn(userData);
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                Log.d(TAG,e.getMessage());
                                Toast.makeText(SignUp1Activity.this,"Invalid Password",Toast.LENGTH_SHORT).show();
                            } catch (Exception e){
                                Log.e(TAG,e.getMessage());
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUp1Activity.this, "Authentication failed.Please check your connection and try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void signIn(final Hashtable<String,String> userData){
        Log.d(TAG,"Signing IN user with email "+userData.get("email")+" and password "+userData.get("password"));

        mAuth.signInWithEmailAndPassword(userData.get("email"), userData.get("password"))
                .addOnCompleteListener(SignUp1Activity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d(TAG, "onComplete: " + (isNew ? "new user" : "old user"));

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            checkIsEmailVerified(firebaseUser, userData.get("userSelected"));
                        } else {
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                Log.d(TAG,e.getMessage());
                                Toast.makeText(SignUp1Activity.this,"Invalid Password",Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Log.d(TAG, "Exception while signIN:"+e.getMessage());
                                Toast.makeText(SignUp1Activity.this,"Authentication failed. Please check connection and try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void checkIsEmailVerified(FirebaseUser firebaseUser, String userSelected){

        verifyEmail = new VerifyEmail(firebaseUser, SignUp1Activity.this);
        if (verifyEmail.isEmailIdVerified()) {
            Toast.makeText(SignUp1Activity.this, "EmailId is verified", Toast.LENGTH_LONG).show();
            String emailId = firebaseUser.getEmail();
            signOut();
            // set uid from firebase
            if (userSelected.equals("signUp")){
                // can proceed to signUp2
                AddData();
            }
        }
        else {
            Log.d(TAG,userSelected);
            if (userSelected.equals("verifyEmailId"))
                verifyEmail.sendVerificationEmail();
            else
                signOut();
            Toast.makeText(SignUp1Activity.this, "EmailId not verified",Toast.LENGTH_SHORT).show();
        }

    }

    private void signOut(){
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
            //Toast.makeText(this, "Logged Out from Firebase", Toast.LENGTH_SHORT).show();
        }
    }

    private void AddData() {

        //Sending the user object
        myDb.setUser(user);

       Boolean isInserted = myDb.insert_data(textinputEmail.getText().toString().trim(), textinputPass.getText().toString().trim());
        if (isInserted) {
         /*   textinputName.setText(null);
            gender_grp.clearCheck();
            textinputDOB.setText(null);
            textinputEmail.setText(null);
            textinputPass.setText(null);
            textinputCnfPass.setText(null);
           // updates the Usr object with filled fields*/
            user=myDb.getUser();
            Log.d("User",user.getEmail().toString());
            //starting signup activity
            Intent intent=new Intent(SignUp1Activity.this,SignUp2.class);
            intent.putExtra("User",user);
            startActivityForResult(intent,1);
        }
        else {
            String UserEmail = textinputEmail.getText().toString().trim();
            boolean res = myDb.CheckUserEmail(UserEmail);
            if (res){
                Toast.makeText(this,"Email already taken",Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(this,"User Entry Unsuccessful",Toast.LENGTH_SHORT).show();
           }
        }
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==10 && requestCode==1){
            user=data.getParcelableExtra("ResultIntent");
            Intent i = new Intent(this,ProfileActivity.class);
            i.putExtra("User",user);
            startActivity(i);
            finish();
        }
        if (requestCode==2){
            Toast.makeText(SignUp1Activity.this, "Please fill the required details", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("hasBackPressed",true);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    private void setUidFromFirebase(final Hashtable<String,String> userData){
        // replace "." with "," in email id to store in firebase db as key
        String email = userData.get("email");
        email = TextUtils.join(",", Arrays.asList(email.split("\\.")));
        Log.d(TAG,email);
        Log.d(TAG,mEmailDatabaseReference.toString());
        mEmailDatabaseReference.child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot emailNodeDataSnapshot) {
                Log.d("Email Data Snapshot:", emailNodeDataSnapshot.toString());
                if (emailNodeDataSnapshot.exists()) {
                    //emailNode = emailNodeDataSnapshot.getValue(Email.class);
                    uid = emailNodeDataSnapshot.getValue().toString();
                    Log.d(TAG,uid);
                } else {
                    uid = null;
                }
                // check in firebase db if email is registered
                validateBeforeSignUp2(userData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validateBeforeSignUp2(Hashtable<String,String> userData){

        Log.d(TAG,"Inside validateBeforeSignUp2 with uid="+uid);
        if (uid == null){
            createUserAndVerifyEmail(userData);
            Log.d(TAG,"Email not stored in email node in firebase db");
        }
        else{
            Toast.makeText(SignUp1Activity.this, "Email Id is already registered",Toast.LENGTH_LONG).show();
        }
    }
}
