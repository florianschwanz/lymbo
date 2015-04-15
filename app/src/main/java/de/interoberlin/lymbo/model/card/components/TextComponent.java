package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.enums.EGravity;
import de.interoberlin.lymbo.model.card.enums.EStyle;

public class TextComponent implements Displayable {
    private String value = "";
    private int lines = 0;
    private EGravity gravity = EGravity.LEFT;
    private EStyle style = EStyle.NORMAL;

    private boolean flip = false;

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
        LinearLayout llTextComponent = (LinearLayout) li.inflate(R.layout.component_text, parent);

        TextView tvText = (TextView) llTextComponent.findViewById(R.id.tvText);

        // Attribute : value
        tvText.setText(value);

        // Attribute : lines
        if (lines != 0)
            tvText.setLines(lines);

        // Attribute : gravity
        if (gravity == EGravity.LEFT)
            tvText.setGravity(Gravity.START);
        else if (gravity == EGravity.CENTER)
            tvText.setGravity(Gravity.CENTER);
        else if (gravity == EGravity.RIGHT)
            tvText.setGravity(Gravity.END);

        // Attribute : style
        if (style == EStyle.CODE)
            tvText.setTypeface(Typeface.MONOSPACE);

        return llTextComponent;
    }

    @Override
    public View getEditableView(Context c, Activity a, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        LinearLayout llTextComponent = (LinearLayout) li.inflate(R.layout.component_text_edit, parent);

        EditText etTitle = (EditText) llTextComponent.findViewById(R.id.etText);
        etTitle.setText(value);

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

    public EGravity getGravity() {
        return gravity;
    }

    public void setGravity(EGravity gravity) {
        this.gravity = gravity;
    }

    public EStyle getStyle() {
        return style;
    }

    public void setStyle(EStyle style) {
        this.style = style;
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }
}
