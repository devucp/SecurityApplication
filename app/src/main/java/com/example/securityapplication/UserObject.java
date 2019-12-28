package com.example.securityapplication;

import com.example.securityapplication.model.User;

public class UserObject {
    public static User user=new User();
    public static String print(){
        String s=user.getDob()+" "+user.isPaid()+" "+user.getName()+" "+user.getGender();
        return s;
    }
}
