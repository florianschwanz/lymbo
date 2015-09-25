package de.interoberlin.lymbo.model.persistence.sqlite.stack;


public class TableStackEntry {
    private String uuid;
    private String file;
    private String path;
    private int state;
    private int format;

    // --------------------
    // Constructors
    // --------------------

    public TableStackEntry() {
    }

    public TableStackEntry(String uuid, String file, String path, int state, int format) {
        this.uuid = uuid;
        this.file = file;
        this.path = path;
        this.state = state;
        this.format = format;
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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public boolean isCompressed() {
        return getFormat() == TableStackDatasource.FORMAT_LYMBOX;
    }
}
