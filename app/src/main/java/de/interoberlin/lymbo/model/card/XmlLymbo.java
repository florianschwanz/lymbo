package de.interoberlin.lymbo.model.card;

import java.util.List;

public class XmlLymbo {
    private String title = "";
    private String description = "";
    private String image = "";
    private String author = "";
    private List<XmlCard> cards;

    // -------------------------
    // Constructors
    // -------------------------

    public XmlLymbo() {
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<XmlCard> getCards() {
        return cards;
    }

    public void setCards(List<XmlCard> cards) {
        this.cards = cards;
    }
}
