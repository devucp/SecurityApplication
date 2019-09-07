package com.example.securityapplication;


import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;

public class InputValidation {
    private Context context;
    public InputValidation(Context context) {
        this.context = context;
    }

    public boolean is_Empty(TextInputEditText textInputEditText,
            TextInputLayout textInputLayout,String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.isEmpty()){
            textInputLayout.setError(message);
            return false;
        }
        else{
            return true;
        }

    }
}
