package com.example.securityapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securityapplication.model.Device;
import com.example.securityapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.support.v4.content.ContextCompat.getSystemService;
import static com.facebook.FacebookSdk.getApplicationContext;

public class profile_fragment extends Fragment {

    private TextView textName,textEmail,textPhone,textAddress,textGender,textDob;
    private Button btn_edit;
    private Button btn_logout;
    SQLiteDBHelper mydb ;
    Database_Helper dbHelper;
    User user;
    Device device;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDevicesDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private FirebaseAuth mAuth;
    private TelephonyManager telephonyManager;
    private String mImeiNumber;
    private int RC;
    private String TAG = "ProfileActivity";


    navigation nv=new navigation();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        user = new User();

        initObjects();
        initviews();
        FetchAllData();
        DisplayData();
        initListeners();

        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        initDataBaseReferences();
    }

    private void initObjects() {

//        user = getIntent().getParcelableExtra("User");
        mydb = new SQLiteDBHelper(getContext());
    }

    private void initListeners() {
        btn_edit.setOnClickListener(new View.OnClickListener() {
                                        @Override public void onClick(View view) {
                                            if (IsInternet.isNetworkAvaliable(getContext())) {
                                                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                                                intent.putExtra("User", user);
                                                startActivityForResult(intent, 1);
                                            }//Sending Data to EditProfileActivity
                                            else {
                                                Toast.makeText(getContext(), "Please check your Internet Connectivity", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                    }
        );

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                Log.d("signout","signout happen");
                signOut();

                //finishing the navigation activity
                getActivity().finish();
                //Clear the back stack and re-directing to the sign-up page
                Intent mLogOutAndRedirect= new Intent(getApplicationContext(),MainActivity.class);
                mLogOutAndRedirect.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mLogOutAndRedirect);

            }
        });
    }

    private void initDataBaseReferences(){
        //Initialize Database
        mDevicesDatabaseReference = mFirebaseDatabase.getReference().child("Devices");
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("Users");

    }


    private void FetchAllData(){
        int i =0;
        Cursor res;
        res = mydb.getAllData();
        if (res.getCount() == 0){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No User Data Found",
                    Toast.LENGTH_LONG);
            toast.show();
            Log.d("Profile","No Data found");
        }
//        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()){
            user.setName(res.getString(1));
            user.setEmail(res.getString(2));
            user.setGender(res.getString(3));
           user.setMobile(res.getString(4));
//            ansAadhaar = res.getString(6);
            user.setLocation(res.getString(5));
            user.setDob(res.getString(6));
            i++;
            Log.d("Profile Activity","User Object set in Profile activity successfully" +i);
        }
    }

    private void DisplayData() {

        textName.setText(user.getName());
//        textAadhaar.setText(ansAadhaar);
        textDob.setText(user.getDob());
        textGender.setText(user.getGender());
        textAddress.setText(user.getLocation());
        textEmail.setText(user.getEmail());
        textPhone.setText(user.getMobile());

        Log.d("Profile","DATA displayed on profile Successfully");
    }

    private void initviews() {
        textName =getActivity().findViewById(R.id.text_Name);
        textEmail = getActivity().findViewById(R.id.text_Email);
        textPhone = getActivity().findViewById(R.id.text_Phone);
        textAddress = getActivity().findViewById(R.id.text_Address);
        textGender = getActivity().findViewById(R.id.text_Gender);
        textDob = getActivity().findViewById(R.id.text_DOB);
        btn_edit = getActivity().findViewById(R.id.btn_Edit);
        btn_logout = getActivity().findViewById(R.id.btn_Logout);
//        textAadhaar = findViewById(R.id.text_Aadhaar);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            if (resultCode == 110){
                user = data.getParcelableExtra("ResultUser");
                Log.d("Profile","User object returned"+user.getEmail());
                mydb.updateUser(user);
                DisplayData();
            }
        }
    }

    private void deviceId() {
        telephonyManager = (TelephonyManager) getActivity().getSystemService(getContext().TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
            return;
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mImeiNumber = telephonyManager.getImei(0);
                Log.d("IMEI", "IMEI Number of slot 1 is:" + mImeiNumber);
            }
            else {
                mImeiNumber = telephonyManager.getDeviceId();
            }
        }



        //Log.d("MAinActivity","SMS intent");
        //check permissions

        while(!checkSMSPermission());
    }

    public  boolean checkSMSPermission(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(), "Permission Required for sending SMS in case of SOS", Toast.LENGTH_LONG).show();
            Log.d("MainActivity", "PERMISSION FOR SEND SMS NOT GRANTED, REQUESTING PERMSISSION...");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS}, RC);
        }
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED;
    }

    private void closeNow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            //finishAffinity();
        }
        else{
            //finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    deviceId();
                } else {
                    closeNow();
                    Toast.makeText(getContext(), "Without permission we check", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void signOut(){
        Log.d(TAG,"Inside signout");
        // first make uid under imei null in Devices and imei under uid null in Users
        deviceId();
        device = new Device();
        device.setUID("null");
        mDevicesDatabaseReference.child(mImeiNumber).setValue(device);

        //Firebase signOut
        if (mAuth.getCurrentUser() != null) {
            mUsersDatabaseReference.child(mAuth.getUid()).child("imei").setValue("null");
            mAuth.signOut();
            Toast.makeText(getContext(), "Logged Out from Firebase", Toast.LENGTH_SHORT).show();
        }
        //Google signOut
        /*if(GoogleSignIn.getLastSignedInAccount(this) != null) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //updateUI(null);
                            //Toast.makeText(MainActivity.this,"Logged Out from Google",Toast.LENGTH_SHORT).show();
                        }
                    });
        }*/
    }
}
