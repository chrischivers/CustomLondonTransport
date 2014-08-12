package com.customlondontransport;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.utils.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExampleAppWidgetProvider extends AppWidgetProvider {

    public static String ACTION = "ActionName";

    private List<ResultRowItem> resultRows = new ArrayList<ResultRowItem>();
    public static List<UserRouteItem> userRouteValues;
    private GPSTracker gps;
    private Location currentLocation;


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

        ComponentName watchWidget;
        RemoteViews rv;

        restoreListFromPrefs(context);

        resultRows = new APIInterface().runQueryAndSort(userRouteValues, currentLocation);

        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetID = appWidgetIds[i];

            Intent intent = new Intent(context, getClass());
            intent.setAction(ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            rv = new RemoteViews(context.getPackageName(), R.layout.main_widget);
            watchWidget = new ComponentName(context, ExampleAppWidgetProvider.class);

            rv.setOnClickPendingIntent(R.id.widgetQueryLinearLayout, pendingIntent);

            rv = updateWidgetQuery(context, rv);

            appWidgetManager.updateAppWidget(appWidgetIds, rv);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
        if (intent.getAction().equals(ACTION)) {
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

            watchWidget = new ComponentName(context, ExampleAppWidgetProvider.class);
            rv= new RemoteViews(context.getPackageName(), R.layout.main_widget);

            resultRows = new APIInterface().runQueryAndSort(userRouteValues, currentLocation);
            rv = updateWidgetQuery(context, rv);

            appWidgetManager.updateAppWidget(watchWidget, rv);



        }
    }

    private RemoteViews updateWidgetQuery(Context context, RemoteViews rv) {
        rv.removeAllViews(R.id.widgetQueryLinearLayout);

        for (ResultRowItem result : resultRows) {
            RemoteViews queryRowRemoteView = new RemoteViews(context.getPackageName(), R.layout.query_view_row_widget);
            queryRowRemoteView.setTextViewText(R.id.transportModeQueryResult, result.getTransportMode());
            queryRowRemoteView.setTextViewText(R.id.routeLineQueryResult, result.getRouteLine());
            queryRowRemoteView.setTextViewText(R.id.startingStopQueryResult, result.getStopStationName());
            queryRowRemoteView.setTextViewText(R.id.directionQueryResult, result.getDestination());
            queryRowRemoteView.setTextViewText(R.id.timeQueryResult, result.getTimeUntilArrivalFormattedString());

            rv.addView(R.id.widgetQueryLinearLayout, queryRowRemoteView);
        }
        return rv;
    }

    public void restoreListFromPrefs(Context context) {

        userRouteValues = new ArrayList<UserRouteItem>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            userRouteValues = (ArrayList<UserRouteItem>) ObjectSerializer.deserialize(prefs.getString("User_Route_Values", ObjectSerializer.serialize(new ArrayList<UserRouteItem>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
