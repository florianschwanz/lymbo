package de.interoberlin.lymbo.model;

public class XmlLymbo {
    private String text;
    private String image;
    private XmlStack stack;

    public XmlLymbo(String text, String image, XmlStack stack) {
        this.text = text;
        this.image = image;
        this.stack = stack;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public XmlStack getStack() {
        return stack;
    }

    public void setStack(XmlStack stack) {
        this.stack = stack;
    }
}
