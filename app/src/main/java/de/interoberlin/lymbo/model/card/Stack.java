package de.interoberlin.lymbo.model.card;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.card.aspects.LanguageAspect;
import de.interoberlin.lymbo.util.XmlUtil;

public class Stack {
    public static final String TAG = "lymbo";

    private String file;
    private String path;
    private boolean asset;
    private EFormat format;

    private String id;
    private String creationDate;
    private String modificationDate;
    private String title;
    private String subtitle;
    private String hint;
    private String image;
    private EImageFormat imageFormat;
    private String author;
    private List<Card> cards;
    private List<Card> templates;

    private List<Tag> tags;

    private LanguageAspect languageAspect;

    private boolean containsGeneratedIds;
    private String error;

    // -------------------------
    // Constructors
    // -------------------------

    public Stack() {
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
        imageFormat = EImageFormat.BASE64;
        author = "";
        cards = new ArrayList<>();
        templates = new ArrayList<>();
        error = "";

        tags = new ArrayList<>();
    }

    /**
     * Determines whether at least one of this stacks's tags matches a given list of tags
     *
     * @param ts list of tags
     * @return whether the cards matches one of the lists' tags
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
                    if (tags != null) {
                        for (Tag tag : tags) {
                            if (t.getName().equals(tag.getName()) && t.isChecked())
                                return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        // Attributes
        Map<String, String> attributes = new HashMap<>();
        attributes.put("id", XmlUtil.escape(getId()));
        attributes.put("creationDate", getCreationDate());
        attributes.put("modificationDate", getModificationDate());
        attributes.put("title", XmlUtil.escape(title));
        attributes.put("subtitle", getSubtitle());
        attributes.put("hint", XmlUtil.escape(hint));
        attributes.put("image", XmlUtil.escape(image));
        attributes.put("author", XmlUtil.escape(author));
        attributes.put("tags", XmlUtil.getTagsList(getTags()));

        result.append(addStartTag(TAG, attributes));

        // Sub elements
        result.append(languageAspect.toString());

        for (Card template : templates) {
            if (template != null)
                result.append(template.toString("template"));
        }

        for (Card card : cards) {
            if (card != null)
                result.append(card.toString("card"));
        }

        result.append(XmlUtil.addEndTag(TAG));

        return result.toString();
    }

    /**
     * Adds a start tag with attributes
     *
     * @param tag        tag name to appended
     * @param attributes attributes to appended
     */
    private StringBuilder addStartTag(String tag, Map<String, String> attributes) {
        StringBuilder result = new StringBuilder();

        result.append("\n<").append(tag);

        for (Map.Entry<String, String> e : attributes.entrySet()) {
            if (e.getValue() != null) {
                result.append("\n ").append(e.getKey()).append("=\"").append(e.getValue()).append("\"");
            }
        }

        result.append(">");

        return result;
    }

    // -------------------------
    // Getters / Setters
    // -------------------------

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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

    public EFormat getFormat() {
        return format;
    }

    public void setFormat(EFormat format) {
        this.format = format;
    }

    public boolean isLymbo() {
        return getFormat() == EFormat.LYMBO;
    }

    public boolean isLymbox() {
        return getFormat() == EFormat.LYMBOX;
    }

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

    public EImageFormat getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(EImageFormat imageFormat) {
        this.imageFormat = imageFormat;
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

    public List<Card> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Card> templates) {
        this.templates = templates;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public LanguageAspect getLanguageAspect() {
        return languageAspect;
    }

    public void setLanguageAspect(LanguageAspect languageAspect) {
        this.languageAspect = languageAspect;
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
