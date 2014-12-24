package de.interoberlin.lymbo.controller;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.card.Side;
import de.interoberlin.lymbo.model.persistence.LymboWriter;

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

    /**
     * Adds a card with an empty front and an empty back
     */
    public void addCard() {
        Card card = new Card();
        card.setFront(new Side());
        card.setBack(new Side());
        cards.add(card);
    }

    /**
     * Writes lymbo object into file
     * (only if lymbo is on file system)
     */
    public void save() {
        if (lymbo.getPath() != null) {
            LymboWriter.writeXml(lymbo, new File(lymbo.getPath()));
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
