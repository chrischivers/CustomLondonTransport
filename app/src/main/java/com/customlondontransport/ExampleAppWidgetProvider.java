package com.customlondontransport;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class ExampleAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetID = appWidgetIds[i];

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tfl.gov.uk"));
            PendingIntent pending = PendingIntent.getActivity(context,0,intent,0);

            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.main_widget);
            views.setOnClickPendingIntent(R.id.widgetButton1, pending);

            appWidgetManager.updateAppWidget(appWidgetID,views);
        }

    }

}
