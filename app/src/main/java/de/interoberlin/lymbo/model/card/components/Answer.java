package de.interoberlin.lymbo.model.card.components;

import java.util.HashMap;
import java.util.Map;

public class Answer {
    private String value = "";
    private Map<String, String> translations = new HashMap();
    private boolean correct = false;
    private boolean selected = false;

    // --------------------
    // Constructors
    // --------------------

    public Answer() {
    }

    // --------------------
    // Methods
    // --------------------

    // --------------------
    // Getters / Setters
    // --------------------

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
