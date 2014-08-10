package com.customlondontransport;

import java.io.Serializable;

public class UserRouteItem implements Serializable {
    String transportForm;
    ComboItem routeLine;
    ComboItem direction;
    ComboItem startingStop;
    DayTimeConditions dayTimeConditions;
    int maxNumberToShow;

    public UserRouteItem(String transportForm, ComboItem routeLine, ComboItem direction, ComboItem startingStop, DayTimeConditions dayTimeConditions, int maxNumberToShow) {
        this.transportForm = transportForm;
        this.routeLine = routeLine;
        this.direction = direction;
        this.startingStop = startingStop;
        this.dayTimeConditions = dayTimeConditions;
        this.maxNumberToShow = maxNumberToShow;
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

    public int getMaxNumberToShow() {
        return maxNumberToShow;
    }

    // For UserList View - returns first line
    public String getLine1() {

        String conditions;
        if (dayTimeConditions == null) {
            conditions = "\nNo conditions set";
        } else {
            conditions = "\nConditions: " + dayTimeConditions;
        }

        return "Direction: " + direction.toString() + "\nStarting at: " + startingStop + ". " + conditions;
    }

}
