package de.interoberlin.lymbo.model.persistence.sqlite;

public class Column {
    private String name;
    private Type type;

    // --------------------
    // Constructors
    // --------------------

    public Column(String name, Type type) {
        this.name = name;
        this.type = type;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
