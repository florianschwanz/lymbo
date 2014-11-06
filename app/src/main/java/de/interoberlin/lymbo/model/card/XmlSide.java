package de.interoberlin.lymbo.model.card;

import java.util.List;

public class XmlSide {
    private List<XmlText> texts;
    private String image;
    private String hint;
    private List<XmlChoice> choices;

    public XmlSide(List<XmlText> texts, String image, String hint, List<XmlChoice> choices) {
        this.texts = texts;
        this.image = image;
        this.hint = hint;
        this.choices = choices;
    }

    public List<XmlText> getTexts() {
        return texts;
    }

    public void setText(List<XmlText> texts) {
        this.texts = texts;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public List<XmlChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<XmlChoice> choices) {
        this.choices = choices;
    }
}