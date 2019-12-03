package com.example.securityapplication;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

public class SosPlayer extends Service {
    private MediaSessionCompat mediaSession;
    private int soskeyscount;
    private  boolean sosplay;
    private int prev_direction;
    private boolean timerStarted;
    @Override
    public void onCreate() {
        super.onCreate();

        //initialising count
        resetCount();
        //initialising sosplaying variable
        sosplay=false;
        prev_direction=0;

        timerStarted=false;

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
    public void startTimer(){
        timerStarted=true;
        Log.d("SOS Timer","Timer started");
        new CountDownTimer(4*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d("SOS Timer" ,"Time remaining:" +millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.d("SOS Timer" ,"Timeoout reached" );
                resetCount();
                timerStarted=false;
                Toast.makeText(getApplicationContext(), "SOS timer reset", Toast.LENGTH_LONG).show();

            }
        }.start();
    }

    public void resetCount(){
        soskeyscount=0;
    }

    public void updateCount(int direction){
        if(soskeyscount==5){
            Intent svc=new Intent(this, BackgroundSosPlayerService.class);
            startService(svc);
            sosplay=true;
        }
        if(soskeyscount==0 && prev_direction==0){
            prev_direction=direction;
            return;
        }
        if (direction!=0){
            //start timer on first key press
            if(soskeyscount==0 && !timerStarted){
                startTimer();

            }

            if(compareDirection(prev_direction,direction)){
                soskeyscount++;
            }

            else{
                resetCount();
            }
            prev_direction=direction;
            Log.d("New soskeycount","Count"+soskeyscount+" Direction"+direction+" Prev direction"+prev_direction);
        }

        Log.d("Sos service","Passing on 0 direction");
    }
    public boolean compareDirection(int p_direction, int n_direction){
        int d_p_direction= (p_direction>0)?1:-1;
        int d_n_direction= (n_direction>0)?1:-1;

        return d_p_direction!=d_n_direction;
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
