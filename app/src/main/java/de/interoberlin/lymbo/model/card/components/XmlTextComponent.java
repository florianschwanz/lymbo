package de.interoberlin.lymbo.model.card.components;

import android.view.View;

public class XmlTextComponent implements XmlComponent {
    private XmlTextType type;
    private String text;

    // --------------------
    // Constructor
    // --------------------

    public XmlTextComponent() {

    }

    public XmlTextComponent(XmlTextType type, String text) {
        this.setType(type);
        this.setText(text);
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView() {
        return null;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public XmlTextType getType() {
        return type;
    }

    public void setType(XmlTextType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
