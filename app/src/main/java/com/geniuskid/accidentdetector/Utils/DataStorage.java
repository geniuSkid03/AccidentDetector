package com.geniuskid.accidentdetector.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class DataStorage {
    private final String NAME = "tracking_app_data";
    private SharedPreferences sharedPreferences;


    public DataStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void saveInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public void saveBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean isDataAvailable(String key) {
        return sharedPreferences.contains(key);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void removeAllData() {
        sharedPreferences.edit().clear().apply();
    }

    public void removeData(String key) {
        if (isDataAvailable(key)) {
            sharedPreferences.edit().remove(key).apply();
        }
    }

}
