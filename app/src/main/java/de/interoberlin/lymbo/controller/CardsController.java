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

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.impl.Card;
import de.interoberlin.lymbo.core.model.v1.impl.Side;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.core.model.v1.impl.components.Answer;
import de.interoberlin.lymbo.core.model.v1.impl.components.Choice;
import de.interoberlin.lymbo.core.model.v1.impl.components.EChoiceType;
import de.interoberlin.lymbo.core.model.v1.impl.components.EGravity;
import de.interoberlin.lymbo.core.model.v1.impl.components.Result;
import de.interoberlin.lymbo.core.model.v1.impl.components.Text;
import de.interoberlin.lymbo.core.model.v1.impl.components.Title;
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
     * @return whether card is visbible or not
     */
    public boolean isVisible(Card card) {
        if (card != null) {
            Tag noTag = new Tag(getResources().getString(R.string.no_tag));
            boolean includeStacksWithoutTag = noTag.containedInList(getTagsSelected());
            boolean matchesTags = card.matchesTag(getTagsSelected(), includeStacksWithoutTag);
            boolean matchesFavorites = (isDisplayOnlyFavorites() && card.isFavorite()) || !isDisplayOnlyFavorites();

            return matchesTags && matchesFavorites;
        } else {
            return false;
        }
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
     * Writes lymbo object into file
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
     */
    public void addCard(String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags, List<Answer> answers) {
        Card card = new Card();
        card.setId(UUID.randomUUID().toString());

        if ((frontTitleValue != null && !frontTitleValue.isEmpty()) || !frontTextsValues.isEmpty() || !answers.isEmpty()) {
            Side sideFront = new Side();

            if (frontTitleValue != null && !frontTitleValue.isEmpty())
            sideFront.getComponents().add(new Title(frontTitleValue));

            if (!frontTextsValues.isEmpty())
                for (String s : frontTextsValues) {
                    sideFront.getComponents().add(new Text(s));
                }

            if (!answers.isEmpty())
                sideFront.getComponents().add(new Choice(EChoiceType.MULTIPLE, answers));

            card.getSides().add(sideFront);
        }

        if ((backTitleValue != null && !backTitleValue.isEmpty()) || !backTextsValues.isEmpty() || !answers.isEmpty()) {
            Side sideBack = new Side();

            if (!answers.isEmpty())
                sideBack.getComponents().add(new Result());

            if (backTitleValue != null && !backTitleValue.isEmpty())
                sideBack.getComponents().add(new Title(backTitleValue));

            if (backTextsValues.isEmpty())
                for (String s : backTextsValues) {
                    sideBack.getComponents().add(new Text(s));
                }

            card.getSides().add(sideBack);
        }

        card.setTags(tags);

        stack.getCards().add(card);
        cards.add(card);
        save();
    }

    /**
     * Updates a card
     *
     * @param uuid             id of the card to be updated
     * @param frontTitleValue  front title
     * @param frontTextsValues front texts
     * @param backTitleValue   back title
     * @param backTextsValues  back texts
     * @param tags             tags
     * @param answers          answers
     */
    public void updateCard(String uuid, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags, List<Answer> answers) {
        if (cardsContainsId(uuid)) {
            Card card = getCardById(uuid);

            if (card.getSides().size() > 0) {
                Side frontSide = card.getSides().get(0);
                frontSide.getComponents().clear();

                Title frontTitle = new Title();
                frontTitle.setValue(frontTitleValue);
                frontTitle.setGravity(EGravity.CENTER);
                frontSide.getComponents().add(frontTitle);

                for (String frontTextValue : frontTextsValues) {
                    Text frontText = new Text();
                    frontText.setValue(frontTextValue);
                    frontText.setGravity(EGravity.START);
                    frontSide.getComponents().add(frontText);
                }

                if (answers != null && !answers.isEmpty()) {
                    frontSide.getComponents().add(new Choice(EChoiceType.MULTIPLE, answers));
                }
            }

            if (card.getSides().size() > 1) {
                Side backSide = card.getSides().get(1);
                backSide.getComponents().clear();

                Title backTitle = new Title();
                backTitle.setGravity(EGravity.CENTER);
                backTitle.setValue(backTitleValue);
                backSide.getComponents().add(backTitle);

                for (String backTextValue : backTextsValues) {
                    Text backText = new Text();
                    backText.setValue(backTextValue);
                    backText.setGravity(EGravity.START);
                    backSide.getComponents().add(backText);
                }

                if (answers != null && !answers.isEmpty()) {
                    backSide.getComponents().add(new Result());
                }
            }

            card.setTags(tags);

            save();
        }
    }

    /**
     * Adds a new template to the current stack
     */
    public void addTemplate(String title, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags) {
        Card template = new Card();
        template.setId(UUID.randomUUID().toString());

        // Title
        template.setTitle(title);

        // Front
        if (!frontTitleValue.isEmpty() || !frontTextsValues.isEmpty()) {
            Side sideFront = new Side();

            if (!frontTitleValue.isEmpty())
                sideFront.getComponents().add(new Title(frontTitleValue));

            for (String s : frontTextsValues) {
                sideFront.getComponents().add(new Text(s));
            }
            template.getSides().add(sideFront);
        }

        // Back
        if (!backTitleValue.isEmpty() || !backTextsValues.isEmpty()) {
            Side sideBack = new Side();

            if (!backTitleValue.isEmpty())
                sideBack.getComponents().add(new Title(backTitleValue));

            for (String s : backTextsValues) {
                sideBack.getComponents().add(new Text(s));
            }
            template.getSides().add(sideBack);
        }

        // Tags
        template.setTags(tags);

        stack.getTemplates().add(template);
        save();
    }

    /**
     * Updates a template
     *
     * @param uuid             id of the template to be updated
     * @param title            title of the template
     * @param frontTitleValue  front title
     * @param frontTextsValues front texts
     * @param backTitleValue   back title
     * @param backTextsValues  back texts
     * @param tags             tags
     */
    public void updateTemplate(String uuid, String title, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags) {
        if (templatesContainsId(uuid)) {
            Card template = getTemplateById(uuid);

            template.setTitle(title);

            if (template.getSides().size() > 0) {
                Side frontSide = template.getSides().get(0);
                frontSide.getComponents().clear();

                Title frontTitle = new Title();
                frontTitle.setValue(frontTitleValue);
                frontTitle.setGravity(EGravity.CENTER);
                frontSide.getComponents().add(frontTitle);

                for (String frontTextValue : frontTextsValues) {
                    Text frontText = new Text();
                    frontText.setValue(frontTextValue);
                    frontText.setGravity(EGravity.START);
                    frontSide.getComponents().add(frontText);
                }
            }

            if (template.getSides().size() > 1) {
                Side backSide = template.getSides().get(1);
                backSide.getComponents().clear();

                Title backTitle = new Title();
                backTitle.setGravity(EGravity.CENTER);
                backTitle.setValue(backTitleValue);
                backSide.getComponents().add(backTitle);

                for (String backTextValue : backTextsValues) {
                    Text backText = new Text();
                    backText.setValue(backTextValue);
                    backText.setGravity(EGravity.START);
                    backSide.getComponents().add(backText);
                }
            }

            template.setTags(tags);

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
        Stack targetStack = stacksController.getLymboById(targetLymboId);
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
        Stack sourceStack = stacksController.getLymboById(sourceLymboId);
        Stack targetStack = stacksController.getLymboById(targetLymboId);
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
        for (Tag tag : tags) {
            if (!tag.containedInList(tagsSelected)) {
                tagsSelected.add(tag);
            }
        }
    }

    public List<Tag> getTagsAll() {
        List<Tag> tagsAll = new ArrayList<>();

        // Consider displayed cards
        for (Card card : getCards()) {
            for (Tag tag : card.getTags()) {
                if (tag != null && !tag.containedInList(tagsAll) && !tag.getValue().equals(getResources().getString(R.string.no_tag)))
                    tagsAll.add(tag);
            }
        }

        // Consider discarded cards
        for (Card card : getCardsDiscarded()) {
            for (Tag tag : card.getTags()) {
                if (tag != null && !tag.containedInList(tagsAll) && !tag.getValue().equals(getResources().getString(R.string.no_tag)))
                    tagsAll.add(tag);
            }
        }

        for (Card template : getTemplates()) {
            for (Tag tag : template.getTags()) {
                if (tag != null && !tag.containedInList(tagsAll) && !tag.getValue().equals(getResources().getString(R.string.no_tag)))
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
            stack = LymboLoader.getLymboFromAsset(App.getContext(), path, false);
        else
            stack = LymboLoader.getLymboFromFile(App.getContext(), new File(path), false);
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

    public boolean cardsContainsId(String uuid) {
        for (Card c : stack.getCards()) {
            if (c.getId() != null && c.getId().equals(uuid)) {
                return true;
            }
        }

        return false;
    }

    public Card getCardById(String uuid) {
        for (Card c : stack.getCards()) {
            if (c.getId() != null && c.getId().equals(uuid)) {
                return c;
            }
        }

        return null;
    }

    public boolean templatesContainsId(String uuid) {
        synchronized (stack.getTemplates()) {
            for (Card t : stack.getTemplates()) {
                if (t.getId() != null && t.getId().equals(uuid)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Card getTemplateById(String uuid) {
        for (Card t : stack.getTemplates()) {
            if (t.getId() != null && t.getId().equals(uuid)) {
                return t;
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
