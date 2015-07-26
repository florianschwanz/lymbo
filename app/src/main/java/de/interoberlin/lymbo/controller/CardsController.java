package de.interoberlin.lymbo.controller;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private List<Card> cardsDismissed;
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
        cardsDismissed = new CopyOnWriteArrayList<>();
        cardsStashed = new CopyOnWriteArrayList<>();
        displayOnlyFavorites = false;

        if (lymbo != null) {
            datasource = new TableCardDatasource(activity);
            datasource.open();

            for (Card c : lymbo.getCards()) {
                if (!datasource.containsUuid(c.getId()) || datasource.isNormal(c.getId())) {
                    cards.add(c);
                } else if (datasource.isDismissed(c.getId())) {
                    cardsDismissed.add(c);
                } else if (datasource.isStashed(c.getId())) {
                    cardsStashed.add(c);
                }
            }

            datasource.close();
        }
    }

    public int getVisibleCardCount() {
        int count = 0;

        for (Card card : getCards()) {
            if (card != null && card.matchesChapter(getLymbo().getChapters()) && card.matchesTag(getLymbo().getTags()))
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
        synchronized (cardsDismissed) {
            for (Card card : cardsDismissed) {
                if (card != null) {
                    retain(card);
                }
            }
        }

        synchronized (cards) {
            for (Card card : cards) {
                if (card != null) {
                    card.reset();
                }
            }
        }
    }

    /**
     * Adds a new card to the current stack
     */
    public void addCard(String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags) {
        Card card = new Card(frontTitleValue, frontTextsValues, backTitleValue, backTextsValues, tags);

        lymbo.getCards().add(card);
        cards.add(card);
        save();
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
     * @param card card
     */
    public void stash(Card card) {
        card.setRestoring(true);

        getCards().remove(card);
        getCardsStashed().add(card);
        changeCardStateStashed(card);
    }

    /**
     * Stashes a card
     *
     * @param pos  position where the card will be stashed
     * @param card card
     */
    public void stash(int pos, Card card) {
        card.setRestoring(true);

        getCards().remove(card);
        getCardsStashed().add(pos < getCardsStashed().size() ? pos : 0, card);
        changeCardStateStashed(card);
    }

    /**
     * Restores a card
     *
     * @param card card
     */
    public void restore(Card card) {
        card.setRestoring(true);

        getCards().add(card);
        getCardsStashed().remove(card);
        changeCardStateNormal(card);
    }

    /**
     * Restores a card
     *
     * @param pos  position where the card will be restored
     * @param card card
     */
    public void restore(int pos, Card card) {
        card.setRestoring(true);

        getCards().add(pos < getCards().size() ? pos : 0, card);
        getCardsStashed().remove(card);
        changeCardStateNormal(card);
    }

    /**
     * Discards a card from the current stack
     *
     * @param card card
     */
    public void discard(Card card) {
        getCards().remove(card);
        getCardsDiscarded().add(card);
        changeCardStateDiscarded(card);
    }

    /**
     * Retains a card that has been removed
     *
     * @param card card
     */
    public void retain(Card card) {
        card.setRestoring(true);

        getCardsDiscarded().remove(card);
        getCards().add(card);
        changeCardStateNormal(card);
    }

    /**
     * Retains a card that has been removed
     *
     * @param pos  position where the card will be retained
     * @param card card
     */
    public void retain(int pos, Card card) {
        card.setRestoring(true);

        getCards().add(pos < getCards().size() ? pos : 0, card);
        getCardsDiscarded().remove(card);
        changeCardStateNormal(card);
    }

    private void changeCardStateNormal(Card card) {
        datasource = new TableCardDatasource(activity);
        datasource.open();
        datasource.updateCardStateNormal(card.getId());
        datasource.close();
    }

    private void changeCardStateDiscarded(Card card) {
        datasource = new TableCardDatasource(activity);
        datasource.open();
        datasource.updateCardStateDiscarded(card.getId());
        datasource.close();
    }

    private void changeCardStateStashed(Card card) {
        datasource = new TableCardDatasource(activity);
        datasource.open();
        datasource.updateCardStateStashed(card.getId());
        datasource.close();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Puts a card to the end
     *
     * @param card card
     */
    public void putToEnd(Card card) {
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
        return cards;
    }

    public List<Card> getCardsDiscarded() {
        return cardsDismissed;
    }

    public List<Card> getCardsStashed() {
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
