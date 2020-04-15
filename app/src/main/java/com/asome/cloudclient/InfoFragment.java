package com.asome.cloudclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InfoFragment extends Fragment {
    public static final String TAG ="InfoFragment";

    private TextView titleTxtViewInfo;
    private TextView txtViewInfo;
    private EditText DevName;


    @Override
    public void onCreate(@Nullable Bundle savedInstantState) {
        super.onCreate(savedInstantState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);





        return v;
    }
}
