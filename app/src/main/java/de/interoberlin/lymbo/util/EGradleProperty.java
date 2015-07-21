package de.interoberlin.lymbo.util;


public enum EGradleProperty {
    GROUP_ID("groupId"),
    ARTIFACT_ID("artifactId"),
    VERSION_CODE("versionCode"),
    VERSION_MAJOR("versionMajor"),
    VERSION_MINOR("versionMinor"),
    VERSION_PATCH("versionPatch"),
    LICENSE("license"),
    WEBSITE("website"),
    PROJECT_URL("projectUrl"),
    SOURCECODE("sourcecode"),
    ISSUETRACKER("issuetracker"),
    SUMMARY("summary"),
    ;

    private String propertyName;

    // --------------------
    // Constructors
    // --------------------

    EGradleProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getPropertyName() {
        return propertyName;
    }
}
