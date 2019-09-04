package com.example.securityapplication;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.concurrent.Executor;

//import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, FaceBookLoginIn.OnFaceBookLoginListener{

    private FirebaseAuth mAuth;
    private TextView mStatus;
    private EditText mEmail;
    private EditText mPassword;
    private Button mSignInButton;

    private LoginButton faceBookLoginButton;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    //
    private FaceBookLoginIn faceBookLoginIn;

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i== R.id.signInButton){
            if(mAuth.getCurrentUser()==null)
                signIn(mEmail.getText().toString(),mPassword.getText().toString());
            else {
                signOut();
                if(GoogleSignIn.getLastSignedInAccount(this) != null) {
                    Intent intent = new Intent(this, GoogleSignInActivity.class);
                    startActivity(intent);
                }
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedInFaceBook = accessToken != null && !accessToken.isExpired();
                if(isLoggedInFaceBook){
                    LoginManager.getInstance().logOut();
                }
                updateUI(null);
            }
        }
        else if (i==R.id.googleSignInButton){
            Intent intent = new Intent(this, GoogleSignInActivity.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail=findViewById(R.id.editEmail);
        mPassword=findViewById(R.id.editPassword);
        mStatus= findViewById(R.id.status);
        mSignInButton=findViewById(R.id.signInButton);
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();

        faceBookLoginIn = FaceBookLoginIn.getInstance();
        faceBookLoginIn.init(MainActivity.this, mAuth);

        findViewById(R.id.signInButton).setOnClickListener(this);
        findViewById(R.id.googleSignInButton).setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();
        faceBookLoginButton = findViewById(R.id.facebook_login_button);
        faceBookLoginButton.setPermissions(Arrays.asList(EMAIL));

        // Callback registration
        faceBookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FB", "facebook:onSuccess:" + loginResult);
                faceBookLoginIn.setOnFaceBookLoginListener((FaceBookLoginIn.OnFaceBookLoginListener)faceBookLoginIn);
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
    }

    public void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
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

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void signIn(String email, String password){

        if(!validateForm()){
            return;
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                            this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        updateUI(null);
                                    }
                                }
                            }
                    );
        }
    }

    private void signOut(){
        mAuth.signOut();
    }

    public void updateUI(FirebaseUser user){
        if(user==null){
            mStatus.setText(R.string.not_logged);
            mSignInButton.setText(R.string.sign_in_text);
            mEmail.setVisibility(View.VISIBLE);
            mPassword.setVisibility(View.VISIBLE);
        }
        else if(user!=null){
            mStatus.setText(R.string.logged);
            mSignInButton.setText(R.string.sign_out_text);
            mEmail.setVisibility(View.GONE);
            mPassword.setVisibility(View.GONE);
        }
    }
}
