package com.asome.cloudclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootBroadCastReceiver  extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "BootBroadcastReceiver";

    /**
     * Start bg service
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent serviceIntent = new Intent(context, UploadService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "Trying to start service");
                context.startForegroundService(serviceIntent);
            }
        }
    }
}
