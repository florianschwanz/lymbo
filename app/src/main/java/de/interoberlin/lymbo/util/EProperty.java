package de.interoberlin.lymbo.util;


public enum EProperty {
    INTEROBERLIN_LOG_PATH("interoberlin_log_path"),
    LYMBO_FILE_EXTENSION("lymbo_file_extension"),
    LYMBO_LOOKUP_PATH("lymbo_lookup_path"),
    LYMBO_SAVE_PATH("lymbo_save_path"),
    REFRESH_DELAY_LYMBOS("refresh_delay_lymbos"),
    REFRESH_DELAY_CARDS("refresh_delay_cards"),
    VIBRATION_DURATION("vibration_duration"),
    DEBUG_MODE("debug_mode"),
    ;

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
