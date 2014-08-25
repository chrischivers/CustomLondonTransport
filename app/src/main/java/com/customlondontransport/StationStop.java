package com.customlondontransport;

import android.location.Location;

import java.io.Serializable;

public class StationStop implements Serializable{
    private String id;
    private String label;
    private String busStopLetterCode;
    private float longitudeCoordinate;
    private float latitudeCoordinate;
    private String towards;

    public StationStop(String id, String label, String busStopLetterCode, float longitudeCoordinate, float latitudeCoordinate, String towards) {
        this.id = id;
        this.label = label;
        this.busStopLetterCode = busStopLetterCode;
        this.longitudeCoordinate = longitudeCoordinate;
        this.latitudeCoordinate = latitudeCoordinate;
        this.towards = towards;
    }
    public StationStop(String id, String label, float longitudeCoordinate, float latitudeCoordinate) {
        this.id = id;
        this.label = label;
        this.longitudeCoordinate = longitudeCoordinate;
        this.latitudeCoordinate = latitudeCoordinate;
    }

    public StationStop() {
        this.id = "";
        this.label = "";
    }

    public StationStop(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public StationStop(String id, String label, String busStopLetterCode) {
        this.id = id;
        this.label = label;
        this.busStopLetterCode = busStopLetterCode;
    }

    public String getID() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public float getLongitudeCoordinate() { return this.longitudeCoordinate; }

    public float getLatitudeCoordinate() { return this.latitudeCoordinate; }

    public Location getLocation() {
        Location l = new Location("");
        l.setLongitude(this.longitudeCoordinate);
        l.setLatitude(this.latitudeCoordinate);
        return l;
    }

    public String getTowards() {
        return this.towards;
    }

    @Override
    public String toString() {
        if (busStopLetterCode == null || busStopLetterCode.equals("")) {
            return label;
        } else {
            return label + " (" + busStopLetterCode + ")";
        }
    }

}