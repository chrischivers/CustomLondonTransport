package com.customlondontransport;

import java.io.Serializable;

public class StationStop implements Serializable{
    private String id;
    private String label;
    private float longitudeCoordinate;
    private float latitudeCoordinate;

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

    public String getID() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public float getLongitudeCoordinate() { return this.longitudeCoordinate; }

    public float getLatitudeCoordinate() { return this.latitudeCoordinate; }

    @Override
    public String toString() {
            return label;
    }
}