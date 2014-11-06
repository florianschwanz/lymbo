package de.interoberlin.lymbo.model.card;

public class XmlText {

    private XmlTextType type;
    private String text;

    public XmlText(XmlTextType type, String text) {
        this.setType(type);
        this.setText(text);
    }

    public XmlTextType getType() {
        return type;
    }

    public void setType(XmlTextType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
