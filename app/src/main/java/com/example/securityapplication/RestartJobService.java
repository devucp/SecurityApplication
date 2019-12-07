package com.example.securityapplication;

import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.app.job.JobService;

public class RestartJobService extends JobService{
        private static String TAG= JobService.class.getSimpleName();
        private static RestartServiceBroadcastReceiver restartSensorServiceReceiver;
        private static JobService instance;
        private static JobParameters jobParameters;

        @Override
        public boolean onStartJob(JobParameters jobParameters) {
            Log.d(TAG,"inside onStartJob");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this,SosPlayer.class));
            } else {
                startService(new Intent(this,SosPlayer.class));
            }
            instance= this;
            RestartJobService.jobParameters= jobParameters;

            return false;
        }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
