package de.interoberlin.lymbo.model.persistence;

import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.card.Side;
import de.interoberlin.lymbo.model.card.components.TextComponent;
import de.interoberlin.lymbo.model.card.components.TitleComponent;

public class LymboMockup {

    // --------------------
    // Methods
    // --------------------

    public static Lymbo getLymbo(int cards) {
        Lymbo lymbo = new Lymbo();

        lymbo.setHint("hint");
        lymbo.setImage("");
        lymbo.setTitle("title");
        lymbo.setAuthor("author");
        lymbo.setSubtitle("subtitle");

        for (int i = 0; i < cards; i++) {
            lymbo.addCard(getCard(i + 1));
        }

        return lymbo;
    }

    public static Card getCard(int i) {
        Card card = new Card();

        card.setFront(getFront(i));
        card.setBack(getBack(i));

        return card;
    }

    public static Side getFront(int i) {
        Side front = new Side();

        front.addComponent(new TitleComponent("Card " + i));
        front.addComponent(new TextComponent("Lorem ipsum"));

        return front;
    }

    public static Side getBack(int i) {
        Side back = new Side();

        back.addComponent(new TitleComponent("Card " + i));
        back.addComponent(new TextComponent("Lorem ipsum"));

        return back;
    }
}
