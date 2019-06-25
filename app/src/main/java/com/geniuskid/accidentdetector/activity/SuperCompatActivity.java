package com.geniuskid.accidentdetector.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.geniuskid.accidentdetector.Utils.DataStorage;
import com.geniuskid.accidentdetector.Utils.PermissionsHandler;
import com.google.gson.Gson;

public class SuperCompatActivity extends AppCompatActivity {

    protected DataStorage dataStorage;
    protected PermissionsHandler permissionsHandler;
    protected Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataStorage = new DataStorage(this);
        permissionsHandler = new PermissionsHandler(this);
    }

    //for starting activity with single intent values
    protected void goTo(Context from, Class to, boolean close, String key, String data) {
        if (!this.getClass().getSimpleName().equals(to.getClass().getSimpleName())) {
            if (key != null && data != null) {
                startActivity(new Intent(from, to).putExtra(key, data));
            }
            if (close) {
                finish();
            }
        }
    }

    //for starting activity without intent values
    protected void goTo(Context from, Class to, boolean close) {
        if (!this.getClass().getSimpleName().equals(to.getClass().getSimpleName())) {
            startActivity(new Intent(from, to));
            if (close) {
                finish();
            }
        }
    }
}
