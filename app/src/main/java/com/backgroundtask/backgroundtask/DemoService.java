package com.backgroundtask.backgroundtask;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created on 14/11/2017.
 */

public class DemoService extends Service /*implements LocationHelperCallback */ {

    private static final int REQUEST_CODE_LOCATION_SETTINGS = 11;
    private static final int PERMISSION_CODE_LOCATION = 101;

    private static final String CHANNEL_MIN = "channel_min";
    private static final String CHANNEL_LOW = "channel_low";
    private static final int NOTIFICATION_TIMER_EXPIRED = 111;
    private static NotificationManager mgr;
    private static long timerValue;
    private static Notification build;

//    private static LocationHelper mLocationHelper;


    public static void startMeUp(final Context ctxt) {
        Intent i = new Intent(ctxt, DemoService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ctxt.startForegroundService(i);
        } else {
            ctxt.startService(i);
        }

    }

    public static void stopMeDown(Context ctxt) {
        Intent i = new Intent(ctxt, DemoService.class);
        ctxt.stopService(i);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initChannels();
        }

        Log.d("DemoService", "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DemoService", "onStartCommand()");

        startForeground(1337, buildForegroundNotification(CHANNEL_LOW));
        new CountDownTimer(1000 * 60, 1000) {

            public void onTick(long millisUntilFinished) {
                timerValue = millisUntilFinished / 1000;
                mgr.notify(NOTIFICATION_TIMER_EXPIRED, build);

            }

            public void onFinish() {
//                DemoService.stopMeDown(ctxt);
            }
        }.start();

        return (super.onStartCommand(intent, flags, startId));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("DemoService", "onBind()");

        throw new IllegalStateException("Exception");
    }

    @Override
    public void onDestroy() {
        Log.d("DemoService", "onDestroy()");

        mgr.cancelAll();
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void initChannels() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_MIN, getString(R.string.channel_min), NotificationManager.IMPORTANCE_MIN);

        mgr.createNotificationChannel(channel);

        channel = new NotificationChannel(CHANNEL_LOW, getString(R.string.channel_low),
                NotificationManager.IMPORTANCE_LOW);
        mgr.createNotificationChannel(channel);
    }

    private Notification buildForegroundNotification(String channel) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this, channel);

        b.setOngoing(true)
                .setContentTitle(getString(R.string.notify_working))
                .setContentText(String.valueOf(timerValue))
                .setSmallIcon(android.R.drawable.stat_sys_download);

        build = b.build();
        mgr.notify(NOTIFICATION_TIMER_EXPIRED, build);

        return build;
    }
}