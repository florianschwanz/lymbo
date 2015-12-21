package de.interoberlin.lymbo.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.impl.Answer;
import de.interoberlin.lymbo.core.model.v1.impl.Card;
import de.interoberlin.lymbo.core.model.v1.impl.Choice;
import de.interoberlin.lymbo.core.model.v1.impl.Result;
import de.interoberlin.lymbo.core.model.v1.impl.Side;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.lymbo.core.model.v1.impl.Title;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.core.model.v1.impl.Text;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboLoader;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboWriter;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.TableCardDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.TableCardEntry;
import de.interoberlin.lymbo.util.ZipUtil;

public class CardsController {
    // Activity
    private Activity activity;

    // Controllers
    private StacksController stacksController;

    // Database
    private TableCardDatasource datasource;

    // Model
    private Stack stack;
    private List<Card> cards;
    private List<Card> cardsDismissed;
    private List<Card> cardsStashed;
    private List<Card> templates;
    private List<Tag> tagsSelected;

    private boolean displayOnlyFavorites;

    private static CardsController instance;

    // --------------------
    // Constructors
    // --------------------

    private CardsController(Activity activity) {
        init();
        setActivity(activity);
    }

    public static CardsController getInstance(Activity activity) {
        if (instance == null) {
            instance = new CardsController(activity);
        }

        instance.setActivity(activity);

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

    public void init() {
        stacksController = StacksController.getInstance(activity);

        cards = new ArrayList<>();
        cardsDismissed = new CopyOnWriteArrayList<>();
        cardsStashed = new CopyOnWriteArrayList<>();
        templates = new CopyOnWriteArrayList<>();
        tagsSelected = new ArrayList<>();
        displayOnlyFavorites = false;

        if (stack != null) {
            datasource = new TableCardDatasource(activity);
            datasource.open();

            for (Card c : stack.getCards()) {
                if (!datasource.containsUuid(c.getId()) || datasource.isNormal(c.getId())) {
                    cards.add(c);
                } else if (datasource.isDismissed(c.getId())) {
                    cardsDismissed.add(c);
                } else if (datasource.isStashed(c.getId())) {
                    cardsStashed.add(c);
                }
            }

            for (Card t : stack.getTemplates()) {
                templates.add(t);
            }

            datasource.close();
        }
    }

    /**
     * Determines whether a given card shall be displayed considering all filters
     *
     * @param card card to determine visibility of
     * @return whether card is visible or not
     */
    public boolean isVisible(Card card) {
        return (card != null &&
                (!isDisplayOnlyFavorites() || (card.isFavorite())) &&
                card.matchesTag(getTagsSelected()));
    }

    /**
     * Returns the amounts of currently visible cards
     *
     * @return number of visible cards
     */
    public int getVisibleCardCount() {
        int count = 0;

        for (Card card : getCards()) {
            if (isVisible(card))
                count++;
        }

        return count;
    }

    // --------------------
    // Methods
    // --------------------

    /**
     * Writes lymbo object into a file
     */
    public void save() {
        if (stack.getFile() != null) {

            switch (stack.getFormat()) {
                case LYMBO: {
                    LymboWriter.writeXml(stack, new File(stack.getFile()));
                    break;
                }
                case LYMBOX: {
                    LymboWriter.writeXml(stack, new File(stack.getPath() + "/" + activity.getResources().getString(R.string.lymbo_main_file)));
                    ZipUtil.zip(new File(stack.getPath()).listFiles(), new File(stack.getFile()));
                    break;
                }
            }
        }
    }

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
     *
     * @param frontTitle front title
     * @param frontTexts front texts
     * @param backTitle  back title
     * @param backTexts  back texts
     * @param tags       tags
     * @param answers    answers
     */
    public void addCard(Title frontTitle, List<Text> frontTexts, Title backTitle, List<Text> backTexts, List<Tag> tags, List<Answer> answers) {
        Card card = new Card();

        Side front = new Side();
        if (frontTitle != null) {
            front.getComponents().add(frontTitle);
        }
        if (frontTexts != null) {
            for (Text t : frontTexts) {
                front.getComponents().add(t);
            }
        }

        Side back = new Side();
        if (backTitle != null) {
            back.getComponents().add(backTitle);
        }
        if (backTexts != null) {
            for (Text t : backTexts) {
                back.getComponents().add(t);
            }
        }

        if (answers != null) {
            Choice c = new Choice();
            for (Answer a : answers) {
                c.getAnswers().add(a);
            }
            front.getComponents().add(c);
            back.getComponents().add(new Result());
        }

        card.getSides().add(front);
        card.getSides().add(back);

        for (Tag t : tags) {
            card.getTags().add(t);
        }

        stack.getCards().add(card);
        cards.add(card);
        save();
    }

    /**
     * Updates a card
     *
     * @param id         id of the card to be updated
     * @param frontTitle front title
     * @param frontTexts front texts
     * @param backTitle  back title
     * @param backTexts  back texts
     * @param tags       tags
     * @param answers    answers
     */
    public void updateCard(String id, Title frontTitle, List<Text> frontTexts, Title backTitle, List<Text> backTexts, List<Tag> tags, List<Answer> answers) {
        if (cardsContainsId(id)) {
            Card card = getCardById(id);
            card.getSides().clear();
            card.getTags().clear();

            Side front = new Side();
            if (frontTitle != null) {
                front.getComponents().add(frontTitle);
            }
            if (frontTexts != null) {
                for (Text t : frontTexts) {
                    front.getComponents().add(t);
                }
            }

            Side back = new Side();
            if (backTitle != null) {
                back.getComponents().add(backTitle);
            }
            if (backTexts != null) {
                for (Text t : backTexts) {
                    back.getComponents().add(t);
                }
            }

            if (answers != null) {
                Choice c = new Choice();
                for (Answer a : answers) {
                    c.getAnswers().add(a);
                }
                front.getComponents().add(c);
                back.getComponents().add(new Result());
            }

            for (Tag t : tags) {
                card.getTags().add(t);
            }

            card.getSides().add(front);
            card.getSides().add(back);

            save();
        }
    }

    /**
     * Adds a new template to the current stack
     *
     * @param title      title
     * @param frontTitle front title
     * @param frontTexts front texts
     * @param backTitle  back title
     * @param backTexts  back texts
     * @param tags       tags
     */
    public void addTemplate(String title, Title frontTitle, List<Text> frontTexts, Title backTitle, List<Text> backTexts, List<Tag> tags) {
        Card template = new Card();

        template.setTitle(title);

        Side front = new Side();
        if (frontTitle != null) {
            front.getComponents().add(frontTitle);
        }
        if (frontTexts != null) {
            for (Text t : frontTexts) {
                front.getComponents().add(t);
            }
        }

        Side back = new Side();
        if (backTitle != null) {
            back.getComponents().add(backTitle);
        }
        if (backTexts != null) {
            for (Text t : backTexts) {
                back.getComponents().add(t);
            }
        }

        template.getSides().add(front);
        template.getSides().add(back);

        for (Tag t : tags) {
            template.getTags().add(t);
        }

        stack.getTemplates().add(template);
        save();
    }

    /**
     * Updates a template
     *
     * @param id         id of the template to be updated
     * @param title      title
     * @param frontTitle front title
     * @param frontTexts front texts
     * @param backTitle  back title
     * @param backTexts  back texts
     * @param tags       tags
     */
    public void updateTemplate(String id, String title, Title frontTitle, List<Text> frontTexts, Title backTitle, List<Text> backTexts, List<Tag> tags) {
        if (templatesContainsId(id)) {
            Card template = getTemplateById(id);

            template.setTitle(title);

            Card card = getCardById(id);
            card.getSides().clear();
            card.getTags().clear();

            Side front = new Side();
            if (frontTitle != null) {
                front.getComponents().add(frontTitle);
            }
            if (frontTexts != null) {
                for (Text t : frontTexts) {
                    front.getComponents().add(t);
                }
            }

            Side back = new Side();
            if (backTitle != null) {
                back.getComponents().add(backTitle);
            }
            if (backTexts != null) {
                for (Text t : backTexts) {
                    back.getComponents().add(t);
                }
            }

            for (Tag t : tags) {
                card.getTags().add(t);
            }

            card.getSides().add(front);
            card.getSides().add(back);

            save();
        }
    }

    public void deleteTemplate(Card template) {
        if (template != null && template.getId() != null) {
            Card t = getTemplateById(template.getId());

            if (t != null)
                stack.getTemplates().remove(t);
        }
        save();
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
     * Restores all stashed cards
     */
    public void restoreAll() {
        for (Card card : cardsStashed) {
            restore(card);
        }
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
     * @param context context
     * @param uuid    id of the card
     * @return note of a card
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
     * @param context context
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
     * Changes the favorite status of a card
     *
     * @param context  context
     * @param card     card
     * @param favorite whether or not to set a card as a favorite
     */
    public void toggleFavorite(Context context, Card card, boolean favorite) {
        card.setFavorite(favorite);

        datasource = new TableCardDatasource(context);
        datasource.open();
        datasource.updateCardFavorite(card.getId(), favorite);
        datasource.close();
    }

    /**
     * Copies a card from one stack to another
     *
     * @param targetLymboId id of target stack
     * @param cardId        id of card to be copied
     * @param deepCopy      true if the copy shall be deep
     */
    public void copyCard(String targetLymboId, String cardId, boolean deepCopy) {
        Stack targetStack = stacksController.getStackById(targetLymboId);
        Card card = getCardById(cardId);

        if (deepCopy)
            card.setId(UUID.randomUUID().toString());

        targetStack.getCards().add(card);
        stacksController.save(targetStack);
    }

    /**
     * Moves a card from one stack to another
     *
     * @param sourceLymboId id of source stack
     * @param targetLymboId id of target stack
     * @param cardId        id of card to be copied
     */
    public void moveCard(String sourceLymboId, String targetLymboId, String cardId) {
        Stack sourceStack = stacksController.getStackById(sourceLymboId);
        Stack targetStack = stacksController.getStackById(targetLymboId);
        Card card = getCardById(cardId);

        targetStack.getCards().add(card);
        stacksController.save(targetStack);

        sourceStack.getCards().remove(card);
        stacksController.save(sourceStack);

        getCards().remove(card);
        save();
    }

    // --------------------
    // Methods - Util
    // --------------------

    private Resources getResources() {
        return activity.getResources();
    }

    public void addTagsSelected(List<Tag> tags) {
        for (Tag t : tags) {
            if (!t.containedInList(tagsSelected)) {
                tagsSelected.add(t);
            }
        }
    }

    public List<Tag> getTagsAll() {
        List<Tag> tagsAll = new ArrayList<>();

        for (Card card : getCards()) {
            for (Tag tag : card.getTags()) {
                if (tag != null && !(tag).containedInList(tagsAll) && !tag.getValue().equals(getResources().getString(R.string.no_tag)))
                    tagsAll.add(tag);
            }
        }

        for (Card template : getTemplates()) {
            for (Tag tag : template.getTags()) {
                if (tag != null && !(tag).containedInList(tagsAll) && !tag.getValue().equals(getResources().getString(R.string.no_tag)))
                    tagsAll.add(tag);
            }
        }

        tagsAll.add(new Tag(getResources().getString(R.string.no_tag)));

        return tagsAll;
    }

    /**
     * Realoads a single stack
     *
     * @param path  path of the stack
     * @param asset whether the stack is an asset
     */
    public void reloadStack(String path, boolean asset) {
        if (asset)
            stack = LymboLoader.getLymboFromAsset(activity, path, false);
        else
            stack = LymboLoader.getLymboFromFile(activity, new File(path), false);
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Stack getStack() {
        return stack;
    }

    public void setStack(Stack stack) {
        this.stack = stack;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setTemplates(List<Card> templates) {
        this.templates = templates;
    }

    public List<Card> getTemplates() {
        return templates;
    }

    public List<Card> getCardsDiscarded() {
        return cardsDismissed;
    }

    public List<Card> getCardsStashed() {
        return cardsStashed;
    }

    public List<Tag> getTagsSelected() {
        return tagsSelected;
    }

    public void setTagsSelected(List<Tag> tagsSelected) {
        this.tagsSelected = tagsSelected;
    }

    public boolean cardsContainsId(String id) {
        for (Card card : stack.getCards()) {
            if (card.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public Card getCardById(String id) {
        for (Card card : stack.getCards()) {
            if (card.getId().equals(id)) {
                return card;
            }
        }

        return null;
    }

    public boolean templatesContainsId(String id) {
        synchronized (stack.getTemplates()) {
            for (Card template : stack.getTemplates()) {
                if (template.getId().equals(id)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Card getTemplateById(String id) {
        for (Card template : stack.getTemplates()) {
            if (template.getId().equals(id)) {
                return template;
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
