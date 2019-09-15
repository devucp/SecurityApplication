package com.example.securityapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LinkAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private SignInButton mGoogleLinkAccountButton;
    //private LoginButton mFaceBookLinkAccountButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private String TAG = "LinkAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_account);

        findViewById(R.id.linkFacebookAccountButton).setOnClickListener(this);
        mGoogleLinkAccountButton = findViewById(R.id.linkGoogleAccountButton);
        mGoogleLinkAccountButton.setOnClickListener(this);

        //set text of GOOGLE LINK BUTTON
        Integer googleButtonCount = mGoogleLinkAccountButton.getChildCount();
        Log.d(TAG,mGoogleLinkAccountButton.getChildAt(0).toString());
        View v = mGoogleLinkAccountButton.getChildAt(0);
        if(v instanceof TextView) {
            TextView tv = (TextView) v;
            tv.setText("Link Google Account");
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth= FirebaseAuth.getInstance();
    }

    private void linkGoogleAccount(GoogleSignInAccount acct) {

        // Link the anonymous user to the email credential
        //showProgressDialog();
        AuthCredential credential= GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        // [START link_credential]
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(LinkAccountActivity.this, "Account Linked Successfully", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            String[] exceptionSplitted = task.getException().toString().split(":");
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(LinkAccountActivity.this, exceptionSplitted[exceptionSplitted.length-1], Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END link_credential]
    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            linkGoogleAccount(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "Linking failed code=" + e.getStatusCode());
            Toast.makeText(LinkAccountActivity.this, "Linking Failed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        // Pass the activity result back to the Facebook SDK
        //callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.linkGoogleAccountButton:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;

            case R.id.linkFacebookAccountButton:
                //linkFaceBookAccount();
                break;
        }

    }
}
