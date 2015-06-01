package de.interoberlin.lymbo.model.card;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.view.controls.RobotoSlabTextView;

public class Tag implements Displayable {
    private boolean checked = true;
    private String name = "";

    // --------------------
    // Constructor
    // --------------------

    public Tag(String name) {
        this.name = name;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(Context c, Activity a, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        LinearLayout llTag = (LinearLayout) li.inflate(R.layout.component_tag, parent, false);

        RobotoSlabTextView rstvText = (RobotoSlabTextView) llTag.findViewById(R.id.rstvText);

        // Attribute : value
        rstvText.setText(name);

        return llTag;
    }

    @Override
    public View getEditableView(Context c, Activity a, ViewGroup parent) {
        return new View(c);
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
