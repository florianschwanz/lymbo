package de.interoberlin.lymbo.model.card;

public class Tag {
    private boolean checked = true;
    private String name = "";

    // --------------------
    // Constructor
    // --------------------

    public Tag(String name) {
        this.name = name;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
