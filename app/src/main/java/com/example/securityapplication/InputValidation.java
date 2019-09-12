package com.example.securityapplication;


import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Patterns;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public boolean is_Valid(TextInputEditText textInputEditText, int maxlength){
        boolean valid = textInputEditText.getText().toString().length()<maxlength;
        String value =  textInputEditText.getText().toString();
        String regex = "^[0-9]+$";
        Matcher matcher = Pattern.compile( regex ).matcher(value);
        if (valid & matcher.find()){
            return false;
        }
        else{
            return true;
        }

    }
}
