package de.interoberlin.lymbo.model.card.components;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.Displayable;

public class ChoiceComponent implements Displayable {
    private List<Answer> answers = new ArrayList<Answer>();

    // --------------------
    // Constructor
    // --------------------

    public ChoiceComponent() {
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

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
