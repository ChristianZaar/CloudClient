package com.asome.cloudclient;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UploadService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String TAG = "UploadService";
    private Handler handler = new Handler();
    private SharedPreferences mPrefs;
    /**
     * Location
     */
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    private Location mLocation;
    private final long UPDATE_INTERVAL = 10000;
    private final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }
//    private Runnable bgUpdate = new Runnable() {
//        @Override
//        public void run() {
//            handler.postDelayed(bgUpdate, 10000);
//        }
//    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent.getAction() != null && intent.getAction().equals(MyApplication.CloseUpdateService)){
            stopForeground(true);
            removeLocationUpdates();
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
            createLocationRequest();

            HandlerThread handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            mServiceHandler = new Handler(handlerThread.getLooper());

            configureLocationService();
            requestLocationUpdates();

//            handler.post(bgUpdate);
        }
        return START_STICKY;
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void configureLocationService(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
                Log.d(TAG ,  getLocationText(mLocation));
                Toast.makeText(MyApplication.getAppContext(),getLocationText(mLocation),Toast.LENGTH_LONG).show();
            }
        };
    }

    private void onNewLocation(Location location) {
        Log.d(TAG, "New location: " + location);

        mLocation = location;
    }

    private void requestLocationUpdates() {
        Log.d(TAG, "Requesting location updates");
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    public void removeLocationUpdates() {
        Log.d(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            stopSelf();
        } catch (SecurityException e) {
            Log.e(TAG, "Lost location permission. " + e);
        }
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
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

    private String getLocationText(Location location) {
        return location == null ?
                "Location is null" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }





}