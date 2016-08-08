package de.interoberlin.lymbo.controller;

import android.content.Context;

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
import de.interoberlin.lymbo.core.model.v1.impl.components.EGravity;
import de.interoberlin.lymbo.core.model.v1.impl.components.Text;
import de.interoberlin.lymbo.core.model.v1.impl.components.Title;
import de.interoberlin.lymbo.model.factories.CardFactory;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboLoader;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboWriter;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.TableCardDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.TableCardEntry;
import de.interoberlin.lymbo.util.ZipUtil;

public class CardsController {
    // <editor-fold defaultstate="collapsed" desc="Members">

    // Database
    private TableCardDatasource datasource;

    // Model
    private Stack stack;
    private List<Card> cards;
    private List<Card> cardsDismissed;
    private List<Card> cardsStashed;
    private List<Card> templates;
    private List<Tag> tagsSelected;

    // Controller
    private StacksController stacksController;

    private boolean displayOnlyFavorites;

    private static CardsController instance;

    // </editor-fold>

    // --------------------
    // Constructors
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    private CardsController() {
    }

    public static CardsController getInstance() {
        if (instance == null) {
            instance = new CardsController();
        }

        return instance;
    }

    // </editor-fold>

    // --------------------
    // Methods
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Methods">

    public void init(Context context) {
        stacksController = StacksController.getInstance();

        cards = new ArrayList<>();
        cardsDismissed = new CopyOnWriteArrayList<>();
        cardsStashed = new CopyOnWriteArrayList<>();
        templates = new CopyOnWriteArrayList<>();
        tagsSelected = new ArrayList<>();
        displayOnlyFavorites = false;

        if (stack != null) {
            datasource = new TableCardDatasource(context);
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
     * @param context context
     * @param card    card to determine visibility of
     * @return whether card is visbible or not
     */
    public boolean isVisible(Context context, Card card) {
        if (card != null) {
            Tag noTag = new Tag(context.getResources().getString(R.string.no_tag));
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
     * @param context context
     * @return number of visible cards
     */
    public int getVisibleCardCount(Context context) {
        int count = 0;

        for (Card card : getCards()) {
            if (isVisible(context, card))
                count++;
        }

        return count;
    }

    /**
     * Writes lymbo object into file
     *
     * @param context context
     */
    public void save(Context context) {
        if (stack.getFile() != null) {

            switch (stack.getFormat()) {
                case LYMBO: {
                    LymboWriter.writeXml(stack, new File(stack.getFile()));
                    break;
                }
                case LYMBOX: {
                    LymboWriter.writeXml(stack, new File(stack.getPath() + "/" + context.getResources().getString(R.string.lymbo_main_file)));
                    ZipUtil.zip(new File(stack.getPath()).listFiles(), new File(stack.getFile()));
                    break;
                }
            }
        }
    }

    /**
     * Resets all cards
     *
     * @param context context
     */
    public void reset(Context context) {
        synchronized (cardsDismissed) {
            for (Card card : cardsDismissed) {
                if (card != null) {
                    retain(context, card);
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
     * @param context context
     */
    public void addCard(Context context, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags, List<Answer> answers) {
        Card card = CardFactory.getInstance().getCard(UUID.randomUUID().toString(), frontTitleValue, frontTextsValues, backTitleValue, backTextsValues, tags, answers);

        stack.getCards().add(card);
        cards.add(card);
        save(context);
    }

    /**
     * Updates a card
     *
     * @param context          context
     * @param id               id of the card to be updated
     * @param frontTitleValue  front title
     * @param frontTextsValues front texts
     * @param backTitleValue   back title
     * @param backTextsValues  back texts
     * @param tags             tags
     * @param answers          answers
     */
    public void updateCard(Context context, String id, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags, List<Answer> answers) {

        if (cardsContainsId(id)) {
            setCardById(id, CardFactory.getInstance().getCard(id, frontTitleValue, frontTextsValues, backTitleValue, backTextsValues, tags, answers));
            save(context);
        }
    }

    /**
     * Adds a new template to the current stack
     *
     * @param context context
     */
    public void addTemplate(Context context, String title, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags) {
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
        save(context);
    }

    /**
     * Updates a template
     *
     * @param context          context
     * @param uuid             id of the template to be updated
     * @param title            title of the template
     * @param frontTitleValue  front title
     * @param frontTextsValues front texts
     * @param backTitleValue   back title
     * @param backTextsValues  back texts
     * @param tags             tags
     */
    public void updateTemplate(Context context, String uuid, String title, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags) {
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

            save(context);
        }
    }

    public void deleteTemplate(Context context, Card template) {
        if (template != null && template.getId() != null) {
            Card t = getTemplateById(template.getId());

            if (t != null)
                stack.getTemplates().remove(t);
        }
        save(context);
    }

    /**
     * Stashes a card
     *
     * @param context context
     * @param card    card
     */
    public void stash(Context context, Card card) {
        card.setRestoring(true);

        getCards().remove(card);
        getCardsStashed().add(card);
        changeCardStateStashed(context, card);
    }

    /**
     * Stashes a card
     *
     * @param pos  position where the card will be stashed
     * @param card card
     */
    public void stash(Context context, int pos, Card card) {
        card.setRestoring(true);

        getCards().remove(card);
        getCardsStashed().add(pos < getCardsStashed().size() ? pos : 0, card);
        changeCardStateStashed(context, card);
    }

    /**
     * Restores a card
     *
     * @param context context
     * @param card    card
     */
    public void restore(Context context, Card card) {
        getCards().add(card);
        getCardsStashed().remove(card);
        changeCardStateNormal(context, card);
    }

    /**
     * Restores a card
     *
     * @param context context
     * @param pos     position where the card will be restored
     * @param card    card
     */
    public void restore(Context context, int pos, Card card) {
        card.setRestoring(true);

        getCards().add(pos < getCards().size() ? pos : 0, card);
        getCardsStashed().remove(card);
        changeCardStateNormal(context, card);
    }

    /**
     * Restores all stashed cards
     *
     * @param context context
     */
    public void restoreAll(Context context) {
        for (Card card : cardsStashed) {
            restore(context, card);
        }
    }

    /**
     * Discards a card from the current stack
     *
     * @param context context
     * @param card    card
     */
    public void discard(Context context, Card card) {
        getCards().remove(card);
        getCardsDiscarded().add(card);
        changeCardStateDiscarded(context, card);
    }

    /**
     * Retains a card that has been removed
     *
     * @param context context
     * @param card    card
     */
    public void retain(Context context, Card card) {
        card.setRestoring(true);

        getCardsDiscarded().remove(card);
        getCards().add(card);
        changeCardStateNormal(context, card);
    }

    /**
     * Retains a card that has been removed
     *
     * @param pos  position where the card will be retained
     * @param card card
     */
    public void retain(Context context, int pos, Card card) {
        card.setRestoring(true);

        getCards().add(pos < getCards().size() ? pos : 0, card);
        getCardsDiscarded().remove(card);
        changeCardStateNormal(context, card);
    }

    private void changeCardStateNormal(Context context, Card card) {
        datasource = new TableCardDatasource(context);
        datasource.open();
        datasource.updateCardStateNormal(card.getId());
        datasource.close();
    }

    private void changeCardStateDiscarded(Context context, Card card) {
        datasource = new TableCardDatasource(context);
        datasource.open();
        datasource.updateCardStateDiscarded(card.getId());
        datasource.close();
    }

    private void changeCardStateStashed(Context context, Card card) {
        datasource = new TableCardDatasource(context);
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
     */
    public void toggleFavorite(Context context, Card card) {
        card.setFavorite(!card.isFavorite());

        datasource = new TableCardDatasource(context);
        datasource.open();
        datasource.updateCardFavorite(card.getId(), card.isFavorite());
        datasource.close();
    }

    /**
     * Copies a card from one stack to another
     *
     * @param context       context
     * @param targetLymboId id of target stack
     * @param cardId        id of card to be copied
     * @param deepCopy      true if the copy shall be deep
     */
    public void copyCard(Context context, String targetLymboId, String cardId, boolean deepCopy) {
        Stack targetStack = stacksController.getLymboById(targetLymboId);
        Card card = getCardById(cardId);

        if (deepCopy)
            card.setId(UUID.randomUUID().toString());

        targetStack.getCards().add(card);
        stacksController.save(context, targetStack);
    }

    /**
     * Moves a card from one stack to another
     *
     * @param context       context
     * @param sourceLymboId id of source stack
     * @param targetLymboId id of target stack
     * @param cardId        id of card to be copied
     */
    public void moveCard(Context context, String sourceLymboId, String targetLymboId, String cardId) {
        Stack sourceStack = stacksController.getLymboById(sourceLymboId);
        Stack targetStack = stacksController.getLymboById(targetLymboId);
        Card card = getCardById(cardId);

        targetStack.getCards().add(card);
        stacksController.save(context, targetStack);

        sourceStack.getCards().remove(card);
        stacksController.save(context, sourceStack);

        getCards().remove(card);
        save(context);
    }

    // </editor-fold>

    // --------------------
    // Util
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Util">

    public void addTagsSelected(List<Tag> tags) {
        for (Tag tag : tags) {
            if (!tag.containedInList(tagsSelected)) {
                tagsSelected.add(tag);
            }
        }
    }

    public List<Tag> getTagsAll(Context context) {
        List<Tag> tagsAll = new ArrayList<>();

        // Consider displayed cards
        for (Card card : getCards()) {
            for (Tag tag : card.getTags()) {
                if (tag != null && !tag.containedInList(tagsAll) && !tag.getValue().equals(context.getResources().getString(R.string.no_tag)))
                    tagsAll.add(tag);
            }
        }

        // Consider discarded cards
        for (Card card : getCardsDiscarded()) {
            for (Tag tag : card.getTags()) {
                if (tag != null && !tag.containedInList(tagsAll) && !tag.getValue().equals(context.getResources().getString(R.string.no_tag)))
                    tagsAll.add(tag);
            }
        }

        for (Card template : getTemplates()) {
            for (Tag tag : template.getTags()) {
                if (tag != null && !tag.containedInList(tagsAll) && !tag.getValue().equals(context.getResources().getString(R.string.no_tag)))
                    tagsAll.add(tag);
            }
        }

        tagsAll.add(new Tag(context.getResources().getString(R.string.no_tag)));

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

    // </editor-fold>

    // --------------------
    // Getters ( Setters
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Getters ( Setters">

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
        for (Card c : stack.getCards()) {
            if (c.getId() != null && c.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public Card getCardById(String id) {
        for (Card c : stack.getCards()) {
            if (c.getId() != null && c.getId().equals(id)) {
                return c;
            }
        }

        return null;
    }

    private void setCardById(String id, Card card) {
        for (Card c : stack.getCards()) {
            if (c.getId() != null && c.getId().equals(id)) {
                c = card;
            }
        }
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

    // </editor-fold>
}
