package de.interoberlin.lymbo.controller;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Lymbo;

public class CardsController {
    private Lymbo lymbo;
    private List<Card> cards = new ArrayList<>();

    private static final String LYMBO_FILE_EXTENSION = ".lymbo";
    private static final String LYMBO_FILE_EXTENSION_STASHED = ".lymbo.stashed";

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
     * Writes lymbo object into file
     * (only if lymbo is on file system)
     */
    /*
    public void save() {
        if (lymbo.getPath() != null) {
            LymboWriter.writeXml(lymbo, new File(lymbo.getPath()));
        }
    }
    */

    /**
     * Renames a lymbo file so that it will not be found anymore
     */
    public void stash() {
        new File(lymbo.getPath()).renameTo(new File(lymbo.getPath().replace(LYMBO_FILE_EXTENSION, LYMBO_FILE_EXTENSION_STASHED)));
    }

    public void restore() {
        new File(lymbo.getPath()).renameTo(new File(lymbo.getPath().replace(LYMBO_FILE_EXTENSION_STASHED, LYMBO_FILE_EXTENSION)));
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
