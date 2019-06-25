package com.geniuskid.accidentdetector.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import com.geniuskid.accidentdetector.R;
import com.geniuskid.accidentdetector.activity.SuperCompatActivity;

public class SensorFragment extends SuperCompatActivity {

    private AppCompatButton applyBtn;
    private TextInputEditText shakeCountEd, shakeDurEd, shakeTimeEd, timeThresholdEd, forceThresholdED;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sensors);

        initView();
    }

    private void initView() {
        shakeCountEd = findViewById(R.id.shake_count);
        shakeDurEd = findViewById(R.id.shake_duration);
        shakeTimeEd = findViewById(R.id.shake_timeout);
        timeThresholdEd = findViewById(R.id.time_thresh);
        forceThresholdED = findViewById(R.id.force_thres);
        applyBtn = findViewById(R.id.apply_btn);

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SensorFragment.this, "Settings applied", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
