package com.example.android.pikachu;

import android.content.Context;
import android.content.SharedPreferences;

public class SkipPreferences {
    private static final String LAUNCH = "LaunchSkip";
    private static final String PREFERENCE = "SkipPref";
    private int MODE = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;
    private Context context;

    public SkipPreferences(Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(PREFERENCE, MODE);
        spEditor = sharedPreferences.edit();
    }

    public void setLaunch(int value) {
        spEditor.putInt(LAUNCH, value);
        spEditor.apply();
    }

    public int SkipLaunch() {
        return sharedPreferences.getInt(LAUNCH, 0);
    }

}
