package com.customlondontransport;

import java.io.Serializable;
import java.util.List;

public class UserStationItem extends UserItem implements Serializable {

    List<?> routeLineList; // for Tube this is a List of Directions, for Bus this is a list of RouteLines

    public UserStationItem(String transportForm, StationStop startingStop, List<?> routeLineList, DayTimeConditions dayTimeConditions, int maxNumberToShow) {
        this.transportForm = transportForm;
        this.routeLineList = routeLineList;
        this.startingStop = startingStop;
        this.dayTimeConditions = dayTimeConditions;
        this.maxNumberToShow = maxNumberToShow;
    }

    public List<?> getRouteLineList() {
        return routeLineList;
    }

    // For UserList View - returns first line
    public String getItemText1() {
        String line1 = "";
        String line2 = "";
        int MAX_NUMBER_CHARACTERS_ON_LINE1 = 42;
        int MAX_NUMBER_CHARACTERS_ON_LINE2 = 48;

        // Format towards string
        String towards = "";
        if (this.startingStop.getTowards() != null) {
            if (!startingStop.getTowards().equals("")) {
                towards = " towards " + startingStop.getTowards() + ".";
            }
        }
        // Format RouteLineList (Remove brackets at the beginning and end of the List.toString())
        String routeLineListFormatted = routeLineList.toString().substring(1, routeLineList.toString().length() - 1);


        if (this.transportForm.equals("Bus")) {
            line1 = startingStop.toString() + towards;
            line2 = "Route(s): " + routeLineListFormatted;

        } else if (this.transportForm.equals("Tube")) {
            line1 = startingStop.toString();
            line2 = "Platforms: " + routeLineListFormatted;

        } else {
            throw new IllegalStateException("Unexpected transport form or null");
        }

        if (line1.length() > MAX_NUMBER_CHARACTERS_ON_LINE1) {
            line1 = line1.substring(0, MAX_NUMBER_CHARACTERS_ON_LINE1) + "...";
        }
        if (line2.length() > MAX_NUMBER_CHARACTERS_ON_LINE2) {
            line2 = line2.substring(0, MAX_NUMBER_CHARACTERS_ON_LINE2) + "...";
        }

        return line1 + "\n" + line2;

    }

    public String getItemText2() {
        String line3 = "";
        int MAX_NUMBER_CHARACTERS_ON_LINE3 = 50;

        if (dayTimeConditions == null) {
            line3 = "No conditions set";
        } else {
            line3 = "Conditions: " + dayTimeConditions;
        }

        if (line3.length() > MAX_NUMBER_CHARACTERS_ON_LINE3) {
            line3 = line3.substring(0, MAX_NUMBER_CHARACTERS_ON_LINE3) + "...";
        }
        return line3;
    }
}
