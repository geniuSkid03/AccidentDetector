package com.geniuskid.accidentdetector.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.geniuskid.accidentdetector.R;
import com.geniuskid.accidentdetector.Utils.DataStorage;
import com.geniuskid.accidentdetector.Utils.Keys;

public class SplashActivity extends SuperCompatActivity {

    private DataStorage dataStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        dataStorage = new DataStorage(this);
    }

    private void runSplash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAndOpen();
            }
        }, 4000);
    }

    private void checkAndOpen() {
        if(dataStorage.getBoolean(Keys.IS_LOGGED_IN)) {
            goTo(this, MainActivity.class, true);
        } else {
            goTo(this, LoginActivity.class, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        runSplash();
    }
}
