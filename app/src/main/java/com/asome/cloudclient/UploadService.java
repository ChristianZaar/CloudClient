package com.asome.cloudclient;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UploadService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String TAG = "UploadService";
    private Handler handler = new Handler();
    private SharedPreferences mPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
    }
    private Runnable bgUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(bgUpdate, 10000);
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction() != null && intent.getAction().equals(MyApplication.CloseUpdateService)){
            stopForeground(true);
            stopSelf();
        }
        else{
            mPrefs = MyApplication.getAppContext().getSharedPreferences(
                    MyApplication.TAG, Context.MODE_PRIVATE);

            createNotificationChannel();
            Intent notificationIntent = new Intent(this, LoginActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Upload service is on")
                    .setSmallIcon(R.drawable.ic_upload_service_on)
                    .build();
            notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;
            startForeground(1, notification);
            handler.post(bgUpdate);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}