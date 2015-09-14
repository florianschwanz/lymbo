package de.interoberlin.lymbo.model.persistence.filesystem;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.interoberlin.lymbo.model.card.Displayable;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Stack;
import de.interoberlin.lymbo.model.card.Side;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.model.card.aspects.LanguageAspect;
import de.interoberlin.lymbo.model.card.components.TextComponent;
import de.interoberlin.lymbo.model.card.components.TitleComponent;

/**
 * This class can be used to write a lymbo object into an xml file
 */
public class LymboWriter {
    private static StringBuilder result;

    public static void createLymboSavePath(File path) {
        if (!path.exists()) {
            path.mkdirs();
        }
    }

    public static void writeXml(Stack stack, File file) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(getXmlString(stack));
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an xml formatted string from a lymbo object
     *
     * @param stack lymbo file to get string representation for
     * @return string representing the lymbo
     */
    public static String getXmlString(Stack stack) {
        result = new StringBuilder();

        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        appendLymbo("lymbo", stack);

        return result.toString();
    }

    /**
     * Appends the lymbo root element to the xml
     *
     * @param tag   tag name to appended
     * @param stack lymbo file to append
     */
    private static void appendLymbo(String tag, Stack stack) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("id", stack.getId());
        attributes.put("creationDate", stack.getCreationDate());
        attributes.put("modificationDate", stack.getModificationDate());
        attributes.put("title", stack.getTitle());
        attributes.put("subtitle", stack.getSubtitle());
        attributes.put("hint", stack.getHint());
        attributes.put("image", stack.getImage());
        attributes.put("author", stack.getAuthor());
        attributes.put("tags", getTagsList(stack.getTags()));

        addStartTag(tag, attributes);

        appendLanguageAspects("language", stack.getLanguageAspect());

        for (Card card : stack.getCards()) {
            if (card != null)
                appendCard("card", card);
        }

        addEndTag(tag);
    }

    /**
     * Appends a card to the xml
     *
     * @param tag  tag name to appended
     * @param card card to appended
     */
    private static void appendCard(String tag, Card card) {
        Map<String, String> attributes = new HashMap<>();
        if (card.getId() != null)
            attributes.put("id", String.valueOf(card.getId()));
        else
            attributes.put("id", UUID.randomUUID().toString());

        attributes.put("edit", String.valueOf(card.isEdit()));
        attributes.put("hint", escape(card.getHint()));
        attributes.put("tags", getTagsList(card.getTags()));

        addStartTag(tag, attributes);

        for (Side s : card.getSides()) {
            appendSide("side", s);
        }

        addEndTag(tag);
    }

    /**
     * Appends a side (front or back) to the xml
     *
     * @param tag  tag name to appended
     * @param side side to appended
     */
    private static void appendSide(String tag, Side side) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("color", side.getColor());

        addStartTag(tag, attributes);

        for (Displayable component : side.getComponents()) {
            if (component instanceof TitleComponent) {
                appendTitleComponent("title", (TitleComponent) component);
            } else if (component instanceof TextComponent) {
                appendTextComponent("text", (TextComponent) component);
            }
        }

        addEndTag(tag);
    }

    /**
     * Appends a title component
     *
     * @param tag       tag name to appended
     * @param component component to appended
     */
    private static void appendTitleComponent(String tag, TitleComponent component) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("value", escape(component.getValue()));
        attributes.put("lines", Integer.toString(component.getLines()));
        attributes.put("gravity", component.getGravity().toString());

        addTag(tag, attributes);
    }

    /**
     * Appends a text component
     *
     * @param tag       tag name to appended
     * @param component component to appended
     */
    private static void appendTextComponent(String tag, TextComponent component) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("value", escape(component.getValue()));
        attributes.put("lines", Integer.toString(component.getLines()));
        attributes.put("gravity", component.getGravity().toString());

        addTag(tag, attributes);
    }

    /**
     * Appends language component to lymbo
     *
     * @param tag            tag name to appended
     * @param languageAspect language aspect to appended
     */
    private static void appendLanguageAspects(String tag, LanguageAspect languageAspect) {
        if (languageAspect != null && languageAspect.getFrom() != null && languageAspect.getTo() != null) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("from", languageAspect.getFrom().getLangCode());
            attributes.put("to", languageAspect.getTo().getLangCode());

            addTag(tag, attributes);
        }
    }

    // --------------------
    // Methods - Helper
    // --------------------

    /**
     * Adds a value between two tags
     *
     * @param value value to append
     */
    private static void addValue(String value) {
        result.append(value);
    }

    /**
     * Adds a start tag
     *
     * @param tag tag to appended
     */
    private static void addStartTag(String tag) {
        result.append("\n<" + tag + ">");
    }

    /**
     * Adds a start tag with attributes
     *
     * @param tag        tag name to appended
     * @param attributes attributes to appended
     */
    private static void addStartTag(String tag, Map<String, String> attributes) {
        result.append("\n<" + tag);

        for (Map.Entry<String, String> e : attributes.entrySet()) {
            if (e.getValue() != null) {
                result.append("\n " + e.getKey() + "=\"" + e.getValue() + "\"");
            }
        }

        result.append(">");
    }

    /**
     * Adds a self-closing tag
     *
     * @param tag tag to appended
     */
    private static void addTag(String tag) {
        result.append("\n<" + tag + " />");
    }

    /**
     * Add a self-closing tag with attributes
     *
     * @param tag        tag name to appended
     * @param attributes attributes to appended
     */
    private static void addTag(String tag, Map<String, String> attributes) {
        result.append("\n<" + tag);

        for (Map.Entry<String, String> e : attributes.entrySet()) {
            if (e.getValue() != null) {
                result.append("\n " + e.getKey() + "=\"" + e.getValue() + "\"");
            }
        }

        result.append(" />");
    }

    /**
     * Adds an end tag
     *
     * @param tag tag name to appended
     */
    private static void addEndTag(String tag) {
        result.append("</" + tag + ">\n");
    }

    /**
     * Appends a simple tag to the xml
     *
     * @param tag  tag name to appended
     * @param text text value of the tag
     */
    private static void appendTag(String tag, String text) {
        addStartTag(tag);
        addValue(text);
        addEndTag(tag);
    }

    private static String getTagsList(List<Tag> tags) {
        String tagsList = "";

        for (Tag t : tags) {
            tagsList +=  t.getName() + " ";
        }

        // Remove trailing blank
        if (tagsList.length() > 0 && tagsList.charAt(tagsList.length() - 1) == ' ') {
            tagsList = tagsList.substring(0, tagsList.length() - 1);
        }

        return tagsList;
    }

    private static String escape(String input) {
        return StringEscapeUtils.escapeXml(input);
    }
}
