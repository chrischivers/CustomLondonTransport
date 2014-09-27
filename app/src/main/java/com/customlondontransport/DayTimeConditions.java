package com.customlondontransport;


import android.content.Context;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DayTimeConditions implements Serializable {
    private Date fromTime;
    private Date toTime;
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private int radiusFromStartingStop  = -1;
    private Context context;

    //Sunday to Saturday
    private boolean[] selectedDays = null;

    // Constructor for time info and day info
    public DayTimeConditions(int fromTimeHour, int fromTimeMinutes, int toTimeHour, int toTimeMinutes, boolean[] selectedDays, int radiusFromStartingStop, Context context) throws ParseException {
        String fromTimeFormatted = ((fromTimeHour < 10) ? "0" + fromTimeHour : Integer.toString(fromTimeHour)) + ":" + ((fromTimeMinutes < 10) ? "0" + fromTimeMinutes : Integer.toString(fromTimeMinutes));
        this.fromTime = new SimpleDateFormat("HH:mm").parse(fromTimeFormatted);

        String toTimeFormatted = ((toTimeHour < 10) ? "0" + toTimeHour : Integer.toString(toTimeHour)) + ":" + ((toTimeMinutes < 10) ? "0" + toTimeMinutes : Integer.toString(toTimeMinutes));
        this.toTime = new SimpleDateFormat("HH:mm").parse(toTimeFormatted);

        this.selectedDays = new boolean[7];
        this.selectedDays = selectedDays;
        this.radiusFromStartingStop = radiusFromStartingStop;
    }

    // Constructor if no time info required
    public DayTimeConditions(boolean[] selectedDays, int radiusFromStartingStop, Context context) throws ParseException {
        this.selectedDays = new boolean[7];
        this.selectedDays = selectedDays;
        this.radiusFromStartingStop = radiusFromStartingStop;
    }

    // Constructor if no day info required
    public DayTimeConditions(int fromTimeHour, int fromTimeMinutes, int toTimeHour, int toTimeMinutes, int radiusFromStartingStop, Context context) throws ParseException {
        String fromTimeFormatted = ((fromTimeHour < 10) ? "0" + fromTimeHour : Integer.toString(fromTimeHour)) + ":" + ((fromTimeMinutes < 10) ? "0" + fromTimeMinutes : Integer.toString(fromTimeMinutes));
        this.fromTime = new SimpleDateFormat("HH:mm").parse(fromTimeFormatted);

        String toTimeFormatted = ((toTimeHour < 10) ? "0" + toTimeHour : Integer.toString(toTimeHour)) + ":" + ((toTimeMinutes < 10) ? "0" + toTimeMinutes : Integer.toString(toTimeMinutes));
        this.toTime = new SimpleDateFormat("HH:mm").parse(toTimeFormatted);

        this.radiusFromStartingStop = radiusFromStartingStop;
    }

    // Constructor if no time or day info required
    public DayTimeConditions(int radiusFromStartingStop, Context context) {
        this.radiusFromStartingStop = radiusFromStartingStop;
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

    public int getRadiusFromStartingStop() {
        return this.radiusFromStartingStop;
    }

    @Override
    public String toString() {
        String daysString = "";
        String fromTimeTwoDigits = context.getString(R.string.conditions_AnyTime) + ". ";
        String toTimeTwoDigits = "";
        String radiusString = "";

        String[] dayArray = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        if (selectedDays != null) {
            for (int i = 0; i < this.selectedDays.length; i++) {
                if (this.selectedDays[i]) {
                    daysString = daysString + dayArray[i] + ", ";
                }
            }
        } else {
            daysString = context.getString(R.string.conditions_AnyDay) + ". ";
        }
        if (fromTime != null && toTime != null) {
            fromTimeTwoDigits = context.getString(R.string.conditions_From) + ": " + dateFormat.format(fromTime);
            toTimeTwoDigits = context.getString(R.string.conditions_To) + ": " + dateFormat.format(toTime);
        }
        if (radiusFromStartingStop != -1 ) {
            radiusString = radiusFromStartingStop + " " + context.getString(R.string.conditions_metresFromStartingStationStop);
        }
        return daysString + fromTimeTwoDigits + toTimeTwoDigits + "\n" + radiusString;
    }

    public boolean isCurrentTimeWithinRange() {
        Date currentDate = new Date();
        return dateFormat.format(currentDate).compareTo(dateFormat.format(this.fromTime)) >= 0 && dateFormat.format(currentDate).compareTo(dateFormat.format(this.toTime)) <= 0;

    }
}