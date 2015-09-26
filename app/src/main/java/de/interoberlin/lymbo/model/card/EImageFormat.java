package de.interoberlin.lymbo.model.card;

public enum EImageFormat {
    BASE64("base64"), REF("ref");

    private String value;

    // --------------------
    // Constructors
    // --------------------

    EImageFormat (String value) {
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
