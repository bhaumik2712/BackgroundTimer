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

import java.util.concurrent.TimeUnit;

public class Timer_Service extends Service {


    private static final String CHANNEL_MIN = "channel_min";
    private static final String CHANNEL_LOW = "channel_low";

    private NotificationManager mgr;

    String strDate;
    private NotificationCompat.Builder builder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initChannels();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Notification notification = buildForegroundNotification(CHANNEL_LOW);
        startForeground(1337, notification);
        CountDownTimer start = new CountDownTimer((1000 * 60) * 5, 1000) {

            public void onTick(long millisUntilFinished) {
                strDate = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60 + ":" + TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                builder.setContentText("Time Left : " + strDate);

                mgr.notify(1337, builder.build());
            }

            public void onFinish() {
                stopMeDown(getApplicationContext());
            }
        };

        start.start();
        return (super.onStartCommand(intent, flags, startId));
    }

    public static void stopMeDown(Context ctxt) {
        Intent i = new Intent(ctxt, Timer_Service.class);
        ctxt.stopService(i);
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
        builder = new NotificationCompat.Builder(this, channel);

        builder.setOngoing(true)
                .setContentTitle("You are on the break")
                .setSmallIcon(android.R.drawable.stat_sys_download);

        return builder.build();
    }

}