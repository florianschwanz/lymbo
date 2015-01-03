package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.List;

public class Card {
    private int id;
    private Side front;
    private Side back;

    private Tag chapter;
    private List<Tag> tags = new ArrayList<>();

    private boolean flip = true;
    private boolean edit = false;

    private boolean discarded = false;

    // -------------------------
    // Constructors
    // -------------------------

    public Card() {
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

    public Side getFront() {
        return front;
    }

    public void setFront(Side front) {
        this.front = front;
    }

    public Side getBack() {
        return back;
    }

    public void setBack(Side back) {
        this.back = back;
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
}
