package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.App;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.components.Answer;
import de.interoberlin.lymbo.model.card.components.ChoiceComponent;
import de.interoberlin.lymbo.model.card.components.TextComponent;
import de.interoberlin.lymbo.model.card.components.TitleComponent;
import de.interoberlin.lymbo.model.card.enums.EGravity;

public class Card {
    private String id;
    private List<Side> sides;

    private String hint;
    private Tag chapter;
    private List<Tag> tags;

    private boolean flip;
    private boolean edit;
    private int sideVisible;

    private boolean revealed = false;
    private boolean restoring = false;
    private boolean noteExpanded = false;

    // -------------------------
    // Constructors
    // -------------------------

    public Card() {
        init();
    }

    public Card(String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags) {
        init();

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

        getSides().add(frontSide);
        getSides().add(backSide);
        setFlip(true);

        setTags(tags);
    }

    // -------------------------
    // Methods
    // -------------------------

    private void init() {
        id = UUID.randomUUID().toString();
        sides = new ArrayList<>();
        hint = null;
        chapter = null;
        tags = new ArrayList<>();
        edit = false;
        sideVisible = 0;
    }

    /**
     * Brings card into initial state
     */
    public void reset() {
        sideVisible = 0;

        for (Side side : getSides()) {
            for (Displayable component : side.getComponents()) {
                if (component instanceof ChoiceComponent) {
                    for (Answer answer : ((ChoiceComponent) component).getAnswers()) {
                        answer.setSelected(false);
                    }
                }
            }
        }
    }

    /**
     * Determines whether this card's chapters is contained in a given list
     *
     * @param cs list of chapter tags
     * @return
     */
    public boolean matchesChapter(List<Tag> cs) {
        String noChapter = App.getContext().getResources().getString(R.string.no_chapter);

        if (cs == null || cs.isEmpty()) {
            return true;
        } else {
            for (Tag c : cs) {
                if (chapter == null && c.isChecked() && c.getName().equals(noChapter) || chapter != null && chapter.getName().equals(c.getName()) && c.isChecked()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines whether at least one of this card's tags matches a given list of tags
     *
     * @param ts list of tags
     * @return
     */
    public boolean matchesTag(List<Tag> ts) {
        String noTag = App.getContext().getResources().getString(R.string.no_tag);

        if (ts == null || ts.isEmpty()) {
            return true;
        } else {
            for (Tag t : ts) {
                if ((tags == null || tags.isEmpty()) && t.isChecked() && t.getName().equals(noTag)) {
                    return true;
                } else {
                    for (Tag tag : tags) {
                        if (t.getName().equals(tag.getName()) && t.isChecked())
                            return true;
                    }
                }
            }
        }

        return false;
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Side> getSides() {
        return sides;
    }

    public void setSides(List<Side> sides) {
        this.sides = sides;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Tag getChapter() {
        return chapter;
    }

    public void setChapter(Tag chapter) {
        this.chapter = chapter;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public int getSideVisible() {
        return sideVisible;
    }

    public void setSideVisible(int sideVisible) {
        this.sideVisible = sideVisible;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isRestoring() {
        return restoring;
    }

    public void setRestoring(boolean restoring) {
        this.restoring = restoring;
    }

    public boolean isNoteExpanded() {
        return noteExpanded;
    }

    public void setNoteExpanded(boolean noteExpanded) {
        this.noteExpanded = noteExpanded;
    }
}
