package com.example.securityapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

public class SosPlayer extends Service {
    private MediaSessionCompat mediaSession;
    private int soskeyscount;
    private  boolean sosplay;
    @Override
    public void onCreate() {
        super.onCreate();

        //initialising count
        resetCount();
        //initialising sosplaying variable
        sosplay=false;

        mediaSession = new MediaSessionCompat(this, "SosPlayer");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0) //you simulate a player which plays something.
                .build());

        //this will only work on Lollipop and up, see https://code.google.com/p/android/issues/detail?id=224134
        VolumeProviderCompat myVolumeProvider =
                new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, /*max volume*/100, /*initial volume level*/50) {
                    @Override
                    public void onAdjustVolume(int direction) {
                /*
                -1 -- volume down
                1 -- volume up
                0 -- volume button released
                 */
                    Log.v("Player","Direction is:"+direction);
                    updateCount(direction);
                    }
                };

        mediaSession.setPlaybackToRemote(myVolumeProvider);
        mediaSession.setActive(true);
    }
    public void resetCount(){
        soskeyscount=0;
    }
    public void updateCount(int direction){

        if (direction!=0){
            if(soskeyscount%2==0 && direction<0){
                soskeyscount++;
            }
            else if(soskeyscount%2!=0 && direction>0){
                soskeyscount++;
            }
            else{
                resetCount();
            }

            Log.d("New soskeycount","Count"+soskeyscount+" Direction"+direction);
        }

        Log.d("Sos service","Passing on 0 direction");
    }

    public boolean checkPlaying(){
        return sosplay;
    }

    public void startPlaying(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.release();
    }
}
