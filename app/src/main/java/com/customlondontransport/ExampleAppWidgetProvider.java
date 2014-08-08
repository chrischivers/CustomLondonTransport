package com.customlondontransport;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ExampleAppWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetID = appWidgetIds[i];

           // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tfl.gov.uk"));
           // PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.main_widget);
            // views.setOnClickPendingIntent(R.id.widgetButton1, pending);

            RemoteViews queryRowRemoteView = new RemoteViews(context.getPackageName(), R.layout.query_view_row_widget);
            queryRowRemoteView.setTextViewText(R.id.transportModeQueryResult, "Text 1");
            queryRowRemoteView.setTextViewText(R.id.routeLineQueryResult, "Text 2");
            queryRowRemoteView.setTextViewText(R.id.startingStopQueryResult, "Text 3");
            queryRowRemoteView.setTextViewText(R.id.directionQueryResult, "Text 4");
            queryRowRemoteView.setTextViewText(R.id.timeQueryResult, "Text 5");


            rv.addView(R.id.widgetQueryLinearLayout, queryRowRemoteView);

            appWidgetManager.updateAppWidget(appWidgetID, rv);

        }

    }
}
