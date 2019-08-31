package com.example.securityapplication;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

//import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private TextView mStatus;
    private EditText mEmail;
    private EditText mPassword;
    private Button mSignInButton;

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if(i== R.id.signInButton){
            if(mAuth.getCurrentUser()==null)
                signIn(mEmail.getText().toString(),mPassword.getText().toString());
            else
                signOut();
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

        findViewById(R.id.signInButton).setOnClickListener(this);

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

    public void signIn(String email, String password){

        if(!validateForm()){
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                }
                                else{
                                    updateUI(null);
                                }
                            }
                        }
                );

    }

    private void signOut(){
        mAuth.signOut();
        updateUI(null);
    }

    private void updateUI(FirebaseUser user){
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
