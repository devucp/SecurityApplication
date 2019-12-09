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
    private Integer alert;
    private Integer safe;
    private Integer emergency;


    private String SOS_MESSAGE;
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


        //initiateMessage();
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
        String toastmsg;

        if(alert==1)
        {
            toastmsg="send alert sos message";
        }
        else if(emergency==1)
        {
            toastmsg="send emergency sos message";
        }
        else
        {
            toastmsg="send safety sos message";
        }
        Toast.makeText(getApplicationContext(), toastmsg, Toast.LENGTH_LONG).show();
        this.stopSelf();//FINISH the service
    }

    public void sendMessage(String number,String location){
        if(alert==1)
        {
            SOS_MESSAGE="HELP";
        }
        else if(emergency==2)
        {
            SOS_MESSAGE="SAFE";
        }
        else
        {
            SOS_MESSAGE="EMERGENCY";

        }

        String messageToSend= getSenderName()+SOS_MESSAGE;
        if(location!=null)
                messageToSend+=" https://www.google.com/maps/place/";
        messageToSend+=location;


        SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null,null);



    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        alert=intent.getIntExtra("alert",0);
        safe=intent.getIntExtra("safe",0);
        emergency=intent.getIntExtra("emergency",0);

        Log.d("SOS SMS","alert is "+alert);
        Log.d("SOS SMS","safe is "+safe);
        Log.d("SOS SMS","emergency is "+emergency);

        initiateMessage();



        return super.onStartCommand(intent, flags, startId);


    }
}
