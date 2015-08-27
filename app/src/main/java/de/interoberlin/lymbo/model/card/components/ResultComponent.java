package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.view.controls.RobotoTextView;

public class ResultComponent implements Displayable {
    private String value = "";
    private String gravity = "left";
    private int color = 0;

    // --------------------
    // Constructors
    // --------------------

    public ResultComponent() {
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(Context c, Activity a, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        LinearLayout llTitleComponent = (LinearLayout) li.inflate(R.layout.component_result, parent, false);

        RobotoTextView tvTitle = (RobotoTextView) llTitleComponent.findViewById(R.id.tvTitle);
        tvTitle.setText(value);

        if (color != 0)
            tvTitle.setTextColor(color);

        return llTitleComponent;
    }

    @Override
    public View getEditableView(Context c, Activity a, ViewGroup parent) {
        return new View(c);
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

    public String getGravity() {
        return gravity;
    }

    public void setGravity(String gravity) {
        this.gravity = gravity;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
