package de.interoberlin.lymbo.controller;


import de.interoberlin.lymbo.model.card.Card;

public class ComponentsController {
    private static Card card;

    private static ComponentsController instance;

    // --------------------
    // Constructors
    // --------------------

    private ComponentsController() {
        init();
    }

    public static ComponentsController getInstance() {
        if (instance == null) {
            instance = new ComponentsController();
        }

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

    public void init() {
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        ComponentsController.card = card;
    }
}
