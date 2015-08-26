package de.interoberlin.lymbo.model.persistence.sqlite.settings;


public class TableSettingsEntry {
    private String key;
    private String value;

    // --------------------
    // Constructors
    // --------------------

    public TableSettingsEntry() {

    }

    public TableSettingsEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
