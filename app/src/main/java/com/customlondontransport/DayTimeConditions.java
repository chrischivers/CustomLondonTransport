package com.customlondontransport;


        import java.io.Serializable;
        import java.text.DateFormat;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;

public class DayTimeConditions implements Serializable {
    private Date fromTime = new Date();
    private Date toTime = new Date();

    //Sunday to Saturday
    private boolean[] selectedDays = null;

    // Constructor for time info and day info
    public DayTimeConditions(int fromTimeHour, int fromTimeMinutes, int toTimeHour, int toTimeMinutes, boolean[] selectedDays) throws ParseException {
        String fromTimeFormatted = ((fromTimeHour < 10) ? "0" + fromTimeHour : Integer.toString(fromTimeHour)) + ":" + ((fromTimeMinutes < 10) ? "0" + fromTimeMinutes : Integer.toString(fromTimeMinutes));
        this.fromTime = new SimpleDateFormat("HH:mm").parse(fromTimeFormatted);

        String toTimeFormatted = ((toTimeHour < 10) ? "0" + toTimeHour : Integer.toString(toTimeHour)) + ":" + ((toTimeMinutes < 10) ? "0" + toTimeMinutes : Integer.toString(toTimeMinutes));
        this.toTime = new SimpleDateFormat("HH:mm").parse(toTimeFormatted);

        this.selectedDays = new boolean[7];
        this.selectedDays = selectedDays;
    }

    // Constructor if no time info required
    public DayTimeConditions(boolean[] selectedDays) throws ParseException {
        this.selectedDays = new boolean[7];
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

    @Override
    public String toString() {
        String daysString = "";
        String fromTimeTwoDigits = "Any time";
        String toTimeTwoDigits = "";

        String[] dayArray = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        if (selectedDays != null) {
            for (int i = 0; i < this.selectedDays.length; i++) {
                if (this.selectedDays[i]) {
                    daysString = daysString + dayArray[i] + ", ";
                }
            }
        }
        if (fromTime != null && toTime != null) {
            fromTimeTwoDigits = "From: " + this.fromTime;
            toTimeTwoDigits = " To: " + this.toTime;
        }
        return daysString + fromTimeTwoDigits + toTimeTwoDigits;
    }

    public boolean isCurrentTimeWithinRange() {
        DateFormat f = new SimpleDateFormat("HH:mm");
        Date currentDate = new Date();
        if (f.format(currentDate).compareTo(f.format(this.fromTime)) >= 0 && f.format(currentDate).compareTo(f.format(this.toTime)) <= 0) {
            return true;
        } else {
            return false;
        }

    }
}