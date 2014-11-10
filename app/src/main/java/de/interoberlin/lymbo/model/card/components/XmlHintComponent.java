package de.interoberlin.lymbo.model.card.components;

import android.view.View;

public class XmlHintComponent implements XmlComponent {
    private String text;

    // --------------------
    // Constructor
    // --------------------

    public XmlHintComponent() {

    }

    public XmlHintComponent( String text) {
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}