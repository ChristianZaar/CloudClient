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
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.asome.cloudclient.rest.Coordinate;
import com.asome.cloudclient.rest.RestAPIInteractor;
import com.asome.cloudclient.rest.UnsafeHttpClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static Queue<Coordinate> queueCoord = new ConcurrentLinkedQueue<Coordinate>();
    public static final String BASE_URL = "https://webclient20200527235035.azurewebsites.net/";//"http://localhost:57896/";
    private static final String TAG = "UploadService";
    private Handler handler = new Handler();
    private SharedPreferences mPrefs;
    private String userId;
    /**
     * Location
     */
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    private Location mLocation;
    private final long UPDATE_INTERVAL = 20000;
    private final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    private Runnable bgUpdate = new Runnable() {
        private RestAPIInteractor mRestApiInteractor;

        @Override
        public void run() {
            ArrayList<Coordinate> coordList = new ArrayList<Coordinate>();
            Coordinate c;

            do{
                c = queueCoord.poll();

                if (c != null)
                    coordList.add(c);
            }while (c != null);

            if (coordList.size() > 0){
                sendToCloud(coordList);
            }

            handler.postDelayed(bgUpdate, 60000);
        }

        private void sendToCloud(ArrayList<Coordinate> list){
            OkHttpClient okHttpClient = UnsafeHttpClient.getUnsafeOkHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mRestApiInteractor = retrofit.create(RestAPIInteractor.class);


            Call<ResponseBody> call = mRestApiInteractor.coordinates(list);
            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Toast.makeText(MyApplication.getAppContext(),"Sucess send to cloud code: " + response.raw().code(),Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //Maybe add unsent queue
                    Toast.makeText(MyApplication.getAppContext(),"Fail send to cloud" + t.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        Log.d(TAG, "onStartCommand");//refs.edit().putBoolean(MyApplication.UpdateService,
        mPrefs = MyApplication.getAppContext().getSharedPreferences(
                MyApplication.TAG, Context.MODE_PRIVATE);
        boolean run = mPrefs.getBoolean(MyApplication.UpdateService,false);

        if (account != null && intent.getAction() != null && intent.getAction().equals(MyApplication.CloseUpdateService) || !run){
            stopForeground(true);
            removeLocationUpdates();
        }
        else{

            userId = account.getId();
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

            handler.post(bgUpdate);
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
        Log.d(TAG, "New location: " + location.getLatitude());
        Double t = location.getLatitude();

        Coordinate c  = new Coordinate();
        c.setLatitude(Double.toString(location.getLatitude()));
        c.setLongitude(Double.toString(location.getLongitude()));
        c.setTimestamp(LocalDateTime.now().toString());
        c.setUserId(userId);
        queueCoord.add(c);

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
            Log.e(TAG, "Lost location permission! " + e);
        }
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacks(bgUpdate);
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