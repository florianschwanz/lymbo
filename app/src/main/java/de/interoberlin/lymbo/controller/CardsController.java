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
import de.interoberlin.lymbo.model.card.components.TextComponent;
import de.interoberlin.lymbo.model.card.components.TitleComponent;
import de.interoberlin.lymbo.model.card.enums.EGravity;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboLoader;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboWriter;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.TableCardDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.TableCardEntry;

public class CardsController {
    // Activity
    private Activity activity;

    // Database
    private TableCardDatasource datasource;

    // Model
    private Lymbo lymbo;
    private List<Card> cards;
    private List<Card> cardsStashed;
    private LymbosController lymbosController;

    private boolean displayOnlyFavorites;

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
        displayOnlyFavorites = false;

        if (lymbo != null) {
            datasource = new TableCardDatasource(activity);
            datasource.open();

            for (Card c : lymbo.getCards()) {
                if (!datasource.containsUuid(c.getId()) || datasource.isNormal(c.getId())) {
                    cards.add(c);
                } else if (datasource.isStashed(c.getId())) {
                    cardsStashed.add(c);
                }
            }

            addNullElementToCards();
            addNullElementToCardsStashed();

            datasource.close();
        }
    }

    public int getVisibleCardCount() {
        int count = 0;

        for (Card card : getCards()) {
            if (card != null && !card.isDiscarded() && card.matchesChapter(getLymbo().getChapters()) && card.matchesTag(getLymbo().getTags()))
                count++;
        }

        return count;
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
        lymbosController.changeState(lymbo.getId(), true);
    }

    /**
     * Restores a lymbo
     *
     * @param lymbo lymbo to be stashed
     */
    public void restore(Lymbo lymbo) {
        lymbosController.getLymbos().add(lymbo);
        lymbosController.getLymbosStashed().remove(lymbo);
        lymbosController.changeState(lymbo.getId(), false);
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
     * @param frontTitleValue
     * @param frontTextsValues
     * @param backTitleValue
     * @param backTextsValues
     * @param tags
     * @return
     */
    public Card getSimpleCard(String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags) {
        Card card = new Card();
        Side frontSide = new Side();
        Side backSide = new Side();

        TitleComponent frontTitle = new TitleComponent();
        frontTitle.setValue(frontTitleValue);
        frontTitle.setGravity(EGravity.CENTER);
        frontTitle.setFlip(true);
        frontSide.addComponent(frontTitle);

        for (String frontTextValue : frontTextsValues) {
            TextComponent frontText = new TextComponent();
            frontText.setValue(frontTextValue);
            frontText.setGravity(EGravity.LEFT);
            frontText.setFlip(true);
            frontSide.addComponent(frontText);
        }

        TitleComponent backTitle = new TitleComponent();
        backTitle.setGravity(EGravity.CENTER);
        backTitle.setValue(backTitleValue);
        backTitle.setFlip(true);
        backSide.addComponent(backTitle);

        for (String backTextValue : backTextsValues) {
            TextComponent backText = new TextComponent();
            backText.setValue(backTextValue);
            backText.setGravity(EGravity.LEFT);
            backText.setFlip(true);
            backSide.addComponent(backText);
        }

        card.getSides().add(frontSide);
        card.getSides().add(backSide);
        card.setFlip(true);

        card.setTags(tags);

        return card;
    }

    /**
     * Updates a simple card
     *
     * @param uuid
     * @param frontTitleValue
     * @param frontTextsValues
     * @param backTitleValue
     * @param backTextsValues
     * @param tags
     */
    public void updateCard(String uuid, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags) {
        if (cardsContainsId(uuid)) {
            Card card = getCardById(uuid);

            if (card.getSides().size() > 0) {
                Side frontSide = card.getSides().get(0);
                frontSide.getComponents().clear();

                TitleComponent frontTitle = new TitleComponent();
                frontTitle.setValue(frontTitleValue);
                frontTitle.setGravity(EGravity.CENTER);
                frontTitle.setFlip(true);
                frontSide.addComponent(frontTitle);

                for (String frontTextValue : frontTextsValues) {
                    TextComponent frontText = new TextComponent();
                    frontText.setValue(frontTextValue);
                    frontText.setGravity(EGravity.LEFT);
                    frontText.setFlip(true);
                    frontSide.addComponent(frontText);
                }
            }

            if (card.getSides().size() > 1) {
                Side backSide = card.getSides().get(1);
                backSide.getComponents().clear();

                TitleComponent backTitle = new TitleComponent();
                backTitle.setGravity(EGravity.CENTER);
                backTitle.setValue(backTitleValue);
                backTitle.setFlip(true);
                backSide.addComponent(backTitle);

                for (String backTextValue : backTextsValues) {
                    TextComponent backText = new TextComponent();
                    backText.setValue(backTextValue);
                    backText.setGravity(EGravity.LEFT);
                    backText.setFlip(true);
                    backSide.addComponent(backText);
                }
            }

            card.setTags(tags);

            save();
        }
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
        changeCardStateStashed(uuid);
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
        changeCardStateStashed(uuid);

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
        changeCardStateNormal(uuid);
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
        changeCardStateNormal(uuid);

        addNullElementToCards();
    }

    private void changeCardStateNormal(String uuid) {
        datasource = new TableCardDatasource(activity);
        datasource.open();
        datasource.updateCardStateNormal(uuid);
        datasource.close();
    }

    private void changeCardStateDismissed(String uuid) {
        datasource = new TableCardDatasource(activity);
        datasource.open();
        datasource.updateCardStateDismissed(uuid);
        datasource.close();
    }

    private void changeCardStateStashed(String uuid) {
        datasource = new TableCardDatasource(activity);
        datasource.open();
        datasource.updateCardStateStashed(uuid);
        datasource.close();
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

    /**
     * Returns the note of a card
     *
     * @param context
     * @param uuid    id of the card
     * @return
     */
    public String getNote(Context context, String uuid) {
        datasource = new TableCardDatasource(context);
        datasource.open();
        TableCardEntry entry = datasource.getEntryByUuid(uuid);
        datasource.close();

        return entry != null ? entry.getNote() : null;
    }

    /**
     * Sets the note of a card
     *
     * @param context
     * @param uuid    id of a card
     * @param text    text of the note
     */
    public void setNote(Context context, String uuid, String text) {
        datasource = new TableCardDatasource(context);
        datasource.open();
        datasource.updateCardNote(uuid, text);
        datasource.close();
    }

    /**
     * Determines whether a card belongs to the favorites
     *
     * @param context context
     * @param uuid    id of the card
     * @return
     */
    public boolean isFavorite(Context context, String uuid) {
        datasource = new TableCardDatasource(context);
        datasource.open();
        TableCardEntry entry = datasource.getEntryByUuid(uuid);
        datasource.close();

        return entry != null ? entry.isFavorite() : false;
    }

    /**
     * Changes the favorite status of a card
     *
     * @param context  context
     * @param uuid     id of the card
     * @param favorite whether or not to set a card as a favorite
     */
    public void toggleFavorite(Context context, String uuid, boolean favorite) {
        datasource = new TableCardDatasource(context);
        datasource.open();
        datasource.updateCardFavorite(uuid, favorite);
        datasource.close();
    }

    /**
     * This is necessary to display the first element below the toolbar
     */
    public void addNullElementToCards() {
        addNullElement(cards);
    }

    /**
     * This is necessary to display the first element below the toolbar
     */
    public void addNullElementToCardsStashed() {
        addNullElement(cardsStashed);
    }

    /**
     * This is necessary to display the first element below the toolbar
     */
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

    public boolean cardsContainsId(String uuid) {
        for (Card c : lymbo.getCards()) {
            if (c.getId().equals(uuid)) {
                return true;
            }
        }

        return false;
    }

    public Card getCardById(String uuid) {
        for (Card c : lymbo.getCards()) {
            if (c.getId().equals(uuid)) {
                return c;
            }
        }

        return null;
    }

    public boolean isDisplayOnlyFavorites() {
        return displayOnlyFavorites;
    }

    public void setDisplayOnlyFavorites(boolean displayOnlyFavorites) {
        this.displayOnlyFavorites = displayOnlyFavorites;
    }
}
