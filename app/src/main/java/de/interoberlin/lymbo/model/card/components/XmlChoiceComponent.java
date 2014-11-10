package de.interoberlin.lymbo.model.card.components;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class XmlChoiceComponent implements XmlComponent {
    private List<XmlAnswer> answers = new ArrayList<XmlAnswer>();

    // --------------------
    // Constructor
    // --------------------

    public XmlChoiceComponent() {
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public View getView() {
        return null;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public List<XmlAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<XmlAnswer> answers) {
        this.answers = answers;
    }
}
