package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import de.interoberlin.lymbo.model.Displayable;

public class HintComponent implements Displayable {
    private String value = "";

    // --------------------
    // Constructor
    // --------------------

    public HintComponent() {
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}