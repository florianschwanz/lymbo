package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.List;

public class Card {
    private int id;
    private List<Side> sides;

    private String hint;
    private Tag chapter;
    private List<Tag> tags;

    private boolean flip = true;
    private boolean edit = false;

    private boolean discarded = false;
    private int sideVisible = 0;

    // -------------------------
    // Constructors
    // -------------------------

    public Card() {
        sides = new ArrayList<>();
        tags = new ArrayList<>();
    }


    public boolean matchesChapter(List<Tag> cs) {
        for (Tag c : cs) {
            if (chapter == null && c.getName().equals("< no chapter >")) {
                return true;
            } else if (chapter.getName().equals(c.getName()) && c.isChecked()) {
                return true;
            }
        }

        return false;
    }

    public boolean matchesTag(List<Tag> ts) {
        for (Tag t : ts) {
            if (tags.isEmpty()) {
                if (t.getName().equals("< no tag >"))
                    return true;
            } else {
                for (Tag tag : tags) {
                    if (t.getName().equals(tag.getName()) && t.isChecked())
                        return true;
                }
            }
        }
        return false;
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public boolean isDiscarded() {
        return discarded;
    }

    public void setDiscarded(boolean discarded) {
        this.discarded = discarded;
    }

    public int getSideVisible() {
        return sideVisible;
    }

    public void setSideVisible(int sideVisible) {
        this.sideVisible = sideVisible;
    }

}
