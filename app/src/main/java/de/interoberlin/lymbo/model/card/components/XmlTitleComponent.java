package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import de.interoberlin.lymbo.model.Displayable;

public class XmlTitleComponent implements Displayable {
    private String value = "";
    private XmlTextType type;

    // --------------------
    // Constructor
    // --------------------

    public XmlTitleComponent() {

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

    public XmlTextType getType() {
        return type;
    }

    public void setType(XmlTextType type) {
        this.type = type;
    }

}
