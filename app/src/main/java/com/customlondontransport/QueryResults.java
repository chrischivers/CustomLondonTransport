package com.customlondontransport;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class QueryResults extends Activity  {

    private LinearLayout queryResultsLayout;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_results);


        GPSTracker gps = new GPSTracker(QueryResults.this);

        // check if GPS enabled
        if(gps.canGetLocation()){
            currentLocation = new Location("");
            currentLocation.setLatitude(gps.getLatitude());
            currentLocation.setLongitude(gps.getLongitude());

            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + currentLocation.getLatitude() + "\nLong: " + currentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        } else {
            currentLocation = null;
            gps.showSettingsAlert();
        }


        Button refreshQueryButton = (Button) findViewById(R.id.refreshQueryButton);
        queryResultsLayout = (LinearLayout) findViewById(R.id.queryResultsLayout);


        refreshAndPopulate();

        refreshQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAndRefreshTable();
            }
        });

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

    }


}
