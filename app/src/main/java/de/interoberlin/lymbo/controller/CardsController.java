package de.interoberlin.lymbo.controller;


import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.card.XmlCard;
import de.interoberlin.lymbo.model.card.XmlLymbo;

public class CardsController {
    private static XmlLymbo lymbo;
    private static List<XmlCard> cards = new ArrayList<XmlCard>();

    private static CardsController instance;

    // --------------------
    // Constructors
    // --------------------

    private CardsController() {
        init();
    }

    public static CardsController getInstance() {
        if (instance == null) {
            instance = new CardsController();
        }

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

    public void init() {
        getCardsFromLymbo();
    }

    public void getCardsFromLymbo() {
        if (lymbo != null) {
            cards = lymbo.getCards();
        }
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public static XmlLymbo getLymbo() {
        return lymbo;
    }

    public static void setLymbo(XmlLymbo lymbo) {
        CardsController.lymbo = lymbo;
    }

    public static List<XmlCard> getCards() {
        return cards;
    }

    public static void setCards(List<XmlCard> cards) {
        CardsController.cards = cards;
    }
}
