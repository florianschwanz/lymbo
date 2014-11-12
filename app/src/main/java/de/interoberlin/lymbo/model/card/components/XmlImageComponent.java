package de.interoberlin.lymbo.model.card.components;

import android.view.View;

public class XmlImageComponent implements de.interoberlin.lymbo.model.Displayable {
    private String image;

    // --------------------
    // Constructor
    // --------------------

    public XmlImageComponent() {
    }

    public XmlImageComponent(String image) {
        this.setImage(image);
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}