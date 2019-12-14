package com.example.securityapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.location.Location;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class GetGPSCoordinates extends Service {
    private LocationListener listener;
    private LocationManager locationManager;
    private static String lastKnownLocation=null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static String getLastKnownLocation(){
        return lastKnownLocation;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(),"Oncreate",Toast.LENGTH_SHORT);
        Log.d("GPSService","Oncreate");
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i = new Intent("location_update");
                GetGPSCoordinates.lastKnownLocation=ddToDms(location.getLatitude(),location.getLongitude()) ;
                i.putExtra("coordinates", location.getLatitude()+","+location.getLongitude() );
                Log.d("GPSService","coordinates"+location.getLatitude()+","+location.getLongitude() );
                Toast.makeText(getApplicationContext(),"coordinates"+location.getLatitude()+","+location.getLongitude() ,Toast.LENGTH_SHORT);
                sendBroadcast(i);
            }


            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,listener);

    }
    /*
    public int[] degreeToDMS(double coordinate){
        coordinate=Math.abs(coordinate);
        int c_degrees= (int)coordinate ;

        double minutes_seconds= coordinate-c_degrees;
        double minutes= (minutes_seconds*60);
        int c_minutes= (int)minutes;

        double seconds= (minutes%1)*60;
        int c_seconds=(int)seconds;

    }*/
    public String ddToDms(double ilat,double ilng) {

        double lat = ilat;
        double lng = ilng;
        String latResult, lngResult;
        String DMS_coordinates; //degree-minute-second converted decimal
        // Make sure that you are working with numbers.
        // This is important in case you are working with values

        // Check the correspondence of the coordinates for latitude: North or South.

        String Strlat= Location.convert(ilat,Location.FORMAT_SECONDS);
        String[] split_Strlat=Strlat.split(":");
        latResult=split_Strlat[0]+"°"+split_Strlat[1]+"\'"+split_Strlat[2]+"\'\'";
        latResult += (lat >= 0)? "N" : "S";

        // Check the correspondence of the coordinates for longitude: East or West.
        String Strlon= Location.convert(ilng,Location.FORMAT_SECONDS);
        String[] split_Strlon=Strlon.split(":");
        lngResult=split_Strlon[0]+"°"+split_Strlon[1]+"\'"+split_Strlon[2]+"\'\'";
        lngResult += (lng >= 0)? "E" : "W";

        DMS_coordinates=latResult+"+"+lngResult;
        Log.d("GPSService/Conversion","INPUT:"+ilat+","+ilng+" result:"+DMS_coordinates);

        return DMS_coordinates;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"GPS service destroyed",Toast.LENGTH_SHORT);
        Log.d("GPSService","OnDestroy");
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}
