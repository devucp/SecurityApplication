package com.example.securityapplication.model;

public class Email {

    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Email(){
        // Default constructor required for calls to DataSnapshot.getValue(Email.class)
    }

    public Email(String uid) {
        this.uid = uid;
    }

}
