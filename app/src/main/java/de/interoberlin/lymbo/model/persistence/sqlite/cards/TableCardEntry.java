package de.interoberlin.lymbo.model.persistence.sqlite.cards;


public class TableCardEntry {
    private String uuid;
    private String note;
    private int state;

    // --------------------
    // Constructors
    // --------------------

    public TableCardEntry() {

    }

    public TableCardEntry(String uuid, String note, int state) {
        this.uuid = uuid;
        this.note = note;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isNormal() {
        return getState() == TableCardDatasource.STATE_NORMAL;
    }

    public boolean isDismissed() {
        return getState() == TableCardDatasource.STATE_DISMISSED;
    }

    public boolean isStashed() {
        return getState() == TableCardDatasource.STATE_STASHED;
    }
}
