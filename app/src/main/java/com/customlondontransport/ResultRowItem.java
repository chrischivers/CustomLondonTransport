package com.customlondontransport;

import java.util.Comparator;

/**
 * Created by Chris on 09/07/2014.
 */
public class ResultRowItem implements Comparable<ResultRowItem> {

    private String transportMode;
    private String routeLine;
    private String StopStationName;
    private String Destination;
    private Long TimeUntilArrival;

    public String getTransportMode() {
        return transportMode;
    }

    public String getRouteLine() {
        return routeLine;
    }

    public String getStopStationName() {
        return StopStationName;
    }

    public String getDestination() {
        return Destination;
    }

    public Long getTimeUntilArrival() {
        return TimeUntilArrival;
    }

    public ResultRowItem(String transportMode, String routeLine, String stopStationName, String destination, Long timeUntilArrival) {

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
}
