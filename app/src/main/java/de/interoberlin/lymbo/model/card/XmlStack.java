package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.List;

public class XmlStack {
    private List<XmlCard> cards;

    public XmlStack(List<XmlCard> cards) {
        this.cards = cards;
    }

    public List<XmlCard> getCards() {
        return cards;
    }

    public void setCards(List<XmlCard> cards) {
        this.cards = cards;
    }

    public void addCard(int pos, XmlCard xmlCard) {
        this.cards.add(pos, xmlCard);
    }

    /**
     * Returns a card specified by id
     *
     * @param id
     * @return
     */
    public XmlCard getCardById(int id) {
        for (XmlCard c : cards) {
            if (c.getId() == id) {
                return c;
            }
        }

        return null;
    }

    /**
     * Removes all cards with a specific id from stack
     *
     * @param id
     */
    public void removeCardById(int id) {
        cards.remove(id);
    }

    /**
     * Adds an empty card at the end of the stack
     */
    public void addEmptyCard() {
        List<XmlText> texts = new ArrayList<XmlText>();
        texts.add(new XmlText(XmlTextType.NORMAL, ""));

        List<XmlChoice> choices = new ArrayList<XmlChoice>();
        choices.add(new XmlChoice("", false));

        addCard(getCards().size(), new XmlCard(getCards().size(), "", "#FFFFFF", new XmlSide(texts, "", "", choices),
                new XmlSide(texts, "", "", choices)));
    }
}
