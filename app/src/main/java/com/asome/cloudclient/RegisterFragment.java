package com.asome.cloudclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class RegisterFragment extends Fragment {
    private EditText mUserNameEditText;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mRegisterBtn;
    private Toolbar mToolbar;
    public static final String TAG ="RegisterFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, parent, false);
        mUserNameEditText = (EditText)v.findViewById(R.id.regUserNameEditText);
        mFirstNameEditText = (EditText)v.findViewById(R.id.regFirstNameEditText);
        mLastNameEditText = (EditText)v.findViewById(R.id.regLastNameEditText);
        mEmailEditText = (EditText)v.findViewById(R.id.regEmailEditText);
        mPasswordEditText = (EditText)v.findViewById(R.id.regPasswordEditText);
        mConfirmPasswordEditText = (EditText)v.findViewById(R.id.regConfirmPasswordEditText);
        mRegisterBtn = (Button)v.findViewById(R.id.registerBtn);
        mToolbar = v.findViewById(R.id.registerToolbar);
        mToolbar.inflateMenu(R.menu.toolbar_menu_login);

        setListeners();


        return v;
    }

    private void setListeners(){
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRegistration()){
                    makeToast("Todo Register action");
                }
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
    private void makeToast(String msg){
        Toast.makeText(getContext() , msg, Toast.LENGTH_LONG).show();
    }

    private boolean checkRegistration() {
        String errorMsg = "";

        EditText[] args = new EditText[]{
                mUserNameEditText,
                mFirstNameEditText,
                mLastNameEditText,
                mEmailEditText,
                mPasswordEditText,
                mConfirmPasswordEditText
        };

        for (EditText eT: args){
            if(eT.getText().toString().trim().isEmpty()) {
                errorMsg = "No field can be empty";
            }
        }

        if( !mPasswordEditText.getText().toString().equals(mConfirmPasswordEditText.getText().toString()))
            errorMsg += "\nPasswords do not match";
        if(errorMsg.isEmpty())
            return true;
        Toast.makeText(getContext(),errorMsg,Toast.LENGTH_LONG).show();
        return false;
    }

}

