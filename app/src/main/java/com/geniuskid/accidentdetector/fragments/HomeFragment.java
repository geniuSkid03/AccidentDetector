package com.geniuskid.accidentdetector.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geniuskid.accidentdetector.R;
import com.geniuskid.accidentdetector.activity.interfaces.FragmentRefresher;
import com.geniuskid.accidentdetector.services.GpsTracker;

public class HomeFragment extends SuperFragment {

    private GpsTracker gpsTracker;
    private double lat, lng;

    private static final int FORCE_THRESHOLD = 350;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView(inflater.inflate(R.layout.fragment_home, container, false));
    }

    private View initView(View view) {

        return view;
    }

    public void refreshHome() {

    }


}
