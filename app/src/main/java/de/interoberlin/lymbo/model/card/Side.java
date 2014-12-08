package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.Displayable;

public class Side {
    private List<Displayable> components = new ArrayList<Displayable>();

    // -------------------------
    // Constructors
    // -------------------------

    public Side() {
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public List<Displayable> getComponents() {
        return components;
    }

    public void setComponents(List<Displayable> components) {
        this.components = components;
    }
}