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

public class TextComponent implements Displayable {
    private String value = "";
    private int lines;
    private String gravity;

    // --------------------
    // Constructors
    // --------------------

    public TextComponent() {
    }

    public TextComponent(String value) {
        this.value = value;
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(Context c, Activity a, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        LinearLayout llTextComponent = (LinearLayout) li.inflate(R.layout.component_text, null);

        TextView tvText = (TextView) llTextComponent.findViewById(R.id.tvText);
        tvText.setText(value);

        tvText.setLines(lines);
        if (gravity.equalsIgnoreCase("left"))
            tvText.setGravity(Gravity.LEFT);
        if (gravity.equalsIgnoreCase("center"))
            tvText.setGravity(Gravity.CENTER);
        if (gravity.equalsIgnoreCase("right"))
            tvText.setGravity(Gravity.RIGHT);

        return llTextComponent;
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
