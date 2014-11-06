package de.interoberlin.lymbo.model.card;

public class XmlChoice {
    private String text;
    private boolean right;

    public XmlChoice(String text, boolean right) {
        this.text = text;
        this.right = right;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }
}
