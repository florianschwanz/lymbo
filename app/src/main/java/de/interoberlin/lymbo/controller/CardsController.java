package de.interoberlin.lymbo.controller;


import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Lymbo;

public class CardsController {
    private static Lymbo lymbo;
    private static List<Card> cards = new ArrayList<Card>();

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

    public static Lymbo getLymbo() {
        return lymbo;
    }

    public static void setLymbo(Lymbo lymbo) {
        CardsController.lymbo = lymbo;
    }

    public static List<Card> getCards() {
        return cards;
    }

    public static void setCards(List<Card> cards) {
        CardsController.cards = cards;
    }
}
