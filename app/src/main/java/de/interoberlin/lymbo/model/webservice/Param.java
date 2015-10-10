package de.interoberlin.lymbo.model.webservice;

public class Param {
    private String key;
    private String value;

    // --------------------
    // Constructors
    // --------------------

    public Param(String key, String value) {
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
