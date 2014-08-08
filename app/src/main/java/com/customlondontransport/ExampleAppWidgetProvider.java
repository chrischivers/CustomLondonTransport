package com.customlondontransport;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

public class ExampleAppWidgetProvider extends AppWidgetProvider {

    public static String ACTION = "ActionName";

    private List<ResultRowItem> resultRows = new ArrayList<ResultRowItem>();


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        System.out.println("Here 2");

        ComponentName watchWidget;
        RemoteViews rv;

        resultRows = new APIInterface().runQueryAndSort();

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
            ComponentName watchWidget;
            RemoteViews rv;

            watchWidget = new ComponentName(context, ExampleAppWidgetProvider.class);
            rv= new RemoteViews(context.getPackageName(), R.layout.main_widget);

            resultRows = new APIInterface().runQueryAndSort();
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

}
