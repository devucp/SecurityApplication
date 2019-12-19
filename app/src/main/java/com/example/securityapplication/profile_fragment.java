package com.example.securityapplication;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securityapplication.Helper.FirebaseHelper;
import com.example.securityapplication.model.Device;
import com.example.securityapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;
import static com.example.securityapplication.R.layout.spinner_layout;

public class profile_fragment extends Fragment {

    private EditText textName,textEmail,textPhone,textDob;
    private AutoCompleteTextView textAddress;
    private Button btn_edit;
    private Button btn_logout;
    private TextView text_changePassword;
    SQLiteDBHelper mydb ;
    User user;
    private TelephonyManager telephonyManager;
    private String mImeiNumber;
    private int RC;
    private String TAG = "ProfileActivity";
    Spinner spinner;
    DatePickerDialog datePickerDialog;

    navigation nv=new navigation();
    private FirebaseHelper firebaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_profile,container,false);

        String [] values =
                {"Male","Female","Others"};
        spinner = (Spinner) v.findViewById(R.id.text_Gender);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), spinner_layout, values);
        adapter.setDropDownViewResource(spinner_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return v;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initObjects();
        initviews();
        //FetchAllData();
        DisplayData();
        initListeners();

        /**  Get FirebaseHelper Instance **/
        firebaseHelper = FirebaseHelper.getInstance();
        firebaseHelper.initFirebase();
        firebaseHelper.initContext(getActivity());
        firebaseHelper.initGoogleSignInClient(getString(R.string.server_client_id));

        deviceId();
    }

    private void initObjects() {

//        user = getIntent().getParcelableExtra("User");
        user = UserObject.user;
        mydb = new SQLiteDBHelper(getContext());
    }

    private void initListeners() {
        textDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c= java.util.Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                textDob.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
                                        @Override public void onClick(View view) {

                                            if (IsInternet.isNetworkAvaliable(getContext())) {

                                                if(btn_edit.getText().equals("edit"))
                                                {btn_edit.setText("Save");
                                                    enable();
                                                alphaa(1.0f);}
                                                else {
                                                    if(!validate())
                                                    {
                                                        Toast.makeText(getContext(), "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else {
                                                        // start progress bar


                                                        //save code will come here
                                                        user.setName(textName.getText().toString());
                                                        user.setDob(textDob.getText().toString());
                                                        user.setLocation(textAddress.getText().toString());
                                                        if (spinner.getSelectedItemPosition() == 0)
                                                            user.setGender("male");
                                                        else if (spinner.getSelectedItemPosition() == 1)
                                                            user.setGender("female");
                                                        else
                                                            user.setGender("others");

                                                        // check mobile number in firebase
                                                        checkMobileInFirebase(textPhone.getText().toString());
                                                    }
                                                }
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
                mydb.delete_table();
                signOut();

                //finishing the navigation activity
                getActivity().finish();
                //Clear the back stack and re-directing to the sign-up page
                Intent mLogOutAndRedirect= new Intent(getContext(),MainActivity.class);
                mLogOutAndRedirect.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mLogOutAndRedirect);

            }
        });

        text_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword(firebaseHelper.getFirebaseAuth().getCurrentUser().getEmail());
            }
        });
    }

    private boolean validate() {
        if(textName.getText().toString().length()>1 && textAddress.getText().toString().length()>1 && textPhone.getText().toString().length()==10)
            return true;
        else
            return false;
    }

    private void FetchAllData(){
        int i =0;
        Cursor res;
        res = mydb.getAllData();
        if (res.getCount() == 0){
            Toast toast = Toast.makeText(getContext(),
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
        int kk=0;
        if(user.getGender().equalsIgnoreCase("male"))
            kk=0;
        else if(user.getGender().equalsIgnoreCase("female"))
            kk=1;
        else
            kk=2;
        spinner.setSelection(kk);
        textAddress.setText(user.getLocation());
        textEmail.setText(user.getEmail());
        textPhone.setText(user.getMobile());

        Log.d("Profile","DATA displayed on profile Successfully");
    }
    private void disable(){
        textName.setEnabled(false);
        spinner.setEnabled(false);
        textEmail.setEnabled(false);
        textPhone.setEnabled(false);
        textAddress.setEnabled(false);
        textDob.setEnabled(false);
    }
    private void alphaa(float k){
        spinner.setAlpha(k);
        textName.setAlpha(k);
        textPhone.setAlpha(k);
        textAddress.setAlpha(k);
        textDob.setAlpha(k);
    }
    private void enable(){
        spinner.setEnabled(true);
        textName.setEnabled(true);
        textPhone.setEnabled(true);
        textAddress.setEnabled(true);
        textDob.setEnabled(true);
    }

    private void initviews() {
        textName =getActivity().findViewById(R.id.text_Name);
        textEmail = getActivity().findViewById(R.id.text_Email);
        textPhone = getActivity().findViewById(R.id.text_Phone);
        textAddress = getActivity().findViewById(R.id.text_Address);
        //textGender = getActivity().findViewById(R.id.text_Gender);
        textDob = getActivity().findViewById(R.id.text_DOB);
        btn_edit = getActivity().findViewById(R.id.btn_Edit);
        btn_logout = getActivity().findViewById(R.id.btn_Logout);
        Resources res = getResources();
        String[] Locality = res.getStringArray(R.array.Locality);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,Locality);
        textAddress.setAdapter(adapter);
        text_changePassword = getActivity().findViewById(R.id.text_changePassword);
        disable();


//        String[] gender = new String[]{
//                "Male",
//                "Female",
//                "Others"
//        };
//        Spinner sp=getActivity().findViewById(R.id.text_Gender);
//       spinnerArrayAdapter = new ArrayAdapter<String>(
//                this.getActivity(), spinner_layout, gender);
//        spinnerArrayAdapter.setDropDownViewResource(spinner_layout);
//        sp.setAdapter(spinnerArrayAdapter);
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
        if (mImeiNumber == null) {
            deviceId();
            return;
        }

        firebaseHelper.getUsersDatabaseReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeEventListener(navigation.mUsersDatabaseReferenceListener);
        firebaseHelper.firebaseSignOut(mImeiNumber);
        firebaseHelper.googleSignOut(getActivity());
        //delete user records from SQLite
        mydb.deleteDatabase(getContext());

        try{
            Intent mStopSosPlayer=new Intent(getContext(),SosPlayer.class);
            mStopSosPlayer.putExtra("stop",1);
            getActivity().startService(mStopSosPlayer); //previously was stopService(). Now using startService() to use the stop extra in onStartCommand()
            Log.d("Profile Fr","Service sosplayer new startIntent...");
            Toast.makeText(getContext(),"Service sosplayer stopping...",Toast.LENGTH_SHORT).show();
        }
        catch(Exception e) {
            Log.d("Profile Fr","Service SOSplayer is not running");
        }
    }

    private void checkMobileInFirebase(final String newMobile){
        firebaseHelper.getUsersDatabaseReference().child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
                final User oldUser = userDataSnapshot.getValue(User.class);
                if (oldUser.getMobile().equals(newMobile)){
                    // update user in sqlite and firebase
                    updateUser();
                }else {
                    firebaseHelper.getMobileDatabaseReference().child(newMobile).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot mobileNodeDataSnapshot) {
                            Log.d("Mobile Data Snapshot:", mobileNodeDataSnapshot.toString());
                            if (mobileNodeDataSnapshot.exists()) {
                                // stop progress bar

                                // prompt user to enter different mobile number
                                Toast.makeText(getActivity(), "Mobile number is registered to another account",Toast.LENGTH_LONG).show();

                            } else {
                                // delete previous mobile number and add new number
                                firebaseHelper.getMobileDatabaseReference().child(oldUser.getMobile()).setValue(null);
                                firebaseHelper.getMobileDatabaseReference().child(newMobile).setValue(FirebaseAuth.getInstance().getUid());
                                updateUser();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUser(){

        // stop progress bar

        Log.d(TAG,"Updating user...");

        user.setMobile(textPhone.getText().toString());

        //mydb.updateUser(user);
        firebaseHelper.updateuser_infirebase(FirebaseAuth.getInstance().getUid(),user);

        btn_edit.setText("edit");
        alphaa(0.6f);
        disable();
    }

    private void changePassword(String email){
        if (!IsInternet.checkInternet(getContext()))
            return;

        //pgbarshow();
        firebaseHelper.getFirebaseAuth().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(),"EMAIL SENT. PLEASE CHECK YOUR MAIL TO CHANGE PASSWORD",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            String error = e.getMessage().split("\\.")[0];
                            Log.d(TAG,e.getMessage());
                            Toast.makeText(getActivity(),error,Toast.LENGTH_LONG).show();
                        }
                    }
                    //pgbarhide();
                }
        });
    }
}
