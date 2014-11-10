package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.card.components.XmlComponent;

public class XmlSide {
    private List<XmlComponent> components = new ArrayList<XmlComponent>();

    // -------------------------
    // Constructors
    // -------------------------

    public XmlSide() {
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public List<XmlComponent> getComponents() {
        return components;
    }

    public void setComponents(List<XmlComponent> components) {
        this.components = components;
    }
}