package de.interoberlin.lymbo.model.persistence.sqlite;

public enum Type {
    TEXT("text"),
    TEXT_PRIMARY_KEY("text primary key"),
    INTEGER("integer"),
    INTEGER_PRIMARY_KEY("integer primary key"),;
    private String name;

    // --------------------
    // Constructors
    // --------------------

    Type(String name) {
        this.name = name;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
