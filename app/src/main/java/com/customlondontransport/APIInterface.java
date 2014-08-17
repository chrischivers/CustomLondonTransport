package com.customlondontransport;

import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class APIInterface   {


    public List<Direction> fetchTubeDirectionsAndPlatforms(String tubeLineID, String tubeStationID) {
        List<Direction> tubeDirectionPlatformList = new ArrayList<Direction>();

        try {

            URL url = new URL("http://cloud.tfl.gov.uk/TrackerNet/PredictionDetailed/" + tubeLineID + "/" + tubeStationID);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                if(inputLine.matches("\\s+<P N=.*")) {
                    String directionPlatform = inputLine.substring(inputLine.indexOf("\"")+1,inputLine.indexOf("\" Num"));
                    tubeDirectionPlatformList.add(new Direction(directionPlatform));
                }
            }
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return tubeDirectionPlatformList;
    }


    public List<ResultRowItem> fetchBusData(RouteLine busRoute, StationStop busStop, Direction busDirection){
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


                busDataList.add(new ResultRowItem("Bus",new RouteLine(busRouteFormatted),startingStopFormatted,destinationFormatted, secondsTo));

            }

        } catch(IOException ex){
            ex.printStackTrace();
        } catch(ArrayIndexOutOfBoundsException ex){
            //TODO error handlers
            ex.printStackTrace();
        }

        return busDataList;
    }

    public List<ResultRowItem> fetchTubeData(RouteLine tubeLine, StationStop tubeStation, Direction directionPlatform) {
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

                            tubeDataList.add(new ResultRowItem("Tube", tubeLine, tubeStation.getLabel(), inputLine.substring(destinationIndex, inputLine.indexOf("\"", destinationIndex)), Long.parseLong(inputLine.substring(secondsToIndex, inputLine.indexOf("\"", secondsToIndex)))));
                            inputLine = reader.readLine();
                        }
                    } catch (IndexOutOfBoundsException ex) {
                        System.out.println("Index out of bounds exception");

                    }
                }
            }
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return tubeDataList;
    }

    public List<ResultRowItem> runQueryAndSort(List<UserRouteItem> userRouteList, Location currentLocation) {
        List<ResultRowItem> resultRows = new ArrayList<ResultRowItem>();
        //clearOutputListTable();

        // get current day of the week. 1 - 7 from Sunday to Saturday
        Calendar c = Calendar.getInstance();
        int currentDayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);

        // Iterate through each line of the table adding to resultRows List
        for (UserRouteItem userRouteItem: userRouteList) {
            boolean processThisRow = false;

            // if condition does not equal null
            if (userRouteItem.getDayTimeConditions() != null) {
                // if condition contain currents day of the week process this row
                if (userRouteItem.getDayTimeConditions().getSelectedDays()[currentDayOfTheWeek - 1]) {

                    // if time or date is null (i.e. any time) process the row
                    if (userRouteItem.getDayTimeConditions().getFromTime() == null || userRouteItem.getDayTimeConditions().getToTime() == null) {
                        processThisRow = true;
                        // if current time is within to/from time range
                    } else if (userRouteItem.getDayTimeConditions().isCurrentTimeWithinRange()) {
                        processThisRow = true;
                    }
                }


            //Check location is within the range

                if (currentLocation != null) {
                    Location startingStopLocation = new Location("");
                    startingStopLocation.setLongitude(userRouteItem.getStartingStop().getLongitudeCoordinate());
                    startingStopLocation.setLatitude(userRouteItem.getStartingStop().getLatitudeCoordinate());

                    // Set processThisRow to false if the distance is greater than the given radius
                    if (userRouteItem.getDayTimeConditions().getRadiusFromStartingStop() != -1) {
                        if (currentLocation.distanceTo(startingStopLocation) > userRouteItem.getDayTimeConditions().getRadiusFromStartingStop()) {
                            processThisRow = false;
                        }
                    }
                } else {
                    //TODO
                    System.out.println("Current Location is null");
                }
            }
            // else if condition equals null
            else {
                processThisRow = true;
            }


            // start processing row if processThisRow set to true
            if (processThisRow) {

                int numberToObtain = userRouteItem.getMaxNumberToShow(); // 0 = all
                try {
                    if (userRouteItem.getTransportForm().equals("Bus")) {
                        int j = 0;
                        for (ResultRowItem result : fetchRowData("Bus", userRouteItem.getRouteLine(), userRouteItem.getStartingStop(), userRouteItem.getDirection())) {
                            if (j < numberToObtain || numberToObtain == 0) {
                                resultRows.add(result);
                                j++;
                            }
                        }
                    } else if (userRouteItem.getTransportForm().equals("Tube")) {
                        int j = 0;
                        for (ResultRowItem result : fetchRowData("Tube", userRouteItem.getRouteLine(), userRouteItem.getStartingStop(), userRouteItem.getDirection())) {
                            if (j < numberToObtain || numberToObtain == 0) {
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

    private  synchronized List<ResultRowItem> fetchRowData(String transportType, RouteLine routeLine, StationStop startingStopStation, Direction direction) {
        APIFetcher apifetcher = new APIFetcher();
        apifetcher.execute(transportType, routeLine, startingStopStation, direction);
        return apifetcher.getRowData();
    }

    class APIFetcher extends AsyncTask<Object, Void, Void> {

        List<ResultRowItem> rowData;

        @Override
        protected synchronized Void doInBackground(Object... objects) {
            rowData = null;
            if (objects[0].equals("Tube")){
                rowData = (new APIInterface().fetchTubeData(((RouteLine) objects[1]), ((StationStop) objects[2]), ((Direction) objects[3])));
            } else if (objects[0].equals("Bus")){
                rowData = (new APIInterface().fetchBusData(((RouteLine) objects[1]), ((StationStop) objects[2]), ((Direction) objects[3])));
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




