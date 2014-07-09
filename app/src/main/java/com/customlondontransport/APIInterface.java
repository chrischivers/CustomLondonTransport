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

public class APIInterface  {


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


    public List<List<String>> fetchBusData(String busNumber, String busStop, String direction) throws {
        List<List<String>> busDataList = new ArrayList<List<String>>();

        URL TflBusAPIURL = null;
        TflBusAPIURL = new URL("http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1?LineName=" + busNumber + "&StopCode1=" + busStop.getID() + "&DirectionID=" + direction.getID() + "&VisitNumber=1&ReturnList=LineName,StopPointName,DestinationText,EstimatedTime");

        BufferedReader in = new BufferedReader(new InputStreamReader(TflBusAPIURL.openStream()));
        CSVReader reader = new CSVReader(in, ',', '"', 1);
        String [] lineData;
        while ((lineData = reader.readNext()) != null) {
            List<String> temporaryList = new ArrayList<String>();
            temporaryList.add("Bus");
            temporaryList.add(lineData[2]);
            temporaryList.add(lineData[1]);
            temporaryList.add(lineData[3]);
            temporaryList.add(lineData[4].substring(0, lineData[4].length()-1)); //cutting off final [ character
            busDataList.add(temporaryList);

        }
        reader.close();
        return busDataList;
    }

}


