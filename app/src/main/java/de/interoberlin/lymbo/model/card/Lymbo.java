package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Lymbo {
    private String path;
    private boolean asset;

    private String id;
    private String creationDate;
    private String modificationDate;
    private String title;
    private String subtitle;
    private String hint;
    private String image;
    private String author;
    private List<Card> cards;

    private boolean containsGeneratedIds;

    private String error;

    // -------------------------
    // Constructors
    // -------------------------

    public Lymbo() {
        init();
    }

    // -------------------------
    // Methods
    // -------------------------

    private void init() {
        id = UUID.randomUUID().toString();
        creationDate = new Date().toString();
        modificationDate = new Date().toString();
        title = "";
        subtitle = "";
        hint = null;
        image = null;
        author = "";
        cards = new ArrayList<>();
        error = "";
    }

    /**
     * Returns a list of all tags used in this lymbo
     *
     * @return a list of tags
     */
    public List<Tag> getTags() {
        List<Tag> tags = new ArrayList<>();

        for (Card c : getCards()) {
            if (c != null) {
                for (Tag t : c.getTags()) {
                    if (!containsTag(tags, t)) {
                        tags.add(t);
                    }
                }
            }
        }

        Collections.sort(tags, new Comparator<Tag>() {
            @Override
            public int compare(Tag t1, Tag t2) {
                return t1.getName().compareTo(t2.getName());
            }
        });

        return tags;
    }

    /**
     * Returns a list of all chapters used in this lymbo
     *
     * @return a list of tags representing the chapters found
     */
    public List<Tag> getChapters() {
        List<Tag> chapters = new ArrayList<>();

        for (Card c : getCards()) {
            if (c != null && c.getChapter() != null && !containsTag(chapters, c.getChapter())) {
                chapters.add(c.getChapter());
            }
        }

        Collections.sort(chapters, new Comparator<Tag>() {
            @Override
            public int compare(Tag t1, Tag t2) {
                return t1.getName().compareTo(t2.getName());
            }
        });

        return chapters;
    }

    private boolean containsTag(List<Tag> tags, Tag tag) {
        for (Tag t : tags) {
            if (t.getName().equalsIgnoreCase(tag.getName()))
                return true;
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAsset() {
        return asset;
    }

    public void setAsset(boolean asset) {
        this.asset = asset;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public boolean isContainsGeneratedIds() {
        return containsGeneratedIds;
    }

    public void setContainsGeneratedIds(boolean containsGeneratedIds) {
        this.containsGeneratedIds = containsGeneratedIds;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
