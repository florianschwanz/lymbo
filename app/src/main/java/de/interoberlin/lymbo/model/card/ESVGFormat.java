package de.interoberlin.lymbo.model.card;

public enum ESVGFormat {
    PLAIN("plain"), REF("ref");

    private String value;

    // --------------------
    // Constructors
    // --------------------

    ESVGFormat(String value) {
        this.value = value;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
