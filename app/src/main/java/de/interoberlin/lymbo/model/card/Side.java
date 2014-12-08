package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.Displayable;

public class Side {
    private String color = "#FFFFFF";
    private List<Displayable> components = new ArrayList<Displayable>();

    // -------------------------
    // Constructors
    // -------------------------

    public Side() {
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Displayable> getComponents() {
        return components;
    }

    public void setComponents(List<Displayable> components) {
        this.components = components;
    }

    public void addComponent(Displayable displayable) {
        if (components == null) {
            components = new ArrayList<Displayable>();
        }

        components.add(displayable);
    }
}