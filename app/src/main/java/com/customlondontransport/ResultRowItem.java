package com.customlondontransport;

/**
 * Created by Chris on 09/07/2014.
 */
public class ResultRowItem {

    private String transportMode;
    private String routeLine;
    private String StopStationName;
    private String Destination;
    private String TimeUntilArrival;

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

    public String getTimeUntilArrival() {
        return TimeUntilArrival;
    }

    public ResultRowItem(String transportMode, String routeLine, String stopStationName, String destination, String timeUntilArrival) {

        this.transportMode = transportMode;
        this.routeLine = routeLine;
        StopStationName = stopStationName;
        Destination = destination;
        TimeUntilArrival = timeUntilArrival;
    }
}
