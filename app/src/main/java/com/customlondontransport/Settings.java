package com.customlondontransport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;


public class Settings extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String KEY_PREF_SYNC_CONN = "pref_widget_refresh_frequency";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        System.out.println("In preference change block1");
        if (key.equals(KEY_PREF_SYNC_CONN)) {
            System.out.println("In preference change block2");
            new AppWidgetAlarm(getApplicationContext()).updateIntervalAndStartAlarm();
        }
    }




}