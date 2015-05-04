package de.interoberlin.lymbo.util;

/**
 * This class provides access to properties stored in assets/lymbo.properties
 */
public enum LymboProperties {
    LYMBO_FILE_EXTENSION("lymbo_file_extension"),
    LYMBO_LOOKUP_PATH("lymbo_lookup_path");

    private String name;

    LymboProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
