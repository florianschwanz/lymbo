package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.enums.EGravity;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.view.controls.RobotoTextView;

public class TitleComponent implements Displayable {
    private String value = "";
    private Map<String, String> translations = new HashMap();
    private int lines = 0;
    private EGravity gravity = EGravity.LEFT;

    private boolean flip = false;

    // --------------------
    // Constructors
    // --------------------

    public TitleComponent() {
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView(Context c, Activity a, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        LinearLayout llTitleComponent = (LinearLayout) li.inflate(R.layout.component_title, parent, false);

        RobotoTextView tvTitle = (RobotoTextView) llTitleComponent.findViewById(R.id.tvTitle);

        // Attribute : value
        if (translations.containsKey(Configuration.getLanguage(c)))
            tvTitle.setText(translations.get(Configuration.getLanguage(c)));
        else
            tvTitle.setText(value);


        // Attribute : lines
        if (lines != 0)
            tvTitle.setLines(lines);

        // Attribute : gravity
        if (gravity == EGravity.LEFT)
            tvTitle.setGravity(Gravity.START);
        else if (gravity == EGravity.CENTER)
            tvTitle.setGravity(Gravity.CENTER);
        else if (gravity == EGravity.RIGHT)
            tvTitle.setGravity(Gravity.END);

        return llTitleComponent;
    }

    @Override
    public View getEditableView(Context c, Activity a, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        LinearLayout llTitleComponent = (LinearLayout) li.inflate(R.layout.component_title_edit, parent, false);

        TextView etTitle = (TextView) llTitleComponent.findViewById(R.id.etTitle);

        etTitle.setText(value);

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

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
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

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }
}
