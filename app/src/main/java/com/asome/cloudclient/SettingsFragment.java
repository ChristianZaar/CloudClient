package com.asome.cloudclient;
import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Iterator;
import java.util.List;
import static android.content.Context.ACTIVITY_SERVICE;

public class SettingsFragment extends Fragment {

    public static final String TAG ="SettingsFragment";
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private Switch mNotifyAlarms;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        mNotifyAlarms = v.findViewById(R.id.notifyAlarmsSwitch);

        SharedPreferences prefs = MyApplication.getAppContext().getSharedPreferences(
                MyApplication.TAG, Context.MODE_PRIVATE);
        mNotifyAlarms.setChecked(prefs.getBoolean(MyApplication.UpdateService,false));

        setListeners();

        return v;
    }

    public void setListeners(){

        mNotifyAlarms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences prefs = MyApplication.getAppContext().getSharedPreferences(
                        MyApplication.TAG, Context.MODE_PRIVATE);
                if(b){
                    if (!checkPermissions()) {
                        requestPermissions();
                        mNotifyAlarms.setChecked(false);
                    }
                    else {
                        prefs.edit().putBoolean(MyApplication.UpdateService, true).apply();
                        if (!isServiceRunning(UploadService.class.getName())) {
                            Intent i = new Intent(MyApplication.getAppContext(), UploadService.class);
                            MyApplication.getAppContext().startService(i);
                            makeToast(TAG + "Started upload service");
                        }
                    }
                }
                else{
                    prefs.edit().putBoolean(MyApplication.UpdateService, false).apply();
                    if(isServiceRunning(UploadService.class.getName())){
                        Intent stopIntent = new Intent(MyApplication.getAppContext(), UploadService.class);
                        stopIntent.setAction(MyApplication.CloseUpdateService);
                        MyApplication.getAppContext().startService(stopIntent);
                        makeToast(TAG + "Stopped upload service");
                    }
                }
            }
        });
    }

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    getActivity().findViewById(R.id.drawer_layout),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void makeToast(String msg){
        Activity a = getActivity();
        if(a != null )
            Toast.makeText(a, msg, Toast.LENGTH_LONG).show();
    }
    private boolean isServiceRunning(String serviceName){
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) MyApplication.getAppContext().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();

        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = i
                    .next();
            Log.d(TAG, "Running " + runningServiceInfo.service.getClassName());
            String test = runningServiceInfo.service.getClassName();
            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                serviceRunning = true;

                if(runningServiceInfo.foreground)
                {
                    //service run in foreground
                }
            }
        }
        return serviceRunning;
    }
}
