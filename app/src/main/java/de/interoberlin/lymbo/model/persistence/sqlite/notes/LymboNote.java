package de.interoberlin.lymbo.model.persistence.sqlite.notes;


public class LymboNote {
    private long id;
    private String uuid;
    private String text;

    // --------------------
    // Constructors
    // --------------------

    public LymboNote() {

    }

    public LymboNote(String uuid, String text) {
        this.uuid = uuid;
        this.text = text;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
