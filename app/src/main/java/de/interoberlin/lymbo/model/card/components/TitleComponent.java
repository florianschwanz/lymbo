package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.view.controls.RobotoTextView;

public class TitleComponent implements Displayable {
    private String value = "";
    private int lines = 1;
    private String gravity = "left";

    // --------------------
    // Constructors
    // --------------------

    public TitleComponent() {
    }

    public TitleComponent(String value) {
        this.value = value;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(Context c, Activity a, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        LinearLayout llTitleComponent = (LinearLayout) li.inflate(R.layout.component_title, null);

        RobotoTextView rtvTitle = (RobotoTextView) llTitleComponent.findViewById(R.id.tvTitle);
        rtvTitle.setText(value);

        rtvTitle.setLines(lines);
        if (gravity.equalsIgnoreCase("left"))
            rtvTitle.setGravity(Gravity.LEFT);
        if (gravity.equalsIgnoreCase("center"))
            rtvTitle.setGravity(Gravity.CENTER);
        if (gravity.equalsIgnoreCase("right"))
            rtvTitle.setGravity(Gravity.RIGHT);

        return llTitleComponent;
    }

    @Override
    public View getEditableView(Context c, Activity a, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        LinearLayout llTitleComponent = (LinearLayout) li.inflate(R.layout.component_title_edit, null);

        TextView etTitle = (TextView) llTitleComponent.findViewById(R.id.etTitle);
        etTitle.setText(value);

        etTitle.setLines(lines);
        if (gravity.equalsIgnoreCase("left"))
            etTitle.setGravity(Gravity.LEFT);
        if (gravity.equalsIgnoreCase("center"))
            etTitle.setGravity(Gravity.CENTER);
        if (gravity.equalsIgnoreCase("right"))
            etTitle.setGravity(Gravity.RIGHT);

        return etTitle;
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

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public String getGravity() {
        return gravity;
    }

    public void setGravity(String gravity) {
        this.gravity = gravity;
    }
}
