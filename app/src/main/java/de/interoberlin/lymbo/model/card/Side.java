package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.interoberlin.lymbo.model.card.components.ChoiceComponent;
import de.interoberlin.lymbo.model.card.components.ImageComponent;
import de.interoberlin.lymbo.model.card.components.ResultComponent;
import de.interoberlin.lymbo.model.card.components.SVGComponent;
import de.interoberlin.lymbo.model.card.components.TextComponent;
import de.interoberlin.lymbo.model.card.components.TitleComponent;
import de.interoberlin.lymbo.model.card.enums.EComponent;
import de.interoberlin.lymbo.util.XmlUtil;

public class Side {
    public static final String TAG = "side";

    private String color = "#FFFFFF";
    private List<Displayable> components = new ArrayList<>();

    private boolean flip = false;

    // -------------------------
    // Constructors
    // -------------------------

    public Side() {
    }

    public Side(Displayable... components) {
        for (Displayable c : components) {
            this.components.add(c);
        }
    }

    // -------------------------
    // Methods
    // -------------------------

    public boolean contains(EComponent component) {
        for (Displayable d : getComponents()) {
            if ((component == EComponent.TITLE && d instanceof TitleComponent) ||
                    (component == EComponent.TEXT && d instanceof TextComponent) ||
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
                    (component == EComponent.IMAGE && d instanceof ImageComponent) ||
                    (component == EComponent.CHOICE && d instanceof ChoiceComponent) ||
                    (component == EComponent.RESULT && d instanceof ResultComponent) ||
                    (component == EComponent.SVG && d instanceof SVGComponent)) {
                return d;
            }
        }

        return null;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        // Attributes
        Map<String, String> attributes = new HashMap<>();
        attributes.put("color", color);
        result.append(XmlUtil.addStartTag(TAG, attributes));

        // Sub elements
        for (Displayable component : components) {
            if (component instanceof TitleComponent) {
                result.append(component.toString());
            } else if (component instanceof TextComponent) {
                result.append(component.toString());
            } else if (component instanceof ImageComponent) {
                result.append(component.toString());
            } else if (component instanceof SVGComponent) {
                result.append(component.toString());
            }
        }

        result.append(XmlUtil.addEndTag(TAG));

        return result.toString();
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
            components = new ArrayList<>();
        }

        components.add(displayable);
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }
}