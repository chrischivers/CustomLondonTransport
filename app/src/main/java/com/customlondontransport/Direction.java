package com.customlondontransport;

import java.io.Serializable;

public class Direction implements Serializable{
    private int id;
    private String label;
    private String line;

    public Direction(int id, String label, String line) {
        this.id = id;
        this.label = label;
        this.line = line;
    }

    public Direction(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public Direction(String label) {
        this.label = label;
    }

    public Direction() {
        this.label = "";
    }

    public int getID() {
        return this.id;
    }
    public String getLabel() {
        return this.label;
    }
    public String getLine() {
        return this.line;
    }


    @Override
    public String toString() {

        return label;
    }
}