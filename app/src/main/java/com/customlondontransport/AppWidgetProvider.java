package com.customlondontransport;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

import com.utils.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.utils.ObjectSerializer.deserialize;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {



    private List<ResultRowItem> resultRows = new ArrayList<ResultRowItem>();
    public static List<UserItem> userValues;
    private GPSTracker gps;
    RemoteViews rv;
    private Location currentLocation;
    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";
    public static final String REFRESH = "REFRESH";
    public static boolean backgroundColourUpdateRequired = false;
    public static boolean textColourUpdateRequired = false;
    private static int backgroundColourResourceID = R.color.transparent;
    private static int textColourResourceID = R.color.lightgrey;

    @Override
    public void onEnabled(Context context)
    { super.onEnabled(context);
        // start alarm
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.updateIntervalAndStartAlarm();

    }

    @Override
    public void onDisabled(Context context)
    {
        super.onDisabled(context);
        // TODO: alarm should be stopped only if all widgets has been disabled

        // stop alarm
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.stopAlarm();
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        gps = new GPSTracker(context);

        //Set colours according to preferences if they exist
        backgroundColourResourceID = context.getResources().getIdentifier(getWidgetBackgroundColour(context), "color", context.getPackageName());
        textColourResourceID = context.getResources().getIdentifier(getWidgetTextColour(context), "color", context.getPackageName());


        if(gps.canGetLocation()){
            currentLocation = new Location("");
            currentLocation.setLatitude(gps.getLatitude());
            currentLocation.setLongitude(gps.getLongitude());
        } else {
            currentLocation = null;
            gps.showSettingsAlert();
        }

        restoreListFromPrefs(context);

        resultRows = new APIInterface().runQueryAndSort(userValues, currentLocation);

        for (int appWidgetID : appWidgetIds) {
            Intent intent = new Intent(context, getClass());
            intent.setAction(REFRESH);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            rv = new RemoteViews(context.getPackageName(), R.layout.main_widget);

            if (backgroundColourUpdateRequired) {
                rv.setInt(R.id.widget_main_relative_layout, "setBackgroundResource", backgroundColourResourceID); // Sets Background Colour
                backgroundColourUpdateRequired = false;
            }

            rv.setOnClickPendingIntent(R.id.widgetQueryRelativeLayout, pendingIntent);

            rv = updateWidgetQuery(context, rv);

            // Sets Settings button
            Intent settingsIntent = new Intent(context, Settings.class);
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
        rv.setTextViewText(R.id.widgetLastUpdatedText, "Last updated: " + DateFormat.format("dd/MM/yy HH:mm:ss", System.currentTimeMillis()));
        if (textColourUpdateRequired) {
            rv.setTextColor(R.id.widgetRouteHeader, context.getResources().getColor(textColourResourceID));
            rv.setTextColor(R.id.widgetStartingStopHeader, context.getResources().getColor(textColourResourceID));
            rv.setTextColor(R.id.widgetDestinationHeader, context.getResources().getColor(textColourResourceID));
            rv.setTextColor(R.id.widgetTimeHeader, context.getResources().getColor(textColourResourceID));
            rv.setTextColor(R.id.widgetLastUpdatedText, context.getResources().getColor(textColourResourceID));
        }
        int numberViewsAdded = 0;

        for (ResultRowItem result : resultRows) {
            RemoteViews queryRowRemoteView = new RemoteViews(context.getPackageName(), R.layout.widget_query_view_row);


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

            if (textColourUpdateRequired) {
                queryRowRemoteView.setTextColor(R.id.routeLineQueryResult, context.getResources().getColor(textColourResourceID));
                queryRowRemoteView.setTextColor(R.id.startingStopQueryResult, context.getResources().getColor(textColourResourceID));
                queryRowRemoteView.setTextColor(R.id.directionQueryResult, context.getResources().getColor(textColourResourceID));
                queryRowRemoteView.setTextColor(R.id.timeQueryResult, context.getResources().getColor(textColourResourceID));
            }

            rv.addView(R.id.widgetQueryLinearLayout, queryRowRemoteView);
            numberViewsAdded++;
        }

        // If no results, display a message
        if (numberViewsAdded == 0) {
            RemoteViews tv = new RemoteViews(context.getPackageName(), R.layout.widget_query_view_row_message);
            if (!isNetworkAvailable(context)) {
                tv.setTextViewText(R.id.widget_query_result_message, context.getResources().getString(R.string.no_connection));
            } else {
                tv.setTextViewText(R.id.widget_query_result_message, context.getResources().getString(R.string.no_results));
            }
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

    private String getWidgetBackgroundColour(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("pref_widget_background_colour","transparent");
    }
    private String getWidgetTextColour(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("pref_widget_text_colour","lightgrey");
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
