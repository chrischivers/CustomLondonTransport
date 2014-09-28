package com.londontransportquerybuilder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class AppWidgetAlarm
{
    private static int ALARM_ID = 0;
    private static int INTERVAL_MILLIS = -1;

    private Context mContext;


    public AppWidgetAlarm(Context context)
    {
        mContext = context;
    }

    public void updateIntervalAndStartAlarm() {
        stopAlarm();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int updateInterval = Integer.parseInt(prefs.getString("pref_widget_refresh_frequency","-1"));
        System.out.println("Update interval set as " + updateInterval);
        if (updateInterval != -1) {
            INTERVAL_MILLIS = updateInterval * 1000 * 60;
            startAlarm();
        }
    }


    public void startAlarm()
    {
        if (INTERVAL_MILLIS != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, INTERVAL_MILLIS);

            Intent alarmIntent = new Intent(AppWidgetProvider.ACTION_AUTO_UPDATE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            // RTC does not wake the device up
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), INTERVAL_MILLIS, pendingIntent);
        }
    }


    public void stopAlarm()
    {
        Intent alarmIntent = new Intent(AppWidgetProvider.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}