package com.customlondontransport;

import java.io.Serializable;
import java.util.List;

public class UserStationItem extends UserItem implements Serializable {

    List<?> routeLineList;



    public UserStationItem(String transportForm, StationStop startingStop, List<?> routeLineList, DayTimeConditions dayTimeConditions, int maxNumberToShow) {
        this.transportForm = transportForm;
        this.routeLineList = routeLineList;
        this.startingStop = startingStop;
        this.dayTimeConditions = dayTimeConditions;
        this.maxNumberToShow = maxNumberToShow;
    }

    public List<?> getRouteLineList() {
        return  routeLineList;
    }

    // For UserList View - returns first line
    public String getLine1() {

        String conditions;
        if (dayTimeConditions == null) {
            conditions = "\nNo conditions set";
        } else {
            conditions = "\nConditions: " + dayTimeConditions;
        }

        return "Stop: " + startingStop + "\nBus Routes: " + routeLineList + ". " + conditions;
    }

}
