package com.example.securityapplication.model;

public class Mobile {

    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Mobile(){
        // Default constructor required for calls to DataSnapshot.getValue(Email.class)
    }

    public Mobile(String uid) {
        this.uid = uid;
    }

}
