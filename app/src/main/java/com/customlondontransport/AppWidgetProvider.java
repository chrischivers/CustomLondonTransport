package com.customlondontransport;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.utils.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.utils.ObjectSerializer.deserialize;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

    public static String REFRESH = "Refresh";
    public static int refreshInterval = -1;

    private List<ResultRowItem> resultRows = new ArrayList<ResultRowItem>();
    public static List<UserItem> userValues;
    private GPSTracker gps;
    private Location currentLocation;
    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";

    @Override
    public void onEnabled(Context context)
    {
        // start alarm
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.updateIntervalAndStartAlarm();
    }

    @Override
    public void onDisabled(Context context)
    {
        // TODO: alarm should be stopped only if all widgets has been disabled

        // stop alarm
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.stopAlarm();
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        gps = new GPSTracker(context);

        if(gps.canGetLocation()){
            currentLocation = new Location("");
            currentLocation.setLatitude(gps.getLatitude());
            currentLocation.setLongitude(gps.getLongitude());
        } else {
            currentLocation = null;
            gps.showSettingsAlert();
        }

        RemoteViews rv;

        restoreListFromPrefs(context);

        resultRows = new APIInterface().runQueryAndSort(userValues, currentLocation);

        for (int appWidgetID : appWidgetIds) {
            Intent intent = new Intent(context, getClass());
            intent.setAction(REFRESH);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            rv = new RemoteViews(context.getPackageName(), R.layout.main_widget);

            rv.setOnClickPendingIntent(R.id.widgetQueryRelativeLayout, pendingIntent);

            rv = updateWidgetQuery(context, rv);

            // Sets Settings button
            Intent settingsIntent = new Intent(context, UserListView.class);
            settingsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);  // Identifies the particular widget...
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Make the pending intent unique...
            settingsIntent.setData(Uri.parse(settingsIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendIntent = PendingIntent.getActivity(context, 0, settingsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            rv.setOnClickPendingIntent(R.id.settingsImageButton, pendIntent);

            appWidgetManager.updateAppWidget(appWidgetID,rv);
        }


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().equals(ACTION_AUTO_UPDATE) || intent.getAction().equals(REFRESH)) {
            System.out.println("In widget update block");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            gps = new GPSTracker(context);

            if(gps.canGetLocation()){
                currentLocation = new Location("");
                currentLocation.setLatitude(gps.getLatitude());
                currentLocation.setLongitude(gps.getLongitude());
            } else {
                currentLocation = null;
                gps.showSettingsAlert();
            }

            ComponentName watchWidget;
            RemoteViews rv;

            restoreListFromPrefs(context);

            watchWidget = new ComponentName(context, AppWidgetProvider.class);
            rv= new RemoteViews(context.getPackageName(), R.layout.main_widget);

            resultRows = new APIInterface().runQueryAndSort(userValues, currentLocation);
            rv = updateWidgetQuery(context, rv);

            appWidgetManager.updateAppWidget(watchWidget, rv);
        }
    }

    private RemoteViews updateWidgetQuery(Context context, RemoteViews rv) {
        rv.removeAllViews(R.id.widgetQueryLinearLayout);
        int numberViewsAdded = 0;

        for (ResultRowItem result : resultRows) {
            RemoteViews queryRowRemoteView = new RemoteViews(context.getPackageName(), R.layout.query_view_row_widget);

            String imageName = result.getRouteLine().getID().toLowerCase() + "_line_icon";

            if (result.getTransportMode().equals("Bus")) {
                queryRowRemoteView.setImageViewResource(R.id.transportModeImageQueryResult, R.drawable.bus_icon);
                queryRowRemoteView.setTextViewText(R.id.routeLineQueryResult, result.getRouteLine().getID());

            } else if (result.getTransportMode().equals("Tube")) {
                queryRowRemoteView.setImageViewResource(R.id.transportModeImageQueryResult, context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));
                queryRowRemoteView.setTextViewText(R.id.routeLineQueryResult, result.getRouteLine().getAbrvName());
            }

            queryRowRemoteView.setTextViewText(R.id.startingStopQueryResult, result.getStopStationName());
            queryRowRemoteView.setTextViewText(R.id.directionQueryResult, result.getDestination());
            queryRowRemoteView.setTextViewText(R.id.timeQueryResult, result.getTimeUntilArrivalFormattedString());

            rv.addView(R.id.widgetQueryLinearLayout, queryRowRemoteView);
            numberViewsAdded++;
        }

        if (numberViewsAdded == 0) {
            RemoteViews tv = new RemoteViews(context.getPackageName(), R.layout.query_view_row_widget);
            tv.setTextViewText(R.id.routeLineQueryResult, "None");
            rv.addView(R.id.widgetQueryLinearLayout, tv);

        }
        return rv;
    }
    @SuppressWarnings("unchecked")
    public void restoreListFromPrefs(Context context) {

        userValues = new ArrayList<UserItem>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            userValues = (ArrayList<UserItem>) deserialize(prefs.getString("User_Route_Values", ObjectSerializer.serialize(new ArrayList<UserItem>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
