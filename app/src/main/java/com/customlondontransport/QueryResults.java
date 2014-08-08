package com.customlondontransport;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class QueryResults extends Activity {

    private LinearLayout queryResultsLayout;
    private Button refreshQueryButton;
    private List<ResultRowItem> resultRows = new ArrayList<ResultRowItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_results);

        refreshQueryButton = (Button) findViewById(R.id.refreshQueryButton);
        queryResultsLayout = (LinearLayout) findViewById(R.id.queryResultsLayout);


        resultRows = new APIInterface().runQueryAndSort();

        //Populate table

        for (ResultRowItem result : resultRows) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.query_view_row, null);

            ((TextView) myView.findViewById(R.id.transportModeQueryResult)).setText(result.getTransportMode());
            ((TextView) myView.findViewById(R.id.routeLineQueryResult)).setText(result.getRouteLine());
            ((TextView) myView.findViewById(R.id.startingStopQueryResult)).setText(result.getStopStationName());
            ((TextView) myView.findViewById(R.id.directionQueryResult)).setText(result.getDestination());
            ((TextView) myView.findViewById(R.id.timeQueryResult)).setText(result.getTimeUntilArrivalFormattedString());
            queryResultsLayout.addView(myView);
        }

        refreshQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAndRefreshTable();
            }
        });
    }

    public void clearAndRefreshTable() {

        //TODO
        /*while (queryResultsTable.getChildCount() >1) {
            System.out.println(queryResultsTable.getChildCount());
            ((TableRow) queryResultsTable.getChildAt(1)).removeAllViews();
            queryResultsTable.removeViewAt(1);
        }
        runQuery();
*/
    }




}

