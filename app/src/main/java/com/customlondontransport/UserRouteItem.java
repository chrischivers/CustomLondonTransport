package com.customlondontransport;

import java.io.Serializable;

public class UserRouteItem implements Serializable {
    String transportForm;
    ComboItem routeLine;
    ComboItem direction;
    ComboItem startingStop;
    DayTimeConditions dayTimeConditions;
    int numberToShow;

    public UserRouteItem(String transportForm, ComboItem routeLine, ComboItem direction, ComboItem startingStop, DayTimeConditions dayTimeConditions, int numberToShow) {
        this.transportForm = transportForm;
        this.routeLine = routeLine;
        this.direction = direction;
        this.startingStop = startingStop;
        this.dayTimeConditions = dayTimeConditions;
        this.numberToShow = numberToShow;
    }

    public String getTransportForm() {
        return transportForm;
    }

    public ComboItem getRouteLine() {
        return routeLine;
    }

    public ComboItem getDirection() {
        return direction;
    }

    public ComboItem getStartingStop() {
        return startingStop;
    }

    public DayTimeConditions getDayTimeConditions() {
        return dayTimeConditions;
    }

    public int getNumberToShow() {
        return numberToShow;
    }

    // For UserList View - returns first line
    public String getLine1() {
        return transportForm + ": " + routeLine + "\nDirection: " + direction.toString();
    }

    // For UserList View - returns second line
    public String getLine2() {
        return "Starting at: " + startingStop + "\nConditions: " + dayTimeConditions;
    }


}
