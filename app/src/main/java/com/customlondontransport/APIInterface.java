package com.customlondontransport;

import android.location.Location;
import android.os.AsyncTask;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class APIInterface   {


    public List<ResultRowItem> fetchBusRouteData(RouteLine busRoute, StationStop busStop, Direction busDirection){
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

    public List<ResultRowItem> fetchBusStationData(StationStop busStop, List<String> busRouteList){
        List<ResultRowItem> busDataList = new ArrayList<ResultRowItem>();
        String busRouteListString = busRouteList.toString().replace("[", "").replace("]", "").replace(", ", ","); // Formats List to remove spaces and brackets

        try {
            URL url = new URL("http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1?LineName=" + busRouteListString + "&StopCode1=" + busStop.getID()  + "&VisitNumber=1&ReturnList=LineName,StopPointName,DestinationText,EstimatedTime");
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

    public List<ResultRowItem> fetchTubeRouteData(RouteLine tubeLine, StationStop tubeStation, Direction directionPlatform) {
        List<ResultRowItem> tubeDataList = new ArrayList<ResultRowItem>();
        try {
            URL url = new URL("http://cloud.tfl.gov.uk/TrackerNet/PredictionDetailed/" + tubeLine.getID() + "/" + tubeStation.getID());
            System.out.println(url);
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

    public List<ResultRowItem> fetchTubeStationData(StationStop tubeStation, List directionPlatformLine) {
        List<ResultRowItem> tubeDataList = new ArrayList<ResultRowItem>();
        Set<String> tubeLinesSet = new HashSet<String>();

        // Add all the lines contained in the directionPlatformLine list into a set
        for (Object direction : directionPlatformLine) {
            tubeLinesSet.add(((Direction) direction).getLine().getID());
        }

        // Call HTTP lookup for each line in the tubeLinesSet
        for (String tubeLine : tubeLinesSet) {
            try {
                URL url = new URL("http://cloud.tfl.gov.uk/TrackerNet/PredictionDetailed/" + tubeLine + "/" + tubeStation.getID());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String inputLine;

                while ((inputLine = reader.readLine()) != null) {
                    for (Object direction : directionPlatformLine) {
                        if (inputLine.matches("\\s+<P N=\"" + ((Direction) direction).getLabel() + ".*")) {
                            try {
                                inputLine = reader.readLine();
                                while (!inputLine.contains("</P>") && !inputLine.contains("</S>") && !inputLine.matches("\\s+<P N=.*")) {

                                    int destinationIndex = inputLine.indexOf("Destination=\"") + 13;
                                    int secondsToIndex = inputLine.indexOf("SecondsTo=\"") + 11;

                                    tubeDataList.add(new ResultRowItem("Tube", ((Direction) direction).getLine(), tubeStation.getLabel(), inputLine.substring(destinationIndex, inputLine.indexOf("\"", destinationIndex)), Long.parseLong(inputLine.substring(secondsToIndex, inputLine.indexOf("\"", secondsToIndex)))));
                                    inputLine = reader.readLine();
                                }
                            } catch (IndexOutOfBoundsException ex) {
                                System.out.println("Index out of bounds exception");

                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
        return tubeDataList;
    }


    public List<ResultRowItem> runQueryAndSort(List<UserItem> userList, Location currentLocation) {
        List<ResultRowItem> resultRows = new ArrayList<ResultRowItem>();
        //clearOutputListTable();

        // get current day of the week. 1 - 7 from Sunday to Saturday
        Calendar c = Calendar.getInstance();
        int currentDayOfTheWeek = c.get(Calendar.DAY_OF_WEEK);

        // Iterate through each line of the table adding to resultRows List
        for (UserItem userItem: userList) {
            boolean processThisRow = false;

            // if condition does not equal null
            if (userItem.getDayTimeConditions() != null) {
                // if condition contain currents day of the week process this row
                if (userItem.getDayTimeConditions().getSelectedDays() != null) {
                    if (userItem.getDayTimeConditions().getSelectedDays()[currentDayOfTheWeek - 1]) {
                        // if time or date is null (i.e. any time) process the row
                        if (userItem.getDayTimeConditions().getFromTime() == null || userItem.getDayTimeConditions().getToTime() == null) {
                            processThisRow = true;
                            // if current time is within to/from time range
                        } else if (userItem.getDayTimeConditions().isCurrentTimeWithinRange()) {
                            processThisRow = true;
                        }
                    }
                } else {
                    processThisRow = true;
                }


            //Check location is within the range

                if (currentLocation != null) {
                    Location startingStopLocation = new Location("");
                    startingStopLocation.setLongitude(userItem.getStartingStop().getLongitudeCoordinate());
                    startingStopLocation.setLatitude(userItem.getStartingStop().getLatitudeCoordinate());

                    // Set processThisRow to false if the distance is greater than the given radius
                    if (userItem.getDayTimeConditions().getRadiusFromStartingStop() != -1) {
                        System.out.println("current location dist to: " + currentLocation.distanceTo(startingStopLocation));
                        System.out.println("radius: " + userItem.getDayTimeConditions().getRadiusFromStartingStop());
                        if (currentLocation.distanceTo(startingStopLocation) > userItem.getDayTimeConditions().getRadiusFromStartingStop()) {
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
                if (userItem instanceof UserRouteItem) {
                    int numberToObtain = userItem.getMaxNumberToShow(); // 0 = all
                    try {
                        if (userItem.getTransportForm().equals("Bus")) {
                            int j = 0;
                            for (ResultRowItem result : fetchRouteRowData("Bus", ((UserRouteItem) userItem).getRouteLine(), userItem.getStartingStop(), ((UserRouteItem) userItem).getDirection())) {
                                if (j < numberToObtain || numberToObtain == 0) {
                                    resultRows.add(result);
                                    j++;
                                }
                            }
                        } else if (userItem.getTransportForm().equals("Tube")) {
                            int j = 0;
                            for (ResultRowItem result : fetchRouteRowData("Tube", ((UserRouteItem) userItem).getRouteLine(), userItem.getStartingStop(), ((UserRouteItem) userItem).getDirection())) {
                                if (j < numberToObtain || numberToObtain == 0) {
                                    resultRows.add(result);
                                    j++;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (userItem instanceof UserStationItem) {
                    int numberToObtain = userItem.getMaxNumberToShow(); // 0 = all
                    try {
                        if (userItem.getTransportForm().equals("Bus")) {
                            int j = 0;
                            for (ResultRowItem result : fetchStationRowData("Bus", userItem.getStartingStop(), ((UserStationItem) userItem).getRouteLineList())) {
                                if (j < numberToObtain || numberToObtain == 0) {
                                    resultRows.add(result);
                                    j++;
                                }
                            }
                        } else if (userItem.getTransportForm().equals("Tube")) {
                        System.out.println("Here");
                            int j = 0;
                            for (ResultRowItem result : fetchStationRowData("Tube", userItem.getStartingStop(), ((UserStationItem) userItem).getRouteLineList())) {
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
        }

        // Sort resultRows list by time
        Collections.sort(resultRows);
        return resultRows;
    }

    private  synchronized List<ResultRowItem> fetchRouteRowData(String transportType, RouteLine routeLine, StationStop startingStopStation, Direction direction) {
        APIFetcher apifetcher = new APIFetcher();
        apifetcher.execute("Route", transportType, routeLine, startingStopStation, direction);
        return apifetcher.getRowData();
    }

    private  synchronized List<ResultRowItem> fetchStationRowData(String transportType, StationStop startingStopStation, List routeLineList) {
            APIFetcher apifetcher = new APIFetcher();
            apifetcher.execute("Station", transportType, startingStopStation, routeLineList);
            return apifetcher.getRowData();
        }


    class APIFetcher extends AsyncTask<Object, Void, Void> {

        List<ResultRowItem> rowData;

        @Override
        protected synchronized Void doInBackground(Object... objects) {
            rowData = null;
            if (objects[0].equals("Route")) {
                if (objects[1].equals("Tube")) {
                    rowData = (new APIInterface().fetchTubeRouteData(((RouteLine) objects[2]), ((StationStop) objects[3]), ((Direction) objects[4])));
                } else if (objects[1].equals("Bus")) {
                    rowData = (new APIInterface().fetchBusRouteData(((RouteLine) objects[2]), ((StationStop) objects[3]), ((Direction) objects[4])));
                } else {
                    throw new IllegalArgumentException("Invalid transport type");
                }
            } else if (objects[0].equals("Station")) {
                if (objects[1].equals("Tube")) {
                    rowData = (new APIInterface().fetchTubeStationData(((StationStop) objects[2]), (List) objects[3]));
                } else if (objects[1].equals("Bus")) {
                    rowData = (new APIInterface().fetchBusStationData(((StationStop) objects[2]), ((List) objects[3])));
                } else {
                    throw new IllegalArgumentException("Invalid transport type");
                }
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






