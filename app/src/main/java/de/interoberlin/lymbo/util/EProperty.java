package de.interoberlin.lymbo.util;


public enum EProperty {
    LYMBO_FILE_EXTENSION("lymbo_file_extension"),
    LYMBO_LOOKUP_PATH("lymbo_lookup_path"),
    REFRESH_DELAY("refresh_delay");

    private String propertyName;

    // --------------------
    // Constructors
    // --------------------

    EProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getPropertyName() {
        return propertyName;
    }
}
