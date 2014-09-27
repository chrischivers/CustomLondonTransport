package com.customlondontransport;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class QueryResults extends Activity  {

    private LinearLayout queryResultsLayout;
    private ScrollView queryResultsScrollView;
    private Location currentLocation;
    private Button refreshQueryButton;
    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_results);

        //Get display dimensions
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;

        GPSTracker gps = new GPSTracker(QueryResults.this);

        // check if GPS enabled
        if(gps.canGetLocation()){
            currentLocation = new Location("");
            currentLocation.setLatitude(gps.getLatitude());
            currentLocation.setLongitude(gps.getLongitude());
        } else {
            currentLocation = null;
            gps.showSettingsAlert();
        }


        refreshQueryButton = (Button) findViewById(R.id.refreshQueryButton);
        queryResultsLayout = (LinearLayout) findViewById(R.id.queryResultsLayout);
        queryResultsScrollView = (ScrollView) findViewById(R.id.queryResultsScrollView);

        refreshQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAndRefreshTable();
            }
        });

        clearAndRefreshTable();



    }





    public void clearAndRefreshTable() {

        while (queryResultsLayout.getChildCount() >1) {
            ((LinearLayout) queryResultsLayout.getChildAt(1)).removeAllViews();
            queryResultsLayout.removeViewAt(1);
        }
        refreshAndPopulate();
    }

    public void refreshAndPopulate() {
        List<ResultRowItem> resultRows = new APIInterface().runQueryAndSort(UserListView.userValues, currentLocation);


        //Populate table
        for (ResultRowItem result : resultRows) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.query_view_row, null);

            String imageName = result.getRouteLine().getID().toLowerCase() + "_line_icon";

            if (result.getTransportMode().equals("Bus")) {
                ((ImageView) myView.findViewById(R.id.transportModeImageQueryResult)).setImageResource(R.drawable.bus_icon);
                ((TextView) myView.findViewById(R.id.routeLineQueryResult)).setText(result.getRouteLine().getID());

            } else if (result.getTransportMode().equals("Tube")) {
                ((ImageView) myView.findViewById(R.id.transportModeImageQueryResult)).setImageResource(getApplicationContext().getResources().getIdentifier(imageName, "drawable", getApplicationContext().getPackageName()));
                ((TextView) myView.findViewById(R.id.routeLineQueryResult)).setText(result.getRouteLine().getAbrvName());
            }

            ((TextView) myView.findViewById(R.id.startingStopQueryResult)).setText(result.getStopStationName());
            ((TextView) myView.findViewById(R.id.directionQueryResult)).setText(result.getDestination());
            ((TextView) myView.findViewById(R.id.timeQueryResult)).setText(result.getTimeUntilArrivalFormattedString());
            queryResultsLayout.addView(myView);
        }
        // If child count is 1, there are no results (the header row is the only child)
        if (queryResultsLayout.getChildCount() == 1) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setMinimumWidth(screenWidth);
            linearLayout.setMinimumHeight(screenWidth);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            TextView tv = new TextView(this);
            tv.setTextSize(20);
            tv.setTypeface(null, Typeface.ITALIC);
            if (!isNetworkAvailable()) {
                tv.setText(R.string.no_connection);
            } else {
                tv.setText(R.string.no_results);
            }
            linearLayout.addView(tv);
            queryResultsLayout.addView(linearLayout);
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
