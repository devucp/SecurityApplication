package com.example.securityapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SendSMSService extends Service {
    private String[] contactList=null; //TODO: stores the number of the emergency contacts
    private String senderName;
    private String location;
    private String SOS_MESSAGE=" IS IN AN EMERGENCY AND NEEDS YOUR HELP.";
    public SendSMSService() {
    }

    public void updateLocation() {
        this.location = GetGPSCoordinates.getLastKnownLocation();
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }


    public String getSenderName() {
        return senderName;
    }

    //TODO: Initiliase the list with contact numbers of the user
    public void setContactList(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initiateMessage();
    }
    public void initiateMessage(){
        updateLocation();
        Log.d("SOS SMS","Location is"+location);
        //call setContactList
        if(contactList==null){
            //filling DUMMY values
            String number="9673153564";
            setSenderName("DG");
            if(location==null){
                location="Location unavailable";
            }
            sendMessage(number,location);

        }
        else{


            for(int i=0;i<contactList.length;i++){

                if(location==null)
                    sendMessage(contactList[i],"Location unavailable");
                else
                    sendMessage(contactList[i],location);
            }
        }
        Toast.makeText(getApplicationContext(), "Sent SOS Messages", Toast.LENGTH_LONG).show();
        this.stopSelf();//FINISH the service
    }

    public void sendMessage(String number,String location){
        String messageToSend= getSenderName()+SOS_MESSAGE;
        if(location!=null)
                messageToSend+=" https://www.google.com/maps/place/";
        messageToSend+=location;
        SmsManager.getDefault().sendTextMessage(number, null, "help", null,null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
