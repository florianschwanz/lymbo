package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.components.ChoiceComponent;
import de.interoberlin.lymbo.model.card.components.ResultComponent;

public class Side {
    private String color = "#FFFFFF";
    private List<Displayable> components = new ArrayList<Displayable>();

    // -------------------------
    // Constructors
    // -------------------------

    public Side() {
    }

    // -------------------------
    // Methods
    // -------------------------

    public boolean containsResult() {
        for (Displayable d : getComponents()) {
            if (d instanceof ResultComponent) {
                return true;
            }
        }

        return false;
    }

    public boolean containsChoice() {
        for (Displayable d : getComponents()) {
            if (d instanceof ChoiceComponent) {
                return true;
            }
        }

        return false;
    }

    public ResultComponent getFirstResultComponent() {
        for (Displayable d : getComponents()) {
            if (d instanceof ResultComponent) {
                return (ResultComponent) d;
            }
        }

        return null;
    }

    public ChoiceComponent getFirstChoiceComponent() {
        for (Displayable d : getComponents()) {
            if (d instanceof ChoiceComponent) {
                return (ChoiceComponent) d;
            }
        }

        return null;
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