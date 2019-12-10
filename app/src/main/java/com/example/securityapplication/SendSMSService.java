package com.example.securityapplication;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class SendSMSService extends Service {
    private String[] contactList=null; //TODO: stores the number of the emergency contacts
    private String senderName;
    private String location;
    private String SOS_MESSAGE=" NEEDS YOUR HELP. PLEASE HELP THEM." +
                                "THE ALERT WAS SENT FROM ";

    private sentReceiver sentReceiver;
    private deliveryReceiver deliveryReceiver;

    //code for checking if delivery proper
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

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

        //---when the SMS has been sent---
        sentReceiver= new sentReceiver();
        registerReceiver(sentReceiver,new IntentFilter(SENT));
        //---when the SMS has been delivered---
        deliveryReceiver= new deliveryReceiver();
        registerReceiver(deliveryReceiver,new IntentFilter(DELIVERED));
        Log.d("SOS SMS","onCreate");
        initiateMessage();
    }
    public void initiateMessage(){
        updateLocation();

        Log.d("SOS SMS","Location is"+location);
        //call setContactList
        if(contactList==null){
            //filling DUMMY values
            String number="9082021653";
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



        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        ArrayList<String> parts = SmsManager.getDefault().divideMessage(messageToSend);

        ArrayList<PendingIntent> sendList = new ArrayList<>();
        sendList.add(sentPI);

        ArrayList<PendingIntent> deliverList = new ArrayList<>();
        deliverList.add(deliveredPI);



        SmsManager.getDefault().sendMultipartTextMessage(number, null, parts, sendList,deliverList);
        Log.d("SOS SMS","sendMessage() end");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sentReceiver);
        unregisterReceiver(deliveryReceiver);

    }
}
