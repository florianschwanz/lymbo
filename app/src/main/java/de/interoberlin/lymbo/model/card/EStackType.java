package de.interoberlin.lymbo.model.card;


public enum EStackType {
    FREESTYLE("freestyle"), LANGUAGE("language");

    private final String type;

    // --------------------
    // Constructors
    // --------------------

    EStackType(final String type) {
        this.type = type;
    }

    // --------------------
    // Methods
    // --------------------

    public static EStackType fromString(final String type) {
        for (EStackType s : values()) {
            if (s.getType().equals(type)) {
                return s;
            }
        }
        return null;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getType() {
        return type;
    }
}