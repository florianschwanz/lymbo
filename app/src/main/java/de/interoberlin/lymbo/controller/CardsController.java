package de.interoberlin.lymbo.controller;

import android.app.Activity;
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
import de.interoberlin.lymbo.model.persistence.filesystem.LymboLoader;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboWriter;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.CardStateDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.notes.Note;
import de.interoberlin.lymbo.model.persistence.sqlite.notes.NoteDatasource;

public class CardsController {
    // Activity
    private Activity activity;

    // Database
    private NoteDatasource datasource;

    // Model
    private Lymbo lymbo;
    private List<Card> cards;
    private List<Card> cardsStashed;
    private LymbosController lymbosController;

    private static CardsController instance;

    // --------------------
    // Constructors
    // --------------------

    private CardsController(Activity activity) {
        this.activity = activity;
        init();
    }

    public static CardsController getInstance(Activity activity) {
        if (instance == null) {
            instance = new CardsController(activity);
        }

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

    public void init() {
        lymbosController = LymbosController.getInstance(activity);

        cards = new ArrayList<>();
        cardsStashed = new ArrayList<>();

        if (lymbo != null) {
            CardStateDatasource dsCardState = new CardStateDatasource(activity);
            dsCardState.open();

            for (Card c : lymbo.getCards()) {
                if (!dsCardState.containsUuid(c.getId()) || !dsCardState.getStashed(c.getId())) {
                    cards.add(c);
                } else {
                    cardsStashed.add(c);
                }
            }

            addNullElementToCards();
            addNullElementToCardsStashed();

            dsCardState.close();
        }
    }

    // --------------------
    // Methods - lymbo
    // --------------------

    /**
     * Stashes a lymbo
     *
     * @param lymbo lymbo to be stashed
     */
    public void stash(Lymbo lymbo) {
        lymbosController.getLymbos().remove(lymbo);
        lymbosController.getLymbosStashed().add(lymbo);
        lymbosController.changeLocation(lymbo.getPath(), true);
    }

    /**
     * Restores a lymbo
     *
     * @param lymbo lymbo to be stashed
     */
    public void restore(Lymbo lymbo) {
        lymbosController.getLymbos().add(lymbo);
        lymbosController.getLymbosStashed().remove(lymbo);
        lymbosController.changeLocation(lymbo.getPath(), false);
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

    // --------------------
    // Methods - cards
    // --------------------

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
     * Adds a new card to the current stack
     *
     * @param card card to be added
     */
    public void addCard(Card card) {
        lymbo.getCards().add(card);
        cards.add(card);
        addNullElementToCards();
        save();
    }

    /**
     * Returns a simple card
     *
     * @param frontText text on front side
     * @param backText  text on back side
     * @return card
     */
    public Card getSimpleCard(String frontText, String backText) {
        Card card = new Card();

        TitleComponent frontTitle = new TitleComponent();
        frontTitle.setValue(frontText);
        frontTitle.setGravity(EGravity.CENTER);
        frontTitle.setFlip(true);

        TitleComponent backTitle = new TitleComponent();
        backTitle.setGravity(EGravity.CENTER);
        backTitle.setValue(backText);
        backTitle.setFlip(true);

        Side frontSide = new Side(frontTitle);
        Side backSide = new Side(backTitle);

        card.getSides().add(frontSide);
        card.getSides().add(backSide);
        card.setFlip(true);

        return card;
    }

    /**
     * Returns a simple card
     *
     * @param frontText text on front side
     * @param backText  text on back side
     * @param tags      tags for this card
     * @return card
     */
    public Card getSimpleCard(String frontText, String backText, List<Tag> tags) {
        Card card = getSimpleCard(frontText, backText);
        card.setTags(tags);

        return card;
    }

    /**
     * Stashes a card
     *
     * @param uuid id of the card to be stashed
     */
    public void stash(String uuid) {
        Card card = getCardById(uuid);
        card.setRestoring(true);

        getCards().remove(card);
        getCardsStashed().add(card);
        changeCardState(uuid, true);
    }

    /**
     * Stashes a card
     *
     * @param pos  position where the card will be stashed
     * @param uuid id of the card to be stashed
     */
    public void stash(int pos, String uuid) {
        Card card = getCardById(uuid);
        card.setRestoring(true);

        getCards().remove(card);
        getCardsStashed().add(pos < getCardsStashed().size() ? pos : 0, card);
        changeCardState(uuid, true);

        addNullElementToCards();
    }

    /**
     * Restores a card
     *
     * @param uuid id of the card to be restored
     */
    public void restore(String uuid) {
        Card card = getCardById(uuid);
        card.setRestoring(true);

        getCards().add(card);
        getCardsStashed().remove(card);
        changeCardState(uuid, false);
    }

    /**
     * Restores a card
     *
     * @param pos  position where the card will be resored
     * @param uuid id of the card to be restored
     */
    public void restore(int pos, String uuid) {
        Card card = getCardById(uuid);
        card.setRestoring(true);

        getCards().add(pos < getCards().size() ? pos : 0, card);
        getCardsStashed().remove(card);
        changeCardState(uuid, false);

        addNullElementToCards();
    }

    private void changeCardState(String uuid, boolean stashed) {
        CardStateDatasource dsCardState = new CardStateDatasource(activity);
        dsCardState.open();
        dsCardState.updateCardState(uuid, stashed);
        dsCardState.close();
    }

    public void shuffle() {
        Collections.shuffle(cards);
        addNullElementToCards();
    }

    /**
     * Discards a card from the current stack
     *
     * @param uuid index of the card to be discarded
     */
    public void discard(String uuid) {
        Card card = getCardById(uuid);
        card.setDiscarded(true);
    }

    /**
     * Retains a card that has been removed
     *
     * @param uuid index of the card to be retained
     */
    public void retain(String uuid) {
        Card card = getCardById(uuid);
        card.reset();
        card.setRestoring(true);
        card.setDiscarded(false);
    }

    /**
     * Puts a card with a given index to the end
     *
     * @param uuid index of the card to be moved
     */
    public void putToEnd(String uuid) {
        Card card = getCardById(uuid);
        card.reset();
        card.setRestoring(true);

        getCards().add(card);
        getCards().remove(card);
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

        getCards().remove(card);
        getCards().add(pos < getCards().size() ? pos : 0, card);
        addNullElementToCards();
    }

    public void selectLabel() {
        addNullElementToCards();
    }

    public void setNote(Context context, String uuid, String text) {
        datasource = new NoteDatasource(context);
        datasource.open();
        datasource.updateNote(uuid, text);
        datasource.close();
    }

    public String getNote(Context context, String uuid) {
        datasource = new NoteDatasource(context);
        datasource.open();

        Note note = datasource.getNote(uuid);

        datasource.close();

        return note != null ? note.getText() : null;
    }

    /**
     * This is necessary to display the first element below the toolbar
     *
     */
    public void addNullElementToCards() {
        addNullElement(cards);
    }

    /**
     * This is necessary to display the first element below the toolbar
     *
     */
    public void addNullElementToCardsStashed() {
        addNullElement(cardsStashed);
    }

    private void addNullElement(List<Card> list) {
        if (list != null) {
            list.removeAll(Collections.singleton(null));

            if (!list.isEmpty()) {
                // Add leading null element
                if (list.get(0) != null) {
                    list.add(0, null);
                }
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

    public void setFullLymbo(Context c, Lymbo lymbo) {
        if (lymbo.isAsset()) {
            this.lymbo = LymboLoader.getLymboFromAsset(c, lymbo.getPath(), false);
        } else {
            this.lymbo = LymboLoader.getLymboFromFile(new File(lymbo.getPath()), false);
        }
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {
        addNullElementToCards();
        return cards;
    }

    public List<Card> getCardsStashed() {
        addNullElementToCardsStashed();
        return cardsStashed;
    }

    public Card getCardById(String uuid) {
        for (Card c : lymbo.getCards()) {
            if (c.getId().equals(uuid)) {
                return c;
            }
        }

        return null;
    }
}
