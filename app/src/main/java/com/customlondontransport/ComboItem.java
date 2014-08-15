package com.customlondontransport;

import java.io.Serializable;

public class ComboItem implements Serializable{
    private String id;
    private String label;
    private float longitudeCoordinate;
    private float latitudeCoordinate;

    public ComboItem(String id, String label, float longitudeCoordinate, float latitudeCoordinate) {
        this.id = id;
        this.label = label;
        this.longitudeCoordinate = longitudeCoordinate;
        this.latitudeCoordinate = latitudeCoordinate;
    }
    public ComboItem(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public ComboItem(String label) {
        this.id = label;
        this.label = label;
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
        if (label == null) {
            return id;
        } else {
            return label;
        }
    }
}