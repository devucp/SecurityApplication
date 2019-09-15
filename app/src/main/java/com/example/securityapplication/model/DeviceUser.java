package com.example.securityapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceUser implements Parcelable {

    private String uid;

    public DeviceUser(){}

    protected DeviceUser(Parcel in) {
        uid = in.readString();
    }

    public static final Creator<DeviceUser> CREATOR = new Creator<DeviceUser>() {
        @Override
        public DeviceUser createFromParcel(Parcel in) {
            return new DeviceUser(in);
        }

        @Override
        public DeviceUser[] newArray(int size) {
            return new DeviceUser[size];
        }
    };

    public String getUID() {
        return uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
    }
}
