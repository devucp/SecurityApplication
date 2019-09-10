package com.example.securityapplication;


import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.widget.TextView;

public class InputValidation {
    private Context context;
    public InputValidation(Context context) {
        this.context = context;
    }

    public boolean is_Empty(TextInputEditText textInputEditText,
                            TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.isEmpty()){
           textInputEditText .setError(message);
            return true;
        }
        else{

            return false;
        }

    }
}
