package de.interoberlin.lymbo.util;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;
import java.util.Map;

import de.interoberlin.lymbo.model.card.Tag;

public class XmlUtil {
    // --------------------
    // Methods
    // --------------------

    /**
     * Adds a start tag with attributes
     *
     * @param tag        tag name to appended
     * @param attributes attributes to appended
     */
    public static String addStartTag(String tag, Map<String, String> attributes) {
        StringBuilder result = new StringBuilder();

        result.append("\n<").append(tag);

        for (Map.Entry<String, String> e : attributes.entrySet()) {
            if (e.getValue() != null) {
                result.append("\n ").append(e.getKey()).append("=\"").append(e.getValue()).append("\"");
            }
        }

        result.append(">");

        return result.toString();
    }

    /**
     * Add a self-closing tag with attributes
     *
     * @param tag        tag name to appended
     * @param attributes attributes to appended
     */
    public static String addTag(String tag, Map<String, String> attributes) {
        StringBuilder result = new StringBuilder();

        result.append("\n<").append(tag);

        for (Map.Entry<String, String> e : attributes.entrySet()) {
            if (e.getValue() != null) {
                result.append("\n ").append(e.getKey()).append("=\"").append(e.getValue()).append("\"");
            }
        }

        result.append(" />");

        return result.toString();
    }

    /**
     * Adds an end tag
     *
     * @param tag tag name to appended
     */
    public static String addEndTag(String tag) {
        StringBuilder result = new StringBuilder();
        result.append("</");
        result.append(tag);
        result.append(">\n");

        return result.toString();
    }

    public static String getTagsList(List<Tag> tags) {
        String tagsList = "";

        for (Tag t : tags) {
            tagsList += t.getName() + " ";
        }

        // Remove trailing blank
        if (tagsList.length() > 0 && tagsList.charAt(tagsList.length() - 1) == ' ') {
            tagsList = tagsList.substring(0, tagsList.length() - 1);
        }

        return tagsList;
    }

    public static String escape(String input) {
        return StringEscapeUtils.escapeXml(input);
    }
}
