package com.customlondontransport;

import java.io.Serializable;

public class UserItem implements Serializable {
    String transportForm;
    StationStop startingStop;
    DayTimeConditions dayTimeConditions;
    int maxNumberToShow;

    public String getTransportForm() {
        return transportForm;
    }

    public DayTimeConditions getDayTimeConditions() {
        return dayTimeConditions;
    }

    public int getMaxNumberToShow() {
        return maxNumberToShow;
    }

    public StationStop getStartingStop() {
        return startingStop;
    }
}
