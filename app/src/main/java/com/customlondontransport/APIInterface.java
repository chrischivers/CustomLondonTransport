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


}


