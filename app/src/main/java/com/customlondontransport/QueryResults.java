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

/**
 * Created by Chris on 10/07/2014.
 */
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


        runQuery();

        refreshQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAndRefreshTable();
            }
        });
    }

    private void runQuery() {

        resultRows = new ArrayList<ResultRowItem>();
        APIInterface api = new APIInterface();
        //clearOutputListTable();

        int tableRowIDCounter = 0;

        // get current day of the week. 1 - 7 from Sunday to Saturday
        Calendar c = Calendar.getInstance();
        int currentDayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);

        // Iterate through each line of the table adding to resultRows List
        for (UserRouteItem uri : UserListView.userRouteValues) {
            boolean processThisRow = false;

            // if condition does not equal null
            if (uri.getDayTimeConditions() != null) {
                // if condition contain currents day of the week process this row
                if (((DayTimeConditions) uri.getDayTimeConditions()).getSelectedDays()[currentDayOfTheWeek - 1]) {

                    // if time or date is null (i.e. any time) process the row
                    if (((DayTimeConditions) uri.getDayTimeConditions()).getFromTime() == null || ((DayTimeConditions) uri.getDayTimeConditions()).getToTime() == null) {
                        processThisRow = true;
                        // if current time is within to/from time range
                    } else if (((DayTimeConditions) uri.getDayTimeConditions()).isCurrentTimeWithinRange()) {
                        processThisRow = true;
                        System.out.println("Here2");
                    }

                }
            }
            // else if condition equals null
            else {
                processThisRow = true;
            }
            // start processing row if processThisRow set to true
            if (processThisRow) {

                int numberToObtain = 5; //set as 5 for testing
                //TODO Add in Number to Obtain variable
                //if (queryListTableModel.getValueAt(i, 5).toString().equals("All")) {
                //    numberToObtain = -1;
                //} else {
                //    numberToObtain = Integer.parseInt(queryListTableModel.getValueAt(i, 5).toString());
                // }

                try {
                    if (uri.getTransportForm().equals("Bus")) {
                        int j = 0;
                        for (ResultRowItem result : fetchRowData(new ComboItem("Bus"), uri.getRouteLine(), uri.getStartingStop(), uri.getDirection())) {
                            if (j < numberToObtain || numberToObtain == -1) {
                                resultRows.add(result);
                                j++;
                            }
                        }
                    } else if (uri.getTransportForm().equals("Tube")) {
                        int j = 0;
                        for (ResultRowItem result : fetchRowData(new ComboItem("Tube"), uri.getRouteLine(), uri.getStartingStop(), uri.getDirection())) {
                            if (j < numberToObtain || numberToObtain == -1) {
                                resultRows.add(result);
                                j++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Sort resultRows list by time
        Collections.sort(resultRows);

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

    }

    public void clearAndRefreshTable() {

        /*while (queryResultsTable.getChildCount() >1) {
            System.out.println(queryResultsTable.getChildCount());
            ((TableRow) queryResultsTable.getChildAt(1)).removeAllViews();
            queryResultsTable.removeViewAt(1);
        }
        runQuery();
*/
    }

    private  synchronized List<ResultRowItem> fetchRowData(ComboItem transportType, ComboItem routeLine, ComboItem startingStopStation, ComboItem direction) {
        APIFetcher apifetcher = new APIFetcher();
        apifetcher.execute(transportType, routeLine, startingStopStation, direction);
        return apifetcher.getRowData();
    }

    class APIFetcher extends AsyncTask<ComboItem, Void, Void> {

        List<ResultRowItem> rowData;

        @Override
        protected synchronized Void doInBackground(ComboItem... comboItems) {
            rowData = null;
            if (comboItems[0].getID().equals("Tube")){
                rowData = (new APIInterface().fetchTubeData(comboItems[1], comboItems[2], comboItems[3]));
            } else if (comboItems[0].getID().equals("Bus")){
                rowData = (new APIInterface().fetchBusData(comboItems[1], comboItems[2], comboItems[3]));
            } else {
                throw new IllegalArgumentException("Invalid transport type");
            }
            notifyAll();
            return null;
        }

        public synchronized List<ResultRowItem> getRowData() {
            while (rowData == null) {
                try {
                    wait();
                    System.out.println("Waiting");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return rowData;
        }

      }
}

