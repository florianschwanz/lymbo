package de.interoberlin.lymbo.model.persistence.sqlite.stack;


public class TableStackEntry {
    private String uuid;
    private String path;
    private int state;

    // --------------------
    // Constructors
    // --------------------

    public TableStackEntry() {
    }

    public TableStackEntry(String uuid, String path, int state) {
        this.uuid = uuid;
        this.path = path;
        this.state = state;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isNormal() {
        return getState() == TableStackDatasource.STATE_NORMAL;
    }

    public boolean isStashed() {
        return getState() == TableStackDatasource.STATE_STASHED;
    }
}