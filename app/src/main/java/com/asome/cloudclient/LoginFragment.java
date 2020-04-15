package com.asome.cloudclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LoginFragment extends Fragment {
    private EditText mUserNameEditText;
    private EditText mPassWordEditText;
    private Button mLoginBtn;
    private Button mRegister;
    private Toolbar mToolbar;
    public static final String TAG ="LoginFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        SharedPreferences prefs = MyApplication.getAppContext().getSharedPreferences(
                MyApplication.TAG, Context.MODE_PRIVATE);
        int i = prefs.getInt(MyApplication.NightModeTag, AppCompatDelegate.MODE_NIGHT_NO);
        if (i == AppCompatDelegate.MODE_NIGHT_NO){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, parent, false);
        mUserNameEditText = (EditText) v.findViewById(R.id.loginUserNameEditText);
        mPassWordEditText = (EditText) v.findViewById(R.id.loginPasswordEditText);
        mLoginBtn = (Button) v.findViewById(R.id.loginBtn);
        mRegister = (Button) v.findViewById(R.id.registerBtn);
        mToolbar = v.findViewById(R.id.loginToolbar);
        mToolbar.inflateMenu(R.menu.toolbar_menu_login);
        setListeners();


        return v;
    }

    private void makeToast(String msg){
        Activity a = getActivity();
        if(a != null )
            Toast.makeText(a, msg, Toast.LENGTH_LONG).show();
    }

    private void setListeners(){
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCredentials()){
                    makeToast(TAG + " Todo login action");
                    Intent intent = MenuActivity.newIntent(getActivity());
                    //Clearing stack
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new RegisterFragment();
                f.setEnterTransition(new Slide(Gravity.RIGHT));
                f.setExitTransition(new Slide(Gravity.LEFT));
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(TAG);
                transaction.replace(R.id.fragment_container, f);
                transaction.commit();
            }
        });

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.toggleMode:
                        SharedPreferences prefs = MyApplication.getAppContext().getSharedPreferences(
                                MyApplication.TAG, Context.MODE_PRIVATE);
                        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            prefs.edit().putInt(MyApplication.NightModeTag, AppCompatDelegate.MODE_NIGHT_NO).apply();
                        }else{
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            prefs.edit().putInt(MyApplication.NightModeTag, AppCompatDelegate.MODE_NIGHT_YES).apply();
                        }
                        return true;
                    case R.id.info:
                        Fragment f = new AboutFragment();
                        f.setEnterTransition(new Slide(Gravity.RIGHT));
                        f.setExitTransition(new Slide(Gravity.LEFT));
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(TAG);
                        transaction.replace(R.id.fragment_container, f);
                        transaction.commit();
                }
                return false;
            }
        });
    }



    private boolean checkCredentials(){
        boolean retVal = true;
        String msg = "";

        if (mUserNameEditText.getText().toString().trim().isEmpty()){
            retVal = false;
            msg = "User name cannot be empty.\n";
        }
        if (mPassWordEditText.getText().toString().trim().isEmpty()){
            msg += "Password cannot be empty.";
        }

        if(!retVal)
            Toast.makeText(getContext(),msg + "",Toast.LENGTH_LONG).show();

        return retVal;
    }
}
