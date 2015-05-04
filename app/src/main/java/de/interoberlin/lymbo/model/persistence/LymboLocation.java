package de.interoberlin.lymbo.model.persistence;


import java.util.Date;

public class LymboLocation {
    private long id;
    private String location;
    private int stashed;
    private String date;

    // --------------------
    // Constructors
    // --------------------

    public LymboLocation() {

    }

    public LymboLocation(String location, int stashed) {
        this.location = location;
        this.stashed = stashed;
        this.date = new Date().toString();
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStashed() {
        return stashed;
    }

    public void setStashed(int stashed) {
        this.stashed = stashed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
