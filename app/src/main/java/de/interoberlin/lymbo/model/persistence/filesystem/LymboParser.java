package de.interoberlin.lymbo.model.persistence.filesystem;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.core.model.v1.impl.Answer;
import de.interoberlin.lymbo.core.model.v1.impl.Card;
import de.interoberlin.lymbo.core.model.v1.impl.Choice;
import de.interoberlin.lymbo.core.model.v1.impl.Component;
import de.interoberlin.lymbo.core.model.v1.impl.EChoiceType;
import de.interoberlin.lymbo.core.model.v1.impl.EGravity;
import de.interoberlin.lymbo.core.model.v1.impl.EImageFormat;
import de.interoberlin.lymbo.core.model.v1.impl.ESVGFormat;
import de.interoberlin.lymbo.core.model.v1.impl.Image;
import de.interoberlin.lymbo.core.model.v1.impl.Language;
import de.interoberlin.lymbo.core.model.v1.impl.Result;
import de.interoberlin.lymbo.core.model.v1.impl.Side;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.core.model.v1.impl.Text;
import de.interoberlin.lymbo.core.model.v1.impl.Title;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.TableCardDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.TableCardEntry;
import de.interoberlin.mate.lib.model.Log;

public class LymboParser {
    public static final String TAG = LymboParser.class.getCanonicalName();
    private static LymboParser instance;

    private boolean onlyTopLevel;
    private boolean containsGeneratedIds;

    // --------------------
    // Constructors
    // --------------------

    private LymboParser() {
    }

    public static LymboParser getInstance() {
        if (instance == null) {
            instance = new LymboParser();
        }

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

    /**
     * Parses xml file an returns a stack
     *
     * @param is           input stream representing lymbo file
     * @param path         resource path
     * @param onlyTopLevel determines if only the top level element shall be parsed
     * @return xmlLymbo
     * @throws IOException
     */
    public Stack parse(InputStream is, File path, boolean onlyTopLevel) throws IOException {
        this.onlyTopLevel = onlyTopLevel;
        containsGeneratedIds = false;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            parser.nextTag();

            Stack stack = parseLymbo(parser, path, onlyTopLevel);
            stack.setContainsGeneratedIds(containsGeneratedIds);

            return stack;
        } catch (XmlPullParserException xmlppe) {
            Log.e(TAG, xmlppe.getMessage());
            Stack stack = new Stack();
            stack.setError(xmlppe.toString());
            return stack;
        } finally {
            is.close();
        }
    }

    /**
     * Parses a stack
     *
     * @param parser       the XmlPullParser
     * @param path         resource path
     * @param onlyTopLevel determines if only the top level element shall be parsed
     * @return stack
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Stack parseLymbo(XmlPullParser parser, File path, boolean onlyTopLevel) throws XmlPullParserException, IOException {
        String name;

        parser.require(XmlPullParser.START_TAG, null, "lymbo");

        // Create element
        Stack stack = new Stack();

        // Read attributes
        String id = parser.getAttributeValue(null, "id");
        String creationDate = parser.getAttributeValue(null, "creationDate");
        String modificationDate = parser.getAttributeValue(null, "modificationDate");
        String title = parser.getAttributeValue(null, "title");
        String subtitle = parser.getAttributeValue(null, "subtitle");
        String image = parser.getAttributeValue(null, "image");
        String imageFormat = parser.getAttributeValue(null, "imageFormat");
        String author = parser.getAttributeValue(null, "author");
        String tags = parser.getAttributeValue(null, "tags");

        // Read sub elements
        List<Card> cards = new ArrayList<>();
        List<Card> templates = new ArrayList<>();
        Language language = new Language();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();

            if (!onlyTopLevel && name.equals("card")) {
                cards.add(parseCard(parser, "card", path));
            } else if (!onlyTopLevel && name.equals("template")) {
                templates.add(parseCard(parser, "template", path));
            } else if (name.equals("language")) {
                language = parseLanguage(parser);
            } else {
                skip(parser);
            }
        }

        // Indicate newly generated id
        if (!onlyTopLevel && id == null) {
            containsGeneratedIds = true;
        }

        // Fill element
        if (id != null)
            stack.setId(id);
        if (creationDate != null)
            stack.setCreationDate(parseDate(creationDate));
        if (modificationDate != null)
            stack.setModificationDate(parseDate(modificationDate));
        if (title != null)
            stack.setTitle(title);
        if (subtitle != null)
            stack.setSubtitle(subtitle);
        if (image != null)
            stack.setImage(image);
        if (imageFormat != null)
            stack.setImageFormat(parseImageFormat(imageFormat));
        if (author != null)
            stack.setAuthor(author);
        if (tags != null)
            stack.setTags(parseTags(tags));

        stack.setCards(cards);
        stack.setTemplates(templates);
        stack.setLanguage(language);

        return stack;
    }

    private EImageFormat parseImageFormat(String imageFormat) {
        switch (imageFormat) {
            case "ref": {
                return EImageFormat.REF;
            }
            case "base64": {
                return EImageFormat.BASE_64;
            }
        }
        return null;
    }

    private ESVGFormat parseSVGFormat(String svgFormat) {
        switch (svgFormat) {
            case "ref": {
                return ESVGFormat.REF;
            }
            case "plain": {
                return ESVGFormat.PLAIN;
            }
        }

        return null;
    }

    /**
     * Returns a card which contains one or two sides
     *
     * @param parser the XmlPullParser
     * @param path   resource path
     * @return card Card object
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Card parseCard(XmlPullParser parser, String tag, File path) throws XmlPullParserException, IOException {
        String name;

        parser.require(XmlPullParser.START_TAG, null, tag);

        // Create element
        Card card = new Card();

        // Read attributes
        String id = parser.getAttributeValue(null, "id");
        String title = parser.getAttributeValue(null, "title");
        String edit = parser.getAttributeValue(null, "edit");
        String hint = parser.getAttributeValue(null, "hint");
        String tags = parser.getAttributeValue(null, "tags");

        // Read sub elements
        List<Side> sides = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();

            Side side = null;
            switch (name) {
                case "front":
                    side = parseSide(parser, "front", path);
                    break;
                case "back":
                    side = parseSide(parser, "back", path);
                    break;
                case "side":
                    side = parseSide(parser, "side", path);
                    break;
                default:
                    skip(parser);
            }

            if (side != null)
                sides.add(side);
        }

        // Indicate newly generated id
        if (!onlyTopLevel && id == null) {
            containsGeneratedIds = true;
        }

        // Fill element
        card.setSides(sides);

        if (id != null)
            card.setId(id);
        if (title != null)
            card.setTitle(title);
        if (edit != null)
            card.setEdit(Boolean.parseBoolean(edit));
        if (hint != null)
            card.setHint(hint);
        if (tags != null)
            card.setTags(parseTags(tags));

        // Read additional information from database
        TableCardDatasource datasource = new TableCardDatasource(App.getContext());
        datasource.open();
        TableCardEntry entry = datasource.getEntryByUuid(card.getId());
        datasource.close();

        card.setFavorite(entry != null && entry.isFavorite());

        return card;
    }

    /**
     * Returns a side of a card
     *
     * @param parser the XmlPullParser
     * @param tag    tag
     * @param path   resource path
     * @return xmlSide
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Side parseSide(XmlPullParser parser, String tag, File path) throws XmlPullParserException, IOException {
        String name;
        parser.require(XmlPullParser.START_TAG, null, tag);

        // Create element
        Side side = new Side();

        // Read attributes
        String color = parser.getAttributeValue(null, "color");
        String flip = parser.getAttributeValue(null, "flip");

        // Read sub elements
        List<Component> components = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();

            switch (name) {
                case "title":
                    components.add(parseTitleComponent(parser));
                    break;
                case "text":
                    components.add(parseTextComponent(parser));
                    break;
                case "choice":
                    components.add(parseChoiceComponent(parser));
                    break;
                /*
                case "svg":
                    components.add(parseSVGComponent(parser, color, path));
                    break;
                   */
                case "image":
                    components.add(parseImageComponent(parser, path));
                    break;
                case "result":
                    components.add(parseResultComponent(parser));
                    break;
                default:
                    skip(parser);
            }
        }

        // Fill element
        if (!components.isEmpty())
            side.setComponents(components);
        if (color != null)
            side.setColor(color);
        if (flip != null)
            side.setFlip(Boolean.parseBoolean(flip));

        return side;
    }

    /**
     * Returns a title component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Title parseTitleComponent(XmlPullParser parser) throws XmlPullParserException, IOException {
        String name;
        parser.require(XmlPullParser.START_TAG, null, "title");

        // Create element
        Title component = new Title();

        // Read attributes
        String value = parser.getAttributeValue(null, "value");
        String lines = parser.getAttributeValue(null, "lines");
        String gravity = parser.getAttributeValue(null, "gravity");

        // Read sub elements
        Map<String, String> translations = new HashMap<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();

            switch (name) {
                case "translation":
                    Map.Entry<String, String> translation = parseTranslation(parser);
                    translations.put(translation.getKey(), translation.getValue());
                    break;
                default:
                    skip(parser);
            }
        }

        // Fill element
        if (value != null)
            component.setValue(value);
        if (lines != null)
            component.setLines(parseLines(lines));
        if (gravity != null)
            component.setGravity(parseGravity(gravity));
        if (!translations.isEmpty())
            component.setTranslations(translations);

        return component;
    }

    /**
     * Returns a text component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Text parseTextComponent(XmlPullParser parser) throws XmlPullParserException, IOException {
        String name;
        parser.require(XmlPullParser.START_TAG, null, "text");

        // Create element
        Text component = new Text();

        // Read attributes
        String value = parser.getAttributeValue(null, "value");
        String lines = parser.getAttributeValue(null, "lines");
        String gravity = parser.getAttributeValue(null, "gravity");
        String style = parser.getAttributeValue(null, "style");

        // Read sub elements
        Map<String, String> translations = new HashMap<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();

            switch (name) {
                case "translation":
                    Map.Entry<String, String> translation = parseTranslation(parser);
                    translations.put(translation.getKey(), translation.getValue());
                    break;
                default:
                    skip(parser);
            }
        }

        // Fill element
        if (value != null)
            component.setValue(value);
        if (lines != null)
            component.setLines(parseLines(lines));
        if (gravity != null)
            component.setGravity(parseGravity(gravity));
        if (style != null)
            component.setStyle(style);
        if (!translations.isEmpty())
            component.setTranslations(translations);

        return component;
    }

    /**
     * Returns a result component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Result parseResultComponent(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "result");

        // Create element
        Result component = new Result();

        // Read attributes

        // Read sub elements
        parser.next();
        /*
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
        }
        */

        // Fill element

        return component;
    }

    /**
     * Returns a result component
     *
     * @param parser the XmlPullParser
     * @param path   resource path
     * @return xmlSide
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Image parseImageComponent(XmlPullParser parser, File path) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "image");

        // Create element
        Image component = new Image();

        // Read attributes
        String value = parser.getAttributeValue(null, "value");
        String format = parser.getAttributeValue(null, "format");

        // Read sub elements
        parser.next();
        /*
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
        }
        */

        // Fill element
        if (value != null)
            component.setValue(value);
        if (format != null)
            component.setFormat(parseImageFormat(format));
        if (path != null)
            component.setResourcePath(path);

        return component;
    }

    /**
     * Returns a choice component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Choice parseChoiceComponent(XmlPullParser parser) throws XmlPullParserException, IOException {
        String name;
        parser.require(XmlPullParser.START_TAG, null, "choice");

        // Create element
        Choice component = new Choice();

        // Read attributes
        String type = parser.getAttributeValue(null, "type");

        // Read sub elements
        List<Answer> answers = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            name = parser.getName();

            if (name.equals("answer")) {
                answers.add(parseAnswer(parser));
            } else {
                skip(parser);
            }
        }

        // Fill element
        if (type != null)
            component.setChoiceType(parseChoiceType(type));
        if (!answers.isEmpty())
            component.setAnswers(answers);

        return component;
    }

    /**
     * Returns an answer component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Answer parseAnswer(XmlPullParser parser) throws XmlPullParserException, IOException {
        String name;
        parser.require(XmlPullParser.START_TAG, null, "answer");

        // Create element
        Answer answer = new Answer();

        // Read attributes
        String value = parser.getAttributeValue(null, "value");
        String correct = parser.getAttributeValue(null, "correct");

        // Read sub elements
        Map<String, String> translations = new HashMap<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();

            switch (name) {
                case "translation":
                    Map.Entry<String, String> translation = parseTranslation(parser);
                    translations.put(translation.getKey(), translation.getValue());
                    break;
                default:
                    skip(parser);
            }
        }

        // Fill element
        if (value != null)
            answer.setValue(value);
        if (correct != null)
            answer.setCorrect(Boolean.parseBoolean(correct));
        if (!translations.isEmpty())
            answer.setTranslations(translations);

        return answer;
    }

    /**
     * Returns an svg component
     *
     * @param parser the XmlPullParser
     * @param color  color
     * @param path   resource path
     * @return xmlSide
     * @throws XmlPullParserException
     * @throws IOException
     */
    /*
    private SVGComponent parseSVGComponent(XmlPullParser parser, String color, File path) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "svg");

        // Create element
        SVGComponent component = new SVGComponent();

        // Read attributes
        String value = parser.getAttributeValue(null, "value");
        String format = parser.getAttributeValue(null, "format");

        // Read sub elements
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
        }

        // SVG svg = SvgParser.getInstance().parseSVG(parser);

        // Fill element
        if (value != null)
            component.setValue(value);
        if (format != null)
            component.setFormat(parseSVGFormat(format));
        if (svg != null)
            component.setSVG(svg);
        if (color != null)
            component.setColor(color);
        if (path != null)
            component.setResourcePath(path);

        return component;
    }
    */

    /**
     * Returns a choice type
     *
     * @param type string value representing choice type
     * @return choice type
     */
    private EChoiceType parseChoiceType(String type) {
        if (type.equalsIgnoreCase("multiple")) {
            return EChoiceType.MULTIPLE;
        } else if (type.equalsIgnoreCase("single")) {
            return EChoiceType.SINGLE;
        } else {
            return null;
        }
    }

    private Map.Entry<String, String> parseTranslation(XmlPullParser parser) throws IOException, XmlPullParserException {
        // String name;
        parser.require(XmlPullParser.START_TAG, null, "translation");

        // Create element

        // Read attributes
        String value = parser.getAttributeValue(null, "value");
        String lang = parser.getAttributeValue(null, "lang");

        // Read sub elements
        parser.next();
        /*
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();

            switch (name) {
                default:
                    skip(parser);
            }
           }*/

        return new AbstractMap.SimpleEntry<>(lang, value);
    }

    /**
     * Returns a line count
     *
     * @param lines string value representing line count
     * @return line count
     */
    private int parseLines(String lines) {
        try {
            return Integer.parseInt(lines) > 0 ? Integer.parseInt(lines) : 0;
        } catch (NumberFormatException nfe) {
            Log.e(TAG, nfe.toString());
            return 0;
        }
    }

    /**
     * Returns a gravity
     *
     * @param gravity string value representing gravity
     * @return gravity
     */
    private EGravity parseGravity(String gravity) {
        if (gravity.equalsIgnoreCase("left")) {
            return EGravity.START;
        } else if (gravity.equalsIgnoreCase("center")) {
            return EGravity.CENTER;
        } else if (gravity.equalsIgnoreCase("right")) {
            return EGravity.END;
        } else {
            return EGravity.START;
        }
    }

    /**
     * Returns a list of tags
     *
     * @param tagString space separated string value containing tags
     * @return list of tags
     */
    private List<Tag> parseTags(String tagString) {
        String[] tags = tagString.split(" ");
        List<String> tagNames = Arrays.asList(tags);
        List<Tag> tagList = new ArrayList<>();

        for (String t : tagNames) {
            if (!t.trim().equals("")) {
                tagList.add(new Tag(t));
            }
        }

        return tagList;
    }

    /**
     * Returns a language aspect
     *
     * @param parser the XmlPullParser
     * @return language aspect
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Language parseLanguage(XmlPullParser parser) throws XmlPullParserException, IOException {
        String name;
        parser.require(XmlPullParser.START_TAG, null, "language");

        // Create element
        Language language = new Language();

        // Read attributes
        String from = parser.getAttributeValue(null, "from");
        String to = parser.getAttributeValue(null, "to");

        // Read sub elements

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();

            switch (name) {
                default:
                    skip(parser);
            }
        }

        // Fill element

        if (from != null)
            language.setFrom(from);
        if (to != null)
            language.setTo(to);

        return language;
    }

    /**
     * Turns a date string to a calendar
     *
     * @param s date string
     * @return Gregorian calendar
     */
    private GregorianCalendar parseDate(String s) {
        GregorianCalendar cal = new GregorianCalendar();

        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.US);
        try {
            Date date = df.parse(s);
            cal.setTime(date);
        } catch (ParseException e) {
            return new GregorianCalendar();
        }

        return cal;
    }

    /**
     * Skips a tag that does not fit
     *
     * @param parser the XmlPullParser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            Log.d(TAG, "throw new IllegalStateException()");
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
