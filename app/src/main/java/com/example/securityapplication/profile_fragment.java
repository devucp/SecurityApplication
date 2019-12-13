package com.example.securityapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securityapplication.model.User;

import static com.facebook.FacebookSdk.getApplicationContext;

public class profile_fragment extends Fragment {
    private TextView textName,textEmail,textPhone,textAddress,textGender,textDob;
    private Button btn_edit;
    navigation nv=new navigation();
    SQLiteDBHelper mydb ;
    Database_Helper dbHelper;
    User user;
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
}
