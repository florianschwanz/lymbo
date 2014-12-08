package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;

public class TextComponent implements Displayable {
    private String value = "";
    private XmlTextType type;

    // --------------------
    // Constructor
    // --------------------

    public TextComponent() {
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

    public XmlTextType getType() {
        return type;
    }

    public void setType(XmlTextType type) {
        this.type = type;
    }
}
