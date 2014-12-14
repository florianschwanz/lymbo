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

        TextView tvTitle = (TextView) llTitleComponent.findViewById(R.id.tvTitle);
        tvTitle.setText(value);

        tvTitle.setLines(lines);
        if (gravity.equalsIgnoreCase("left"))
            tvTitle.setGravity(Gravity.LEFT);
        if (gravity.equalsIgnoreCase("center"))
            tvTitle.setGravity(Gravity.CENTER);
        if (gravity.equalsIgnoreCase("right"))
            tvTitle.setGravity(Gravity.RIGHT);

        return llTitleComponent;
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
