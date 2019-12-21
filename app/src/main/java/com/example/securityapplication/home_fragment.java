package com.example.securityapplication;

import android.annotation.SuppressLint;
import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.content.ContextCompat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import static android.content.Intent.getIntent;

public class home_fragment extends Fragment {

    public Button alert;
    public Button emergency;
    public Button informsafety;
    int RC;
    Boolean is_paid = true;
    public static Boolean test = true;
    //NOTE: Button bt has been removed. Now using Button emergency. Event listeners also moved to emergency


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alert = Objects.requireNonNull(getActivity()).findViewById(R.id.alert);
        emergency = getActivity().findViewById(R.id.emergency);
        informsafety = getActivity().findViewById(R.id.inform);


        alert.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                if(!checkSMSPermission()) {

                }
                else
                {
                    Toast.makeText(getContext(),"sms permisssion noy enabled",Toast.LENGTH_LONG);
                }

                Intent mSosPlayerIntent = new Intent(getContext(), SendSMSService.class);
                mSosPlayerIntent.putExtra("alert",1);


                if (!isMyServiceRunning(SendSMSService.class)){

                    Objects.requireNonNull(getContext()).startService(mSosPlayerIntent);




                }
            }
        });

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_paid) {
                    Toast.makeText(getContext(), "You are premier member", Toast.LENGTH_SHORT).show();

                    //Code: TO play siren and send emergency message and alert
                    //emergency.setBackgroundColor(getResources().getColor(R.drawable.buttonshape_emer));
                    Context c2 = getContext();

                    Intent emergencyintent1=new Intent(getContext(), BackgroundSosPlayerService.class);

                    if (c2 != null) {
                        c2.startService(emergencyintent1);
                    }

                    Intent emergencyintent2 = new Intent(getContext(), SendSMSService.class);
                    emergencyintent2.putExtra("emergency",1);
                    assert c2 != null;
                    c2.startService(emergencyintent2);


                } else {
                    //if user using free services only
                    new AlertDialog.Builder(getContext()).setMessage("Upgrade to Premier to Use this function")
                            .setPositiveButton("Purchased", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                        intent.setData(Uri.parse("http://www.w3schools.com"));
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).setNegativeButton("Cancel", null).setCancelable(false).create().show();
                }
            }
        });

        informsafety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context c3 = getContext();
                Intent stopsms = new Intent(getContext(),SendSMSService.class);
                stopsms.putExtra("safe",1);
                if (c3 != null) {
                    c3.startService(stopsms);
                }


                if(isMyServiceRunning(SendSMSService.class))
               {


                   if (c3 != null) {
                       c3.stopService(stopsms);
                   }
               }

               if(isMyServiceRunning(BackgroundSosPlayerService.class))
               {
                   Intent stopemergency = new Intent(getContext(),BackgroundSosPlayerService.class);
                   if (c3 != null) {
                       c3.stopService(stopemergency);
                   }
               }




            }
        });


        if (navigation.test) {

            TextView tv = getActivity().findViewById(R.id.textView3);
            tv.setVisibility(View.VISIBLE);
        } else {
            TextView tv = getActivity().findViewById(R.id.textView3);
            tv.setVisibility(View.INVISIBLE);
        }

    }

    public  boolean checkSMSPermission(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(), "Permission Required for sending SMS in case of SOS", Toast.LENGTH_LONG).show();
            Log.d("MainActivity", "PERMISSION FOR SEND SMS NOT GRANTED, REQUESTING PERMSISSION...");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS}, RC);
        }
        final boolean b = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        return b;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        Context c1 = getContext();
        ActivityManager manager = (ActivityManager) c1.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

}



