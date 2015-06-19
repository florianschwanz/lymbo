package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.controller.LymbosController;
import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.components.Answer;
import de.interoberlin.lymbo.model.card.components.ChoiceComponent;

public class Card {
    private LymbosController lymbosController;

    private String id;
    private List<Side> sides;

    private String hint;
    private Tag chapter;
    private List<Tag> tags;

    private boolean edit;
    private int sideVisible;
    private boolean discarded;

    private boolean revealed = false;
    private boolean restoring = false;
    private boolean noteExpanded = false;

    // -------------------------
    // Constructors
    // -------------------------

    public Card() {
        init();
    }

    // -------------------------
    // Methods
    // -------------------------

    private void init() {
        lymbosController = LymbosController.getInstance();

        id = UUID.randomUUID().toString();
        sides = new ArrayList<>();
        hint = null;
        chapter = null;
        tags = new ArrayList<>();
        edit = false;
        sideVisible = 0;
        discarded = false;
    }

    public boolean matchesChapter(List<Tag> cs) {
        String noChapter = lymbosController.getContext().getResources().getString(R.string.no_chapter);

        if (cs == null || cs.isEmpty()) {
            return true;
        } else {
            for (Tag c : cs) {
                if (chapter == null && c.getName().equals(noChapter) || chapter != null && chapter.getName().equals(c.getName()) && c.isChecked()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean matchesTag(List<Tag> ts) {
        String noTag = lymbosController.getContext().getResources().getString(R.string.no_tag);

        if (ts == null || ts.isEmpty()) {
            return true;
        } else {
            for (Tag t : ts) {
                if ((tags == null || tags.isEmpty()) && (t.getName().equals(noTag))) {
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

    /**
     * Brings card into initial state
     */
    public void reset() {
        sideVisible = 0;
        discarded = false;

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

    public boolean isDiscarded() {
        return discarded;
    }

    public void setDiscarded(boolean discarded) {
        this.discarded = discarded;
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
