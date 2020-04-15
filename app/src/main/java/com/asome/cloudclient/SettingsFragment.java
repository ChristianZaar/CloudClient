package com.asome.cloudclient;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.Fragment;
import java.util.Iterator;
import java.util.List;
import static android.content.Context.ACTIVITY_SERVICE;

public class SettingsFragment extends Fragment {

    public static final String TAG ="SettingsFragment";
    private String mHousePassword;
    private int mHouseID;
    private int mDeviceID;

    //UI
    private Button mAccessHouseButton;
    private Button mDeleteDeviceButton;
    private EditText mHousePasswordET;
    private EditText mHouseIdET;
    private EditText mDeviceIdET;
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
                    prefs.edit().putBoolean(MyApplication.UpdateService, true).apply();
                    if(!isServiceRunning(UploadService.class.getName())){
                        Intent i = new Intent(MyApplication.getAppContext(), UploadService.class);
                        MyApplication.getAppContext().startService(i);
                        makeToast(TAG + "Started upload service");
                    }
                }
                else{
                    prefs.edit().putBoolean(MyApplication.UpdateService, false).apply();
                    if(isServiceRunning(UploadService.class.getName())){
                        Intent stopIntent = new Intent(MyApplication.getAppContext(), UploadService.class);
                        stopIntent.setAction(MyApplication.UpdateService);
                        MyApplication.getAppContext().startService(stopIntent);
                        makeToast(TAG + "Stopped upload service");
                    }
                }
            }
        });
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
