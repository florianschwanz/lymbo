package de.interoberlin.lymbo.model.persistence.sqlite.location;


public class LymboLocation {
    private String path;
    private int stashed;

    // --------------------
    // Constructors
    // --------------------

    public LymboLocation() {

    }

    public LymboLocation(String path, int stashed) {
        this.path = path;
        this.stashed = stashed;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStashed() {
        return stashed;
    }

    public void setStashed(int stashed) {
        this.stashed = stashed;
    }
}
