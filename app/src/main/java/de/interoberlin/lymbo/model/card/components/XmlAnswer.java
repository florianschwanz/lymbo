package de.interoberlin.lymbo.model.card.components;

public class XmlAnswer {
    private String text = "";
    private boolean correct = false;

    // --------------------
    // Constructors
    // --------------------

    public XmlAnswer () {

    }

    // --------------------
    // Methods
    // --------------------

    // --------------------
    // Getters / Setters
    // --------------------

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
