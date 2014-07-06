package com.customlondontransport;


        import java.io.Serializable;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;

public class DayTimeConditions implements Serializable {
    private Calendar fromTime = null;
    private Calendar toTime = null;

    //Sunday to Saturday
    private boolean[] selectedDays = null;

    // Constructor for time info and day info
    public DayTimeConditions(int fromTimeHour, int fromTimeMinutes, int toTimeHour, int toTimeMinutes, boolean[] selectedDays) throws ParseException {
        String fromTimeFormatted = ((fromTimeHour < 10) ? "0" + fromTimeHour : Integer.toString(fromTimeHour)) + ":" + ((fromTimeMinutes < 10) ? "0" + fromTimeMinutes : Integer.toString(fromTimeMinutes));
        Date fromTimeDate = new SimpleDateFormat("HH:mm").parse(fromTimeFormatted);
        this.fromTime = Calendar.getInstance();
        this.fromTime.setTime(fromTimeDate);

        String toTimeFormatted = ((toTimeHour < 10) ? "0" + toTimeHour : Integer.toString(toTimeHour)) + ":" + ((toTimeMinutes < 10) ? "0" + toTimeMinutes : Integer.toString(toTimeMinutes));
        Date toTimeDate = new SimpleDateFormat("HH:mm").parse(toTimeFormatted);
        this.toTime = Calendar.getInstance();
        this.toTime.setTime(toTimeDate);

        this.selectedDays = new boolean[7];
        this.selectedDays = selectedDays;
    }

    // Constructor if no time info required
    public DayTimeConditions(boolean[] selectedDays) throws ParseException {
        this.selectedDays = new boolean[7];
        this.selectedDays = selectedDays;
    }

    public Calendar getfromTime() {
        // Sets date to current date before returning to allow Date.After and Date.Before methods to run correctly. Returns null if time not set.
        if (this.fromTime==null) {
            return null;
        } else {
            return this.fromTime;
        }
    }


    public Calendar gettoTime() {
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
            fromTimeTwoDigits = "From: " + String.format("%02d:%02d", this.fromTime.get(Calendar.HOUR_OF_DAY), this.fromTime.get(Calendar.MINUTE));
            toTimeTwoDigits = " To: " + String.format("%02d:%02d", this.toTime.get(Calendar.HOUR_OF_DAY), this.toTime.get(Calendar.MINUTE));
        }
        return daysString + fromTimeTwoDigits + toTimeTwoDigits;
    }
}