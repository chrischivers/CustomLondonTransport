package com.customlondontransport;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Result;

/**
 * Created by Chris on 10/07/2014.
 */
public class QueryResults extends Activity {

    private TableLayout queryResultsTable;
    private List<ResultRowItem> resultRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_results);
        resultRows = new ArrayList<ResultRowItem>();
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

        // Iterate through each line of the table adding to resultRows List
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
                        for (ResultRowItem result : fetchRowData(new ComboItem("Bus"), uri.getRouteLine(), uri.getStartingStop(), uri.getDirection())) {
                            if (j < numberToObtain || numberToObtain == -1) {
                                System.out.println(result);
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



            // Create a TableRow and give it an ID
            TableRow tr = new TableRow(this);
            tr.setId(++tableRowIDCounter);
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            // Transport Mode
            TextView transportMode = new TextView(this);
            transportMode.setId(tableRowIDCounter+1);
            transportMode.setText(result.getTransportMode());
            transportMode.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(transportMode);

            // Route LineRow
            TextView routeLine = new TextView(this);
            routeLine.setId(tableRowIDCounter+2);
            routeLine.setText(result.getRouteLine());
            routeLine.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(routeLine);

            // Starting Stop Station
            TextView startingStopStation = new TextView(this);
            startingStopStation.setId(tableRowIDCounter+3);
            startingStopStation.setText(result.getStopStationNameTrimmed());
            startingStopStation.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(startingStopStation);

            // Destination
            TextView destination = new TextView(this);
            destination.setId(tableRowIDCounter+4);
            destination.setText(result.getDestinationTrimmed());
            destination.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(destination);

            // Time To Arrival
            TextView timeToArrival = new TextView(this);
            timeToArrival.setId(tableRowIDCounter+5);
            timeToArrival.setText(result.getTimeUntilArrivalFormattedString());
            timeToArrival.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(timeToArrival);

            // Add the TableRow to the TableLayout
            queryResultsTable.addView(tr, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }

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

