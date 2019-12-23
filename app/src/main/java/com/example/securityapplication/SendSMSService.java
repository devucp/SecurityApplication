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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SendSMSService extends Service {
    private static String[] contactList=null; //TODO: stores the number of the emergency contacts
    private String senderName;
    private String location;
    private static Integer alert;
    private static Integer safe;
    private static Integer emergency;


    private String SOS_MESSAGE="You are my SOS Contact." ;


    private sentReceiver sentReceiver;
    private deliveryReceiver deliveryReceiver;


    //code for checking if delivery proper
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    private static boolean contactsFetched=false; //checks whether sos contacts have been initialised

    public SendSMSService() {
    }

    public void updateLocation() {
        this.location = GetGPSCoordinates.getLastKnownLocation();
    }


    public static int getAlert(){
        return alert;
    }
    public static int getEmergency(){
        return emergency;
    }

    public static int getSafe(){
        return safe;
    }

    public void initSenderName() {
        try{
        this.senderName= navigation.newUser.getName();}
        catch(Exception e){
            Toast.makeText(this,"initialising sender name error"+e.getMessage(),Toast.LENGTH_LONG);
            //initSenderName(); //re-try
        }
    }


    public String getSenderName() {
        return senderName;
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
    }

    /**Check if testmode was active when called**/
    public boolean checkTestMode(){
        SQLiteDBHelper sqLiteDBHelper= new SQLiteDBHelper(this);
        Log.d("SOS SMS","checktestmode():"+sqLiteDBHelper.getTestmode());
        return sqLiteDBHelper.getTestmode();
    }

    /**Initialise the SOS contacts**/
    public static void initContacts(){
        Log.d("SendSMSService","initContacts:");
        //check if HashMap was initialised on call
        try {
            HashMap<String, String> sosContacts = navigation.newUser.getSosContacts();
            Log.d("SendSMSServcie", "SosContacts size:" + sosContacts.size());
            if (sosContacts.size() != 0) {
                contactList = new String[sosContacts.size()];
                Iterator sosContactsIterator = sosContacts.entrySet().iterator();
                int i = 0;
                while (sosContactsIterator.hasNext()) {
                    Map.Entry contactEntry = (Map.Entry) sosContactsIterator.next();
                    String contact = (String) contactEntry.getValue();
                    Log.d("SendSMSService", "contact.get(" + i + "):" + contact);
                    if (!contact.equals("null") && contact.length() == 10) {
                        contactList[i] = contact;
                        Log.d("SendSMSService", "contactList[" + i + "]:" + contactList[i]);

                    }
                    i++;

                }
            }
        }catch(NullPointerException e){
            e.printStackTrace();
            Log.d("SendSMSServcie", "HashMap not initialised exception" );

        }


        contactsFetched=true; //sets contacts fetched to true to indicate that init has been called
    }
    public void initiateMessage(){
        initContacts(); //added code to initialise contacts

        updateLocation();

        Log.d("SOS SMS","Location is"+location);
        //call setContactList
        if(contactList==null){
            //removed dummy contact
            Toast.makeText(getApplicationContext(),"No SOS Contacts were found",Toast.LENGTH_SHORT);
        }
        else{


            for(int i=0;i<contactList.length;i++){
                if(contactList[i]==null){ //checking if array was initialised but no contacts were iniitialised as all contacts from firebase were null
                    continue;
                }
                if(location==null)
                    sendMessage(contactList[i],"Location unavailable");
                else
                    sendMessage(contactList[i],location);

            }
        }
        String toastmsg;

        if(alert==1)
        {
            safe=0; //reset the safe variable
            toastmsg="send alert sos message";
        }
        else if(safe==1)
        {
            alert=0; //reset the alert variable
            toastmsg="send safe sos message";
        }
        else
        {   emergency=1;
            safe=0; //emergency =1 . reset the safe variable
            toastmsg="send emergency sos message";
        }
        Toast.makeText(getApplicationContext(), toastmsg, Toast.LENGTH_LONG).show();
        this.stopSelf();//FINISH the service
    }

    /**Prepares the text message depending on the intent extras**/
    public String initSMSText(String location){
        String messageToSend=SOS_MESSAGE;
        //modified message depending on the extra received from calling intent
    Log.d("SMS Service","checking Testmode:"+checkTestMode());
        if(!navigation.test) { //to be replaced with checkTestMode() when it is functional
            if (alert == 1) {
                messageToSend += "I'm feeling UNSAFE. ";
            } else if (safe == 1) {
                messageToSend += "Just wanted to let you know that I'm SAFE. ";
            } else {
                messageToSend += "PLEASE HELP ME. ";

            }

            messageToSend += "This is my approximate location ";

            if (location != null && location != "Location unavailable")
                messageToSend += "\nhttps://www.google.com/maps/place/";
            messageToSend += location;
        }
        else{
            initSenderName();
            messageToSend= getSenderName()+ "uses Trata app for their safety. You're "+getSenderName()+"'s SOS contact."+
                                            " This is a TEST message."+getSenderName()+" may contact you in emergency.";
        }
        return messageToSend;

    }
    public void sendMessage(String number,String location){

        String messageToSend= initSMSText(location); //refactored the code into initSMSText()

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        Log.d("SendSMSServcie", "message to be sent is: "+messageToSend );

        ArrayList<String> parts = SmsManager.getDefault().divideMessage(messageToSend);

        ArrayList<PendingIntent> sendList = new ArrayList<>();
        sendList.add(sentPI);

        ArrayList<PendingIntent> deliverList = new ArrayList<>();
        deliverList.add(deliveredPI);


        try {
            SmsManager.getDefault().sendMultipartTextMessage(number, null, parts, sendList, deliverList);

        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
            Log.d("SOS SMS", "sendMessage() exception");
            Toast.makeText(getApplicationContext(),"Invalid Mobile No or SOS contacts not initiliased",Toast.LENGTH_SHORT);
        }
        Log.d("SOS SMS", "sendMessage() end");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        alert = intent.getIntExtra("alert", 0);
        safe = intent.getIntExtra("safe", 0);
        emergency = intent.getIntExtra("emergency", 0);

        Log.d("SOS SMS", "alert is " + alert);
        Log.d("SOS SMS", "safe is " + safe);
        Log.d("SOS SMS", "emergency is " + emergency);

        initiateMessage();


        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sentReceiver);
        unregisterReceiver(deliveryReceiver);

    }
}
