package com.customlondontransport;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Chris on 10/07/2014.
 */
public class QueryResults extends Activity {

    private TableLayout queryResultsTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_results);
        runQuery();
    }

    private void runQuery() {
        APIInterface api = new APIInterface();
        //clearOutputListTable();
        queryResultsTable = (TableLayout) findViewById(R.id.QueryResultsTable);
        int tableRowIDCounter = 0;

        // get current day of the week. 1 - 7 from Sunday to Saturday
        Calendar c = Calendar.getInstance();
        int currentDayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);
        Date currentTime = c.getTime();

        // Iterate through each line of the table
        for (UserRouteItem uri : UserListView.userRouteValues) {
            boolean processThisRow = false;

            // if condition does not equal null
            if (uri.getDayTimeConditions() != null) {
                // if condition contain currents day of the week process this row
                if (((DayTimeConditions) uri.getDayTimeConditions()).getSelectedDays()[currentDayOfTheWeek - 1]) {
                    // if time or date is null (i.e. any time) process the row
                    if (((DayTimeConditions) uri.getDayTimeConditions()).getfromTime() == null || ((DayTimeConditions) uri.getDayTimeConditions()).gettoTime() == null) {
                        processThisRow = true;
                        // if current time is within to/from time range
                    } else if (currentTime.after(((DayTimeConditions) uri.getDayTimeConditions()).getfromTime().getTime())
                            && currentTime.before(((DayTimeConditions) uri.getDayTimeConditions()).gettoTime().getTime())) {
                        processThisRow = true;
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
                        for (ResultRowItem result : api.fetchBusData(uri.getRouteLine().getID(), uri.getStartingStop().getID(), uri.getDirection().getID())) {
                            if (j < numberToObtain || numberToObtain == -1) {
                                String minutesRemaining = Long.toString((((Long.parseLong(result.getTimeUntilArrival()) - System.currentTimeMillis()) / 1000) / 60));
                                String secondsRemaining = (Long.toString((((Long.parseLong(result.getTimeUntilArrival()) - System.currentTimeMillis()) / 1000) % 60)));

                                // this loop adds '0' prefix to single digit seconds
                                if (secondsRemaining.length() == 1) {
                                    secondsRemaining = "0" + secondsRemaining;
                                }

                                // this loop adds '0' prefix to single digit minutes
                                if (minutesRemaining.length() == 1) {
                                    minutesRemaining = "0" + minutesRemaining;
                                }

                                //Output methods go here
                            }
                        }

                    } else if (uri.getTransportForm().equals("Tube")) {
                        int j = 0;
                        for (ResultRowItem result : fetchTubeData(uri.getRouteLine(), uri.getStartingStop(), uri.getDirection())) {
                            if (j < numberToObtain || numberToObtain == -1) {
                                String minutesRemaining = Long.toString(Long.parseLong(result.getTimeUntilArrival()) / 60);
                                String secondsRemaining = (Long.toString(Long.parseLong(result.getTimeUntilArrival()) - (Long.parseLong(minutesRemaining) * 60)));

                                // this loop adds '0' prefix to single digit seconds
                                if (secondsRemaining.length() == 1) {
                                    secondsRemaining = "0" + secondsRemaining;
                                }

                                // this loop adds '0' prefix to single digit minutes
                                if (minutesRemaining.length() == 1) {
                                    minutesRemaining = "0" + minutesRemaining;
                                }
                                // Create a TableRow and give it an ID
                                TableRow tr = new TableRow(this);
                                tr.setId(++tableRowIDCounter);
                                tr.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.FILL_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));

                                // Transport Mode
                                TextView transportMode = new TextView(this);
                                transportMode.setId(tableRowIDCounter+1);
                                transportMode.setText("Tube");
                                transportMode.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.FILL_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                tr.addView(transportMode);

                                // Route LineRow
                                TextView routeLine = new TextView(this);
                                routeLine.setId(tableRowIDCounter+2);
                                routeLine.setText(result.getRouteLine());
                                routeLine.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.FILL_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                tr.addView(routeLine);

                                // Starting Stop Station
                                TextView startingStopStation = new TextView(this);
                                startingStopStation.setId(tableRowIDCounter+3);
                                startingStopStation.setText(result.getStopStationName());
                                startingStopStation.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.FILL_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                tr.addView(startingStopStation);

                                // Destination
                                TextView destination = new TextView(this);
                                destination.setId(tableRowIDCounter+4);
                                destination.setText(result.getDestination());
                                destination.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.FILL_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                tr.addView(destination);

                                // Time To Arrival
                                TextView timeToArrival = new TextView(this);
                                timeToArrival.setId(tableRowIDCounter+5);
                                timeToArrival.setText(result.getTimeUntilArrival());
                                timeToArrival.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.FILL_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                tr.addView(timeToArrival);

                                // Add the TableRow to the TableLayout
                                queryResultsTable.addView(tr, new TableLayout.LayoutParams(
                                        TableRow.LayoutParams.FILL_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                            }

                            j++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
    private  synchronized List<ResultRowItem> fetchTubeData(ComboItem tubeLine, ComboItem tubeStation, ComboItem directionPlatform) {
        APIFetcher apifetcher = new APIFetcher();
        apifetcher.execute(tubeLine, tubeStation, directionPlatform);
        return apifetcher.getTubeData();
    }

    class APIFetcher extends AsyncTask<ComboItem, Void, Void> {

        List<ResultRowItem> tubeData;

        @Override
        protected synchronized Void doInBackground(ComboItem... comboItems) {
            tubeData = null;
            tubeData = (new APIInterface().fetchTubeData(comboItems[0],comboItems[1],comboItems[2]));
            notifyAll();
            return null;
        }

        public synchronized List<ResultRowItem> getTubeData() {
            while (tubeData == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return tubeData;
        }

    }
}

