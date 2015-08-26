package de.interoberlin.lymbo.util;


public enum EPreference {
    TRANSLATOR_API_SECRET(String.class, "translator_api_secret");

    private Class type;
    private String preferenceName;

    // --------------------
    // Constructors
    // --------------------

    EPreference(Class type, String preferenceName) {
        this.type = type;
        this.preferenceName = preferenceName;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public Class getType() {
        return type;
    }

    public String getPreferenceName() {
        return preferenceName;
    }
}