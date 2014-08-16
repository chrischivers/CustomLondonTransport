package com.customlondontransport;

import java.io.Serializable;

public class RouteLine implements Serializable{
    private String id;
    private String abrvName;
    private String fullName;

    public RouteLine(String id) {
        this.id = id;
    }

    public RouteLine(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }
    public RouteLine(String id, String abrvName, String fullName) {
        this.id = id;
        this.abrvName = abrvName;
        this.fullName = fullName;
    }

    public RouteLine() {
        this.id = "";
        this.abrvName = "";
        this.fullName = "";
    }

    public String getID() {
        return this.id;
    }

    public String getFullName() {
        return this.fullName;
    }

    @Override
    public String toString() {
        if (this.fullName == null || this.fullName.equals("")) {
            return this.id;
        } else {
            return this.fullName;
        }
    }
}