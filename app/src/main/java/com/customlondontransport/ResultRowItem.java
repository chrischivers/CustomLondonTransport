package com.customlondontransport;

import java.util.Comparator;

/**
 * Created by Chris on 09/07/2014.
 */
public class ResultRowItem implements Comparable<ResultRowItem> {

    private String transportMode;
    private ComboItem routeLine;
    private String StopStationName;
    private String Destination;
    private Long TimeUntilArrival;

    public String getTransportMode() {
        return transportMode;
    }

    public ComboItem getRouteLine() {
        return routeLine;
    }

    public String getStopStationName() {
        return StopStationName;
    }

    /*public String getStopStationNameTrimmed () {
        if (StopStationName.length() < 10) {
            return StopStationName;
        } else {
            return StopStationName.substring(0,10) + "...";
        }
    }*/

    public String getDestination() {
        return Destination;
    }


    public Long getTimeUntilArrival() {
        return TimeUntilArrival;
    }

    public String getTimeUntilArrivalFormattedString () {

        String minutesRemaining = Long.toString((this.getTimeUntilArrival()) / 60);
        String secondsRemaining = Long.toString((this.getTimeUntilArrival()) % 60);

        // this loop adds '0' prefix to single digit seconds
        if (secondsRemaining.length() == 1) {
            secondsRemaining = "0" + secondsRemaining;
        }

        // this loop adds '0' prefix to single digit minutes
        if (minutesRemaining.length() == 1) {
            minutesRemaining = "0" + minutesRemaining;
        }

        return minutesRemaining + ":" + secondsRemaining;
    }

    public ResultRowItem(String transportMode, ComboItem routeLine, String stopStationName, String destination, Long timeUntilArrival) {

        this.transportMode = transportMode;
        this.routeLine = routeLine;
        StopStationName = stopStationName;
        Destination = destination;
        TimeUntilArrival = timeUntilArrival;
    }

     @Override
    public int compareTo(ResultRowItem resultRowItem) {
        if (this.getTimeUntilArrival() < resultRowItem.getTimeUntilArrival()) {
             return -1;
        } else if (this.getTimeUntilArrival() > resultRowItem.getTimeUntilArrival()) {
             return 1;
         } else {
             return 0;
         }
    }

    @Override
    public String toString() {
        return transportMode + ", " + routeLine + ", " + StopStationName + ", " + Destination + ", " +  TimeUntilArrival;
    }
}
