package com.customlondontransport;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class APIInterface   {


    public List<ComboItem> fetchTubeDirectionsAndPlatforms(String tubeLineID, String tubeStationID) {
        List<ComboItem> tubeDirectionPlatformList = new ArrayList<ComboItem>();

        try {

            URL url = new URL("http://cloud.tfl.gov.uk/TrackerNet/PredictionDetailed/" + tubeLineID + "/" + tubeStationID);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                if(inputLine.matches("\\s+<P N=.*")) {
                    String directionPlatform = inputLine.substring(inputLine.indexOf("\"")+1,inputLine.indexOf("\" Num"));
                    tubeDirectionPlatformList.add(new ComboItem(directionPlatform));
                }
            }
        } catch(IOException ex){
            System.out.println(ex);
        }
        return tubeDirectionPlatformList;
    }


    public List<ResultRowItem> fetchBusData(ComboItem busRoute, ComboItem busStop, ComboItem busDirection){
        List<ResultRowItem> busDataList = new ArrayList<ResultRowItem>();

        try {
            URL url = new URL("http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1?LineName=" + busRoute.getID() + "&StopCode1=" + busStop.getID() + "&DirectionID=" + busDirection.getID() + "&VisitNumber=1&ReturnList=LineName,StopPointName,DestinationText,EstimatedTime");
            System.out.println(url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String inputLine;
            reader.readLine(); //skip first line (header)

            while ((inputLine = reader.readLine()) != null) {
                String[] inputLineSplit = inputLine.split(",");

                String startingStopFormatted = inputLineSplit[1].substring(1,inputLineSplit[1].length()-1);
                String busRouteFormatted = inputLineSplit[2].substring(1,inputLineSplit[2].length()-1);
                String destinationFormatted = inputLineSplit[3].substring(1,inputLineSplit[3].length()-1);
                Long secondsTo = ((Long.parseLong(inputLineSplit[4].substring(0,inputLineSplit[4].length()-1))-System.currentTimeMillis())/1000);


                busDataList.add(new ResultRowItem("Bus",busRouteFormatted,startingStopFormatted,destinationFormatted, secondsTo));

            }

        } catch(IOException ex){
            System.out.println(ex);
        }

        return busDataList;
    }

    public List<ResultRowItem> fetchTubeData(ComboItem tubeLine, ComboItem tubeStation, ComboItem directionPlatform) {
        List<ResultRowItem> tubeDataList = new ArrayList<ResultRowItem>();
        try {
            URL url = new URL("http://cloud.tfl.gov.uk/TrackerNet/PredictionDetailed/" + tubeLine.getID() + "/" + tubeStation.getID());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String inputLine;

            while ((inputLine = reader.readLine()) != null) {

                if (inputLine.matches("\\s+<P N=\"" + directionPlatform.getID() + ".*")) {
                    try {
                        inputLine = reader.readLine();
                        while (!inputLine.contains("</P>") && !inputLine.contains("</S>") && !inputLine.matches("\\s+<P N=.*")) {

                            int destinationIndex = inputLine.indexOf("Destination=\"") + 13;
                            int secondsToIndex = inputLine.indexOf("SecondsTo=\"") + 11;

                            tubeDataList.add(new ResultRowItem("Tube", tubeLine.getLabel(), tubeStation.getLabel(), inputLine.substring(destinationIndex, inputLine.indexOf("\"", destinationIndex)), Long.parseLong(inputLine.substring(secondsToIndex, inputLine.indexOf("\"", secondsToIndex)))));
                            inputLine = reader.readLine();
                        }
                    } catch (IndexOutOfBoundsException ex) {
                        System.out.println("Index out of bounds exception");

                    }
                }
            }
        } catch(IOException ex){
            System.out.println(ex);
        }
        return tubeDataList;
    }

    public List runQueryAndSort() {
        List resultRows = new ArrayList<ResultRowItem>();
        APIInterface api = new APIInterface();
        //clearOutputListTable();

        int tableRowIDCounter = 0;

        // get current day of the week. 1 - 7 from Sunday to Saturday
        Calendar c = Calendar.getInstance();
        int currentDayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);

        // Iterate through each line of the table adding to resultRows List
        for (UserRouteItem userRouteItem: UserListView.userRouteValues) {
            boolean processThisRow = false;

            // if condition does not equal null
            if (userRouteItem.getDayTimeConditions() != null) {
                // if condition contain currents day of the week process this row
                if (((DayTimeConditions) userRouteItem.getDayTimeConditions()).getSelectedDays()[currentDayOfTheWeek - 1]) {

                    // if time or date is null (i.e. any time) process the row
                    if (((DayTimeConditions) userRouteItem.getDayTimeConditions()).getFromTime() == null || ((DayTimeConditions) userRouteItem.getDayTimeConditions()).getToTime() == null) {
                        processThisRow = true;
                        // if current time is within to/from time range
                    } else if (((DayTimeConditions) userRouteItem.getDayTimeConditions()).isCurrentTimeWithinRange()) {
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
                    if (userRouteItem.getTransportForm().equals("Bus")) {
                        int j = 0;
                        for (ResultRowItem result : fetchRowData(new ComboItem("Bus"), userRouteItem.getRouteLine(), userRouteItem.getStartingStop(), userRouteItem.getDirection())) {
                            if (j < numberToObtain || numberToObtain == -1) {
                                resultRows.add(result);
                                j++;
                            }
                        }
                    } else if (userRouteItem.getTransportForm().equals("Tube")) {
                        int j = 0;
                        for (ResultRowItem result : fetchRowData(new ComboItem("Tube"), userRouteItem.getRouteLine(), userRouteItem.getStartingStop(), userRouteItem.getDirection())) {
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
        return resultRows;
    }

    private  synchronized List<ResultRowItem> fetchRowData(ComboItem transportType, ComboItem routeLine, ComboItem startingStopStation, ComboItem direction) {
        APIFetcher apifetcher = new APIFetcher();
        apifetcher.execute(transportType, routeLine, startingStopStation, direction);
        return apifetcher.getRowData();
    }

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


