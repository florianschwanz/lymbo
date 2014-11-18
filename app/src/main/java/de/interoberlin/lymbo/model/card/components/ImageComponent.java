package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import de.interoberlin.lymbo.model.Displayable;

public class ImageComponent implements Displayable {
    private String image;

    // --------------------
    // Constructor
    // --------------------

    public ImageComponent() {
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(Context c, Activity a, ViewGroup parent) {
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