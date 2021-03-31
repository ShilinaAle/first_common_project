package com.shilina.project_x;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class BroadcastService extends Service {

    private static PhoneHandler phoneCall;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LOOK HERE: BroadcastService", "PhoneHandler started background running");

        phoneCall = new PhoneHandler();
        IntentFilter phoneCall_filter = new IntentFilter("android.intent.action.PHONE_STATE");
        if (!PhoneHandler.isRegistered) {
            registerReceiver(phoneCall, phoneCall_filter);
            Log.i("LOOK HERE: BroadcastService", "Registering PhoneHandler");
            PhoneHandler.isRegistered = true;
        } else {
            Log.i("LOOK HERE: BroadcastService", "PhoneHandler is registered");
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("LOOK HERE: BroadcastService", "Created");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        startForeground(1001, notification);

        //Toast.makeText(this, "Напоминание добавлено в календарь", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("LOOK HERE: BroadcastService", "Destroyed");
        unregisterReceiver(phoneCall);
        phoneCall = null;
        PhoneHandler.isRegistered = false;
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel (NotificationManager notificationManager) {
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    public BroadcastService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}