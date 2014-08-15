package com.customlondontransport;

import java.io.Serializable;

public class Direction implements Serializable{
    private String id;
    private String label;

    public Direction(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public Direction(String label) {
        this.id = label;
        this.label = label;
    }

    public Direction() {
       this.id = "";
        this.label = "";
    }

    public String getID() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }


    @Override
    public String toString() {

        return label;
    }
}