package com.customlondontransport;

import java.io.Serializable;

public class UserRouteItem extends UserItem implements Serializable {
    RouteLine routeLine;
    Direction direction;



    public UserRouteItem(String transportForm, RouteLine routeLine, Direction direction, StationStop startingStop, DayTimeConditions dayTimeConditions, int maxNumberToShow) {
        this.transportForm = transportForm;
        this.routeLine = routeLine;
        this.direction = direction;
        this.startingStop = startingStop;
        this.dayTimeConditions = dayTimeConditions;
        this.maxNumberToShow = maxNumberToShow;
    }

    public RouteLine getRouteLine() {
        return routeLine;
    }

    public Direction getDirection() {
        return direction;
    }

    // For UserList View - returns first line
    public String getItemText1() {
        String line1 = "";
        String line2 = "";
        int MAX_NUMBER_CHARACTERS_ON_LINE = 40;


        if (this.transportForm.equals("Bus")) {
            line1 = "Route: " + routeLine + ". From: " + startingStop.toString();
            line2 = "Direction: " + direction;

        } else if (this.transportForm.equals("Tube")) {
            line1 = "Line:" + routeLine + ". From: " + startingStop.toString();
            line2 = "Platforms: " + direction;

        } else {
            throw new IllegalStateException("Unexpected transport form or null");
        }

        if (line1.length() > MAX_NUMBER_CHARACTERS_ON_LINE) {
            line1 = line1.substring(0,MAX_NUMBER_CHARACTERS_ON_LINE) + "...";
        }
        if (line2.length() > MAX_NUMBER_CHARACTERS_ON_LINE) {
            line2 = line2.substring(0,MAX_NUMBER_CHARACTERS_ON_LINE) + "...";
        }
        return line1 +"\n" + line2;

    }

    public String getItemText2() {
        String line3 = "";
        int MAX_NUMBER_CHARACTERS_ON_LINE = 40;

        if (dayTimeConditions == null) {
            line3 = "No conditions set";
        } else {
            line3 = "Conditions: " + dayTimeConditions;
        }

        if (line3.length() > MAX_NUMBER_CHARACTERS_ON_LINE) {
            line3 = line3.substring(0,MAX_NUMBER_CHARACTERS_ON_LINE) + "...";
        }
        return line3;
    }

}
