package de.interoberlin.lymbo.model.persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.card.Side;
import de.interoberlin.lymbo.model.card.components.TextComponent;
import de.interoberlin.lymbo.model.card.components.TitleComponent;

/**
 * This class can be used to write a lymbo object into an xml file
 */
public class LymboWriter {
    private static StringBuilder result;

    public static void writeXml(Lymbo lymbo, File file) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(getXmlString(lymbo));
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an xml formatted string from a lymbo object
     *
     * @param lymbo
     * @return
     */
    public static String getXmlString(Lymbo lymbo) {
        result = new StringBuilder();

        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        appendLymbo("lymbo", lymbo);

        return result.toString();
    }

    /**
     * Appends the lymbo root element to the xml
     *
     * @param tag
     * @param lymbo
     */
    private static void appendLymbo(String tag, Lymbo lymbo) {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("title", lymbo.getTitle());
        attributes.put("subtitle", lymbo.getSubtitle());
        attributes.put("hint", lymbo.getHint());
        attributes.put("image", lymbo.getImage());
        attributes.put("author", lymbo.getAuthor());

        addStartTag(tag, attributes);

        for (Card card : lymbo.getCards()) {
            appendCard("card", card);
        }

        addEndTag(tag);
    }

    /**
     * Appends a card to the xml
     *
     * @param tag
     * @param card
     */
    private static void appendCard(String tag, Card card) {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("id", String.valueOf(card.getId()));
        attributes.put("hint", card.getHint());

        addStartTag(tag, attributes);

        if (card.getFront() != null)
            appendSide("front", card.getFront());

        if (card.getBack() != null)
            appendSide("back", card.getFront());

        addEndTag(tag);
    }

    /**
     * Appends a side (front or back) to the xml
     *
     * @param tag
     * @param side
     */
    private static void appendSide(String tag, Side side) {
        Map<String, String> attributes = new HashMap<String, String>();
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
     * @param tag
     * @param component
     */
    private static void appendTitleComponent(String tag, TitleComponent component) {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("value", component.getValue());
        attributes.put("lines", new Integer(component.getLines()).toString());
        attributes.put("gravity", component.getGravity().toString());

        addTag(tag, attributes);
    }

    /**
     * Appends a text component
     *
     * @param tag
     * @param component
     */
    private static void appendTextComponent(String tag, TextComponent component) {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("value", component.getValue());
        attributes.put("value", component.getValue());
        attributes.put("lines", new Integer(component.getLines()).toString());
        attributes.put("gravity", component.getGravity().toString() );

        addTag(tag, attributes);
    }

    // --------------------
    // Methods - helper
    // --------------------

    /**
     * Adds a value between two tags
     *
     * @param value
     */
    private static void addValue(String value) {
        result.append(value);
    }

    /**
     * Adds a start tag
     *
     * @param tag
     */
    private static void addStartTag(String tag) {
        result.append("\n<" + tag + ">");
    }

    /**
     * Adds a start tag
     *
     * @param tag
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
     * @param tag
     */
    private static void addTag(String tag) {
        result.append("\n<" + tag + " />");
    }

    /**
     * Adds a self-closing tag
     *
     * @param tag
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
     * @param tag
     */
    private static void addEndTag(String tag) {
        result.append("</" + tag + ">\n");
    }

    /**
     * Appends a simple tag to the xml
     *
     * @param tag
     * @param text
     */
    private static void appendTag(String tag, String text) {
        addStartTag(tag);
        addValue(text);
        addEndTag(tag);
    }
}
