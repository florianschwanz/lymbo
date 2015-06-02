package de.interoberlin.lymbo.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
        addCard(getSimpleCard(frontText, backText));
        save();
    }

    public Card getSimpleCard(String frontText, String backText) {
        Card card = new Card();
        card.setId(UUID.randomUUID().toString());
        TitleComponent frontTitle = new TitleComponent();
        frontTitle.setValue(frontText);
        frontTitle.setGravity(EGravity.CENTER);
        TitleComponent backTitle = new TitleComponent();
        backTitle.setGravity(EGravity.CENTER);
        backTitle.setValue(backText);

        return card;
    }

    public void addCard(Card card) {
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

    public void shuffle() {
        Collections.shuffle(cards);
        addNullElement(cards);
    }

    /**
     * Puts a card with a given index to the end
     *
     * @param pos index of the card to be moved
     */
    public void putToEnd(int pos) {
        Card card = getCards().get(pos);
        card.reset();

        getCards().add(card);
        getCards().remove(pos);
    }

    /**
     * Puts a card from the end to a given position
     *
     * @param pos index to put card
     */
    public void putLastItemToPos(int pos) {
        int lastItem = getCards().size() - 1;

        Card card = getCards().get(lastItem);

        getCards().remove(lastItem);
        getCards().add(pos, card);
    }

    /**
     * This is necessary to display the first element below the toolbar
     *
     * @param list list which shall be extended by a leading null element
     */
    public void addNullElement(List<Card> list) {
        if (!list.isEmpty()) {
            list.removeAll(Collections.singleton(null));

            if (list.get(0) != null) {
                list.add(0, null);
            }
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
        addNullElement(cards);

        return cards;
    }
}
