package com.example.securityapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.securityapplication.model.User;

import org.w3c.dom.Text;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edit_Name,edit_Email,edit_Phone,edit_Address;
    private String Name,Email,Phone,Address,FinName,FinEmail,FinPhone,FinAddress;
    private boolean Namechanged,Emailchanged,Phonechanged,Addresschanged;
    private boolean NameVal,AddressVal,EmailVal,PhoneVal;
    Validation val;
    User user;
    SQLiteDBHelper mydb;
    private Button Btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity_edit);

        mydb = SQLiteDBHelper.getInstance(this);
        val = new Validation();
        user = getIntent().getParcelableExtra("User");

        initialData();
        initViews();
        initListeners();
    }

    private void initialData() {
        Name=user.getName();
        Email= user.getEmail();
        Phone=user.getMobile();
        Address= user.getLocation();
    }

    private void initListeners() {

        edit_Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Toast.makeText(getApplicationContext(),"Before Name Change",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Toast.makeText(getApplicationContext(),"upon Name Change",Toast.LENGTH_LONG).show();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                FinEmail=edit_Email.getText().toString().trim();
                if (FinEmail.equals(Email)) {
                    Emailchanged = false;
                }
                else {
                    EmailVal = val.EditvalidateEmail(edit_Email);
                    Emailchanged = true;
                }
            }
        });
        edit_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                FinName=edit_Name.getText().toString().trim();
                if (FinName.equals(Name)) {
                    Namechanged = false;
                }
                else {
                    Namechanged = true;
                    NameVal = val.EditvalidateName(edit_Name);
                }
            }
        });
        edit_Phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                FinPhone=edit_Phone.getText().toString().trim();
                if (FinPhone.equals(Phone)) {
                    Phonechanged = false;
                }
                else {
                    PhoneVal = val.EditvalidatePhone(edit_Phone);
                    Phonechanged = true;
                }
            }
        });
        edit_Address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                FinAddress=edit_Address.getText().toString().trim();
                if (FinAddress.equals(Address)) {
                    Addresschanged = false;
                }
                else {
                    AddressVal = val.EditvalidateAddress(edit_Address);
                    if (AddressVal)
                        Addresschanged = true;
                    else
                        Addresschanged = false;
                }
            }
        });

       /* edit_Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    Toast.makeText(getApplicationContext(),"Lost Focus",Toast.LENGTH_SHORT).show();
                    FinName=edit_Name.getText().toString().trim();
                    if (FinName.equals(Name)) {
                         Namechanged = false;
                    }
                    else {
                        Namechanged = true;
                        NameVal = val.EditvalidateName(edit_Name);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Got Focus ", Toast.LENGTH_SHORT).show();
                }
            }
        });*/


        Btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Namechanged){
                    if (NameVal){
                        user.setName(FinName);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Enter a Valid Name",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (Phonechanged){
                    if (PhoneVal){
                        user.setMobile(FinPhone);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Enter a Valid Phone Number",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (Emailchanged){
                    if (EmailVal){
                        user.setEmail(FinEmail);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Enter a Valid Email",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (Addresschanged){
                    if (AddressVal){
                        user.setLocation(FinAddress);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Enter a Valid Locality",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Intent returnIntent = new Intent();
                Log.d("EditProfile","Changes Made "+user.getEmail());
                returnIntent.putExtra("ResultUser",user);
                setResult(110,returnIntent);
                finish();
            }
        });
    }

    private void initViews() {
        Btn_submit = (Button)findViewById(R.id.btn_Submit_Changes);
        edit_Email = (EditText)findViewById(R.id.edit_Email);
        edit_Name = (EditText)findViewById(R.id.edit_Name);
        edit_Phone = (EditText)findViewById(R.id.edit_Phone);
        edit_Address=(EditText)findViewById(R.id.edit_Address);

        edit_Address.setText(Address);
        edit_Name.setText(Name);
        edit_Phone.setText(Phone);
        edit_Email.setText(Email);
    }
}
