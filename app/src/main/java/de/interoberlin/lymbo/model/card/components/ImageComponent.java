package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import de.interoberlin.lymbo.model.Displayable;

public class ImageComponent implements Displayable {
    private String image;

    private boolean flip = false;

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

    @Override
    public View getEditableView(Context c, final Activity a, ViewGroup parent) {
        return new View(c);
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

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }
}