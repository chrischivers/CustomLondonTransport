package com.customlondontransport;

public class ComboItem {
    private String id;
    private String label;

    public ComboItem(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public ComboItem(String id) {
        this.id = id;
        this.label = null;
    }

    public String getID() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        if (label == null) {
            return id;
        } else {
            return label;
        }
    }
}