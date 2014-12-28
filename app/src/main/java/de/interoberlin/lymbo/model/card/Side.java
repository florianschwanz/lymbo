package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.components.ChoiceComponent;
import de.interoberlin.lymbo.model.card.enums.EComponent;
import de.interoberlin.lymbo.model.card.components.HintComponent;
import de.interoberlin.lymbo.model.card.components.ImageComponent;
import de.interoberlin.lymbo.model.card.components.ResultComponent;
import de.interoberlin.lymbo.model.card.components.SVGComponent;
import de.interoberlin.lymbo.model.card.components.TextComponent;
import de.interoberlin.lymbo.model.card.components.TitleComponent;

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

    // TITLE,TEXT,HINT,IMAGE,CHOICE,RESULT,SVG;


    public boolean contains(EComponent component) {
        for (Displayable d : getComponents()) {
            if ((component == EComponent.TITLE && d instanceof TitleComponent) ||
                    (component == EComponent.TEXT && d instanceof TextComponent) ||
                    (component == EComponent.HINT && d instanceof HintComponent) ||
                    (component == EComponent.IMAGE && d instanceof ImageComponent) ||
                    (component == EComponent.CHOICE && d instanceof ChoiceComponent) ||
                    (component == EComponent.RESULT && d instanceof ResultComponent) ||
                    (component == EComponent.SVG && d instanceof SVGComponent)) {
                return true;
            }
        }

        return false;
    }

    public Displayable getFirst(EComponent component) {
        for (Displayable d : getComponents()) {
            if ((component == EComponent.TITLE && d instanceof TitleComponent) ||
                    (component == EComponent.TEXT && d instanceof TextComponent) ||
                    (component == EComponent.HINT && d instanceof HintComponent) ||
                    (component == EComponent.IMAGE && d instanceof ImageComponent) ||
                    (component == EComponent.CHOICE && d instanceof ChoiceComponent) ||
                    (component == EComponent.RESULT && d instanceof ResultComponent) ||
                    (component == EComponent.SVG && d instanceof SVGComponent)) {
                return d;
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