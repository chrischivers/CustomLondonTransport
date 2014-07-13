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
                System.out.println("Input Line =" + inputLine);
                System.out.println("Direction Platform = " + directionPlatform.getID());

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

}


