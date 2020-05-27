package com.asome.cloudclient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application {
    private static Context context;
    public final static String TAG = "com.asome.cloudclient";
    public final static String NightModeTag = "com.asome.cloudclient.nightmode";
    public final static String CloseUpdateService = "com.asome.cloudclient.stoprunning";
    public final static String UpdateService = "com.smartest.smarthouseapp.notifyAlarms";

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

}
