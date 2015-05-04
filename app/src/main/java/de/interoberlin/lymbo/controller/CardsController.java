package de.interoberlin.lymbo.controller;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.card.components.TitleComponent;
import de.interoberlin.lymbo.model.card.enums.EGravity;
import de.interoberlin.lymbo.model.persistence.LymboWriter;

public class CardsController {
    private Lymbo lymbo;
    private List<Card> cards = new ArrayList<>();

    private LymbosController lymbosController = LymbosController.getInstance();

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
     * Renames a lymbo file so that it will not be found anymore
     */
    public void stash() {
        lymbosController.getLymbos().remove(lymbo);
        lymbosController.getLymbosStashed().add(lymbo);

        lymbosController.changeLocation(lymbo.getPath(), true);
    }

    /**
     * Renames a lymbo file so that it will be found again
     */
    public void restore() {
        lymbosController.getLymbos().add(lymbo);
        lymbosController.getLymbosStashed().remove(lymbo);

        lymbosController.changeLocation(lymbo.getPath(), false);
    }

    public void addSimpleCard(String frontText, String backText) {
        TitleComponent front = new TitleComponent();
        front.setValue(frontText);
        front.setGravity(EGravity.CENTER);
        TitleComponent back = new TitleComponent();
        back.setGravity(EGravity.CENTER);
        back.setValue(backText);

        Card card = new Card();
        card.getFront().addComponent(front);
        card.getBack().addComponent(back);

        cards.add(card);
    }

    /**
     * Writes lymbo object into file
     */
    public void save() {
        if (lymbo.getPath() != null) {
            LymboWriter.writeXml(lymbo, new File(lymbo.getPath()));
        }
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public Lymbo getLymbo() {
        return lymbo;
    }

    public void setLymbo(Lymbo lymbo) {
        this.lymbo = lymbo;
    }

    public List<Card> getCards() {
        return cards;
    }
}
