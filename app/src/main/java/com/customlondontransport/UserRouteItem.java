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
