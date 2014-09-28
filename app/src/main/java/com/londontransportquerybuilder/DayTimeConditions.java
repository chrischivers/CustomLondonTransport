package com.londontransportquerybuilder;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DayTimeConditions implements Serializable {
    private Date fromTime;
    private Date toTime;
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private Integer radiusFromStartingStop;

    //Sunday to Saturday
    private boolean[] selectedDays = new boolean[7];

    // Constructor for time info and day info
    public DayTimeConditions(Integer fromTimeHour, Integer fromTimeMinutes, Integer toTimeHour, Integer toTimeMinutes, boolean[] selectedDays, Integer radiusFromStartingStop) throws ParseException {

        if (fromTimeHour != null && fromTimeMinutes != null) {
            String fromTimeFormatted = ((fromTimeHour < 10) ? "0" + fromTimeHour : Integer.toString(fromTimeHour)) + ":" + ((fromTimeMinutes < 10) ? "0" + fromTimeMinutes : Integer.toString(fromTimeMinutes));
            this.fromTime = new SimpleDateFormat("HH:mm").parse(fromTimeFormatted);
        }

        if (toTimeHour != null && toTimeMinutes != null) {
            String toTimeFormatted = ((toTimeHour < 10) ? "0" + toTimeHour : Integer.toString(toTimeHour)) + ":" + ((toTimeMinutes < 10) ? "0" + toTimeMinutes : Integer.toString(toTimeMinutes));
            this.toTime = new SimpleDateFormat("HH:mm").parse(toTimeFormatted);
        }
        if (radiusFromStartingStop != null) {
            this.radiusFromStartingStop = radiusFromStartingStop;
        }
        this.selectedDays = selectedDays;
    }


    public Date getFromTime() {
        // Sets date to current date before returning to allow Date.After and Date.Before methods to run correctly. Returns null if time not set.
        if (this.fromTime==null) {
            return null;
        } else {
            return this.fromTime;
        }
    }


    public Date getToTime() {
        // Sets date to current date before returning to allow Date.After and Date.Before methods to run correctly. Returns null if time not set.
        if (this.toTime==null) {
            return null;
        } else {
            return this.toTime;
        }
    }

    public boolean[] getSelectedDays() {
        return this.selectedDays;
    }

    public Integer getRadiusFromStartingStop() {
        return this.radiusFromStartingStop;
    }

    @Override
    public String toString() {
        String daysString = "";
        String fromTimeTwoDigits = "";
        String toTimeTwoDigits = "";
        String radiusString = "";

        String[] dayArray = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        if (selectedDays != null) {
            if (!areSelectedDaysAllFalse(selectedDays)) {
                for (int i = 0; i < this.selectedDays.length; i++) {
                    if (this.selectedDays[i]) {
                        daysString = daysString + dayArray[i] + ", ";
                    }
                }
            } else {
                daysString = "Any day. ";
            }
        } else {
            daysString = "Any day. ";
        }

        if (fromTime != null && toTime != null) {
            fromTimeTwoDigits = "From: " + dateFormat.format(fromTime);
            toTimeTwoDigits = " to: " + dateFormat.format(toTime);
        } else {
            fromTimeTwoDigits = "Any time. ";
        }
        if (radiusFromStartingStop != null ) {
            radiusString = radiusFromStartingStop + " metres from stating station/stop";
        } else {
            radiusString = "Any radius. ";
        }
        return daysString + fromTimeTwoDigits + toTimeTwoDigits + "\n" + radiusString;
    }

    public boolean isCurrentTimeWithinRange() {
        Date currentDate = new Date();
        return dateFormat.format(currentDate).compareTo(dateFormat.format(this.fromTime)) >= 0 && dateFormat.format(currentDate).compareTo(dateFormat.format(this.toTime)) <= 0;

    }

    public boolean areSelectedDaysAllFalse (boolean[] array) {
        for(boolean b : array) if(b) return false;
        return true;
    }

}