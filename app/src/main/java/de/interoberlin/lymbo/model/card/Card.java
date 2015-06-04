package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.components.Answer;
import de.interoberlin.lymbo.model.card.components.ChoiceComponent;

public class Card {
    private String id = "";
    private List<Side> sides = new ArrayList<>();

    private String hint = null;
    private Tag chapter = null;
    private List<Tag> tags = new ArrayList<>();

    private boolean flip = true;
    private boolean edit = false;

    private int sideVisible = 0;

    private boolean discarded = false;

    // -------------------------
    // Constructors
    // -------------------------

    public Card() {
    }

    // -------------------------
    // Constructors
    // -------------------------

    public boolean matchesChapter(List<Tag> cs) {
        if (chapter == null)
            return true;

        if (cs.isEmpty()) {
            return true;
        } else {
            for (Tag c : cs) {
                if (chapter == null && c.getName().equals("no chapter") || chapter != null && chapter.getName().equals(c.getName()) && c.isChecked()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean matchesTag(List<Tag> ts) {
        if (tags == null || tags.isEmpty())
            return true;

        if (ts.isEmpty()) {
            return true;
        } else {
            for (Tag t : ts) {
                if (tags.isEmpty()) {
                    if (t.getName().equals("no tag"))
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

    public boolean isDiscarded() {
        return discarded;
    }

    public void setDiscarded(boolean discarded) {
        this.discarded = discarded;
    }
}
