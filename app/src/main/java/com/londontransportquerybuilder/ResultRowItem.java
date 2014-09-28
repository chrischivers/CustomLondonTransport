package com.londontransportquerybuilder;

public class ResultRowItem implements Comparable<ResultRowItem> {

    private String transportMode;
    private RouteLine routeLine;
    private String StopStationName;
    private String Destination;
    private Long TimeUntilArrival;

    public String getTransportMode() {
        return transportMode;
    }

    public RouteLine getRouteLine() {
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

        /*String minutesRemaining = Long.toString((this.getTimeUntilArrival()) / 60);
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
        */
        int minutesRounded = Math.round((float)this.getTimeUntilArrival()/60);
        if (minutesRounded == 0) {
            return "due";
        } else {
            return minutesRounded + " min";
        }
    }

    public ResultRowItem(String transportMode, RouteLine routeLine, String stopStationName, String destination, Long timeUntilArrival) {

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
