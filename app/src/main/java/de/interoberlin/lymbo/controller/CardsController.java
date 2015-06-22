package de.interoberlin.lymbo.controller;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.card.Side;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.model.card.components.TitleComponent;
import de.interoberlin.lymbo.model.card.enums.EGravity;
import de.interoberlin.lymbo.model.persistence.LymboLoader;
import de.interoberlin.lymbo.model.persistence.LymboWriter;
import de.interoberlin.lymbo.model.persistence.sqlite.notes.LymboNote;
import de.interoberlin.lymbo.model.persistence.sqlite.notes.LymboNoteDatasource;

public class CardsController {
    private Lymbo lymbo;
    private List<Card> cards = new ArrayList<>();
    private LymbosController lymbosController = LymbosController.getInstance();

    private LymboNoteDatasource datasource;

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

    /**
     * Resets all cards
     */
    public void reset() {
        for (Card card : cards) {
            if (card != null) {
                card.reset();
            }
        }
    }

    /**
     * Renames a lymbo file so that it will not be found anymore
     *
     * @param lymbo lymbo to be stashed
     */
    public void stash(Lymbo lymbo) {
        lymbosController.getLymbos().remove(lymbo);
        lymbosController.getLymbosStashed().add(lymbo);
        lymbosController.changeLocation(lymbo.getPath(), true);
    }

    public void getCardsFromLymbo() {
        if (lymbo != null) {
            cards = lymbo.getCards();
        }
    }

    /**
     * Renames a lymbo file so that it will be found again
     *
     * @param lymbo lymbo to be stashed
     */
    public void restore(Lymbo lymbo) {
        lymbosController.getLymbos().add(lymbo);
        lymbosController.getLymbosStashed().remove(lymbo);

        lymbosController.changeLocation(lymbo.getPath(), false);
    }

    public Card getSimpleCard(String frontText, String backText) {
        Card card = new Card();

        TitleComponent frontTitle = new TitleComponent();
        frontTitle.setValue(frontText);
        frontTitle.setGravity(EGravity.CENTER);

        TitleComponent backTitle = new TitleComponent();
        backTitle.setGravity(EGravity.CENTER);
        backTitle.setValue(backText);

        Side frontSide = new Side(frontTitle);
        Side backSide = new Side(backTitle);

        card.getSides().add(frontSide);
        card.getSides().add(backSide);

        return card;
    }

    public Card getSimpleCard(String frontText, String backText, List<Tag> tags) {
        Card card = getSimpleCard(frontText, backText);
        card.setTags(tags);

        return card;
    }

    public void addCard(Card card) {
        cards.add(card);
        save();
    }

    /**
     * Writes lymbo object into file
     */
    public void save() {
        if (lymbo.getPath() != null) {
            lymbo.setModificationDate(new Date().toString());
            LymboWriter.writeXml(lymbo, new File(lymbo.getPath()));
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
        addNullElement(cards);
    }

    /**
     * Discards a card from the current stack
     *
     * @param pos index of the card to be discarded
     */
    public void discard(int pos) {
        getCards().get(pos).setDiscarded(true);
        // save();
    }

    /**
     * Retains a card that has been removed
     *
     * @param pos index of the card to be retained
     */
    public void retain(int pos) {
        Card card = getCards().get(pos);
        card.reset();
        card.setRestoring(true);

        card.setDiscarded(false);
    }

    /**
     * Puts a card with a given index to the end
     *
     * @param pos index of the card to be moved
     */
    public void putToEnd(int pos) {
        Card card = getCards().get(pos);
        card.reset();
        card.setRestoring(true);

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
        card.setRestoring(true);

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

    public void setNote(Context context, String uuid, String text) {
        datasource = new LymboNoteDatasource(context);
        datasource.open();
        datasource.updateNote(uuid, text);
        datasource.close();
    }

    public String getNote(Context context, String uuid) {
        datasource = new LymboNoteDatasource(context);
        datasource.open();

        LymboNote note = datasource.getNote(uuid);

        datasource.close();

        return note != null ? note.getText() : null;
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

    public void setFullLymbo(Context c, Lymbo lymbo) {
        if (lymbo.isAsset()) {
            this.lymbo = LymboLoader.getLymboFromAsset(c, lymbo.getPath(), false);
        } else {
            this.lymbo = LymboLoader.getLymboFromFile(new File(lymbo.getPath()), false);
        }
    }

    public List<Card> getCards() {
        addNullElement(cards);

        return cards;
    }
}
