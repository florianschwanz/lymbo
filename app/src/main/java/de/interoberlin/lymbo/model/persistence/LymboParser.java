package de.interoberlin.lymbo.model.persistence;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.card.Side;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.model.card.components.Answer;
import de.interoberlin.lymbo.model.card.components.ChoiceComponent;
import de.interoberlin.lymbo.model.card.components.ImageComponent;
import de.interoberlin.lymbo.model.card.components.ResultComponent;
import de.interoberlin.lymbo.model.card.components.SVGComponent;
import de.interoberlin.lymbo.model.card.components.TextComponent;
import de.interoberlin.lymbo.model.card.components.TitleComponent;
import de.interoberlin.lymbo.model.card.enums.ChoiceType;
import de.interoberlin.lymbo.model.card.enums.EGravity;
import de.interoberlin.lymbo.model.card.enums.EStyle;
import de.interoberlin.mate.lib.model.Log;
import de.interoberlin.sauvignon.lib.controller.parser.SvgParser;
import de.interoberlin.sauvignon.lib.model.svg.SVG;

public class LymboParser {
    private static LymboParser instance;

    private Map<String, String> defaults = new HashMap<>();

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
     * Parses xml file an returns a map
     *
     * @param is           input stream representing lymbo file
     * @param onlyTopLevel determines if only the top level element shall be parsed
     * @return xmlLymbo
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    public Lymbo parse(InputStream is, boolean onlyTopLevel) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            parser.nextTag();
            return parseLymbo(parser, onlyTopLevel);
        } finally {
            is.close();
        }
    }

    /**
     * Returns a lymbo
     *
     * @param parser       the XmlPullParser
     * @param onlyTopLevel determines if only the top level element shall be parsed
     * @return xmlLymbo
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private Lymbo parseLymbo(XmlPullParser parser, boolean onlyTopLevel) throws XmlPullParserException, IOException {
        Log.trace("parseLymbo()");
        String name;

        parser.require(XmlPullParser.START_TAG, null, "lymbo");

        // Create element
        Lymbo lymbo = new Lymbo();

        // Read attributes
        String title = parser.getAttributeValue(null, "title");
        String subtitle = parser.getAttributeValue(null, "subtitle");
        String hint = parser.getAttributeValue(null, "hint");
        String image = parser.getAttributeValue(null, "image");
        String author = parser.getAttributeValue(null, "author");

        // Read attributes - default
        parseDefault(parser, "cardFlip");
        parseDefault(parser, "cardEdit");
        parseDefault(parser, "sideColor");
        parseDefault(parser, "sideFlip");
        parseDefault(parser, "titleLines");
        parseDefault(parser, "titleGravity");
        parseDefault(parser, "titleFlip");
        parseDefault(parser, "textLines");
        parseDefault(parser, "textGravity");
        parseDefault(parser, "textStyle");
        parseDefault(parser, "textFlip");
        parseDefault(parser, "resultFlip");
        parseDefault(parser, "imageFlip");
        parseDefault(parser, "choiceType");
        parseDefault(parser, "svgColor");
        parseDefault(parser, "svgFlip");

        // Read sub elements
        List<Card> cards = new ArrayList<>();

        if (!onlyTopLevel) {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    Log.trace("parseCard() continue");
                    continue;
                }

                name = parser.getName();

                if (name.equals("card")) {
                    cards.add(parseCard(parser));
                } else {
                    skip(parser);
                }
            }
        }

        // Fill element
        if (title != null)
            lymbo.setTitle(title);
        if (subtitle != null)
            lymbo.setSubtitle(subtitle);
        if (hint != null)
            lymbo.setHint(hint);
        if (image != null)
            lymbo.setImage(image);
        if (author != null)
            lymbo.setAuthor(author);

        lymbo.setCards(cards);


        return lymbo;
    }

    private void parseDefault(XmlPullParser parser, String attribute) {
        String value = parser.getAttributeValue(null, attribute);
        if (value != null)
            defaults.put(attribute, value);
    }

    /**
     * Returns a card which contains one or two sides
     *
     * @param parser the XmlPullParser
     * @return xmlCard
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private Card parseCard(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.trace("parseCard()");
        String name;
        parser.require(XmlPullParser.START_TAG, null, "card");

        // Create element
        Card card = new Card();

        // Read attributes
        String flip = parser.getAttributeValue(null, "flip");
        String edit = parser.getAttributeValue(null, "edit");

        String hint = parser.getAttributeValue(null, "hint");
        String chapter = parser.getAttributeValue(null, "chapter");
        String tags = parser.getAttributeValue(null, "tags");

        // Read sub elements
        List<Side> sides = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                Log.trace("parseCard() continue");
                continue;
            }

            name = parser.getName();

            Side side = null;
            switch (name) {
                case "front":
                    side = parseSide(parser, "front");
                    break;
                case "back":
                    side = parseSide(parser, "back");
                    break;
                case "side":
                    side = parseSide(parser, "side");
                    break;
                default:
                    skip(parser);
            }

            if (side != null)
                sides.add(side);
        }

        // Fill element
        card.setSides(sides);

        if (hint != null)
            card.setHint(hint);
        if (chapter != null) {
            card.setChapter(parseTag(chapter));
        } else {
            card.setChapter(new Tag("< no chapter >"));
        }
        if (tags != null) {
            card.setTags(parseTags(tags));
        } else {
            List<Tag> defaultTags = new ArrayList<>();
            defaultTags.add(new Tag("< no tag >"));
            card.setTags(defaultTags);
        }
        if (flip != null)
            card.setFlip(Boolean.parseBoolean(flip));
        if (edit != null)
            card.setEdit(Boolean.parseBoolean(edit));

        return card;
    }

    /**
     * Returns a side of a card
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private Side parseSide(XmlPullParser parser, String tag) throws XmlPullParserException, IOException {
        Log.trace("parseSide()");
        String name;
        parser.require(XmlPullParser.START_TAG, null, tag);

        // Create element
        Side side = new Side();

        // Read attributes
        String color = parser.getAttributeValue(null, "color");
        String flip = parser.getAttributeValue(null, "flip");

        // Read sub elements
        List<Displayable> components = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();
            Log.trace("name : " + name);

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
                case "svg":
                    components.add(parseSVGComponent(parser, color));
                    break;
                case "image":
                    components.add(parseImageComponent(parser));
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
        else if (defaults.containsKey("sideColor"))
            side.setColor(defaults.get("sideColor"));
        if (flip != null)
            side.setFlip(Boolean.parseBoolean(flip));
        else if (defaults.containsKey("sideFlip"))
            side.setFlip(Boolean.parseBoolean(defaults.get("sideFlip")));

        return side;
    }

    /**
     * Returns a title component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private TitleComponent parseTitleComponent(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.trace("parseTitleComponent()");
        String name;
        parser.require(XmlPullParser.START_TAG, null, "title");

        // Create element
        TitleComponent component = new TitleComponent();

        // Read attributes
        String value = parser.getAttributeValue(null, "value");
        String lines = parser.getAttributeValue(null, "lines");
        String gravity = parser.getAttributeValue(null, "gravity");
        String flip = parser.getAttributeValue(null, "flip");

        // Read sub elements
        Map<String, String> translations = new HashMap<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();
            Log.trace("name : " + name);

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
        else if (defaults.containsKey("titleLines"))
            component.setLines(parseLines(defaults.get("titleLines")));
        if (gravity != null)
            component.setGravity(parseGravity(gravity));
        else if (defaults.containsKey("titleGravity"))
            component.setGravity(parseGravity(defaults.get("titleGravity")));
        if (flip != null)
            component.setFlip(Boolean.parseBoolean(flip));
        else if (defaults.containsKey("titleFlip"))
            component.setFlip(Boolean.parseBoolean(defaults.get("titleFlip")));
        if (!translations.isEmpty())
            component.setTranslations(translations);

        return component;
    }

    /**
     * Returns a text component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private TextComponent parseTextComponent(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.trace("parseTextComponent()");
        String name;
        parser.require(XmlPullParser.START_TAG, null, "text");

        // Create element
        TextComponent component = new TextComponent();

        // Read attributes
        String value = parser.getAttributeValue(null, "value");
        String lines = parser.getAttributeValue(null, "lines");
        String gravity = parser.getAttributeValue(null, "gravity");
        String style = parser.getAttributeValue(null, "style");
        String flip = parser.getAttributeValue(null, "flip");

        // Read sub elements
        Map<String, String> translations = new HashMap<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();
            Log.trace("name : " + name);

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
        else if (defaults.containsKey("textLines"))
            component.setLines(parseLines(defaults.get("textLines")));
        if (gravity != null)
            component.setGravity(parseGravity(gravity));
        else if (defaults.containsKey("textGravity"))
            component.setGravity(parseGravity(defaults.get("textGravity")));
        if (style != null)
            component.setStyle(parseStyle(style));
        else if (defaults.containsKey("textStyle"))
            component.setStyle(parseStyle(defaults.get("textStyle")));
        if (flip != null)
            component.setFlip(Boolean.parseBoolean(flip));
        else if (defaults.containsKey("textFlip"))
            component.setFlip(Boolean.parseBoolean(defaults.get("textFlip")));
        if (!translations.isEmpty())
            component.setTranslations(translations);

        return component;
    }

    /**
     * Returns a result component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private ResultComponent parseResultComponent(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.trace("parseResultComponent()");
        parser.require(XmlPullParser.START_TAG, null, "result");

        // Create element
        ResultComponent component = new ResultComponent();

        // Read attributes
        String flip = parser.getAttributeValue(null, "flip");

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
        if (flip != null)
            component.setFlip(Boolean.parseBoolean(flip));
        else if (defaults.containsKey("resultFlip"))
            component.setFlip(Boolean.parseBoolean(defaults.get("resultFlip")));

        return component;
    }

    /**
     * Returns a result component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private ImageComponent parseImageComponent(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.trace("parseImageComponent()");
        parser.require(XmlPullParser.START_TAG, null, "image");

        // Create element
        ImageComponent component = new ImageComponent();

        // Read attributes
        String value = parser.getAttributeValue(null, "value");
        String flip = parser.getAttributeValue(null, "flip");

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
        if (flip != null)
            component.setFlip(Boolean.parseBoolean(flip));
        else if (defaults.containsKey("imageFlip"))
            component.setFlip(Boolean.parseBoolean(defaults.get("imageFlip")));

        return component;
    }

    /**
     * Returns a choice component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private ChoiceComponent parseChoiceComponent(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.trace("parseChoiceComponent()");
        String name;
        parser.require(XmlPullParser.START_TAG, null, "choice");

        // Create element
        ChoiceComponent component = new ChoiceComponent();

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
        else if (defaults.containsKey("choiceType"))
            component.setChoiceType(parseChoiceType(defaults.get("choiceType")));
        if (!answers.isEmpty())
            component.setAnswers(answers);

        return component;
    }

    /**
     * Returns an answer component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private Answer parseAnswer(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.trace("parseAnswer()");
        parser.require(XmlPullParser.START_TAG, null, "answer");

        // Create element
        Answer answer = new Answer();

        // Read attributes
        String value = parser.getAttributeValue(null, "value");
        String correct = parser.getAttributeValue(null, "correct");

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
            answer.setValue(value);
        if (correct != null)
            answer.setCorrect(Boolean.parseBoolean(correct));

        return answer;
    }

    /**
     * Returns an svg component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private SVGComponent parseSVGComponent(XmlPullParser parser, String color) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "svg");

        // Create element
        SVGComponent component = new SVGComponent();
        SVG svg = SvgParser.getInstance().parseSVG(parser);

        // Read attributes
        String flip = parser.getAttributeValue(null, "flip");

        // Read sub elements
        /*
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
        }
        */

        // Fill element
        if (svg != null)
            component.setSVG(svg);
        if (color != null)
            component.setColor(color);
        else if (defaults.containsKey("svgColor"))
            component.setColor(defaults.get("svgColor"));
        if (flip != null)
            component.setFlip(Boolean.parseBoolean(flip));
        else if (defaults.containsKey("svgFlip"))
            component.setFlip(Boolean.parseBoolean(defaults.get("svgFlip")));

        return component;
    }

    /**
     * Returns a choice type
     *
     * @param type string value representing choice type
     * @return choice type
     */
    private ChoiceType parseChoiceType(String type) {
        if (type.equalsIgnoreCase("multiple")) {
            return ChoiceType.MULTIPLE;
        } else if (type.equalsIgnoreCase("single")) {
            return ChoiceType.SINGLE;
        } else {
            return null;
        }
    }

    private Map.Entry<String, String> parseTranslation(XmlPullParser parser) throws IOException, XmlPullParserException {
        Log.trace("parseTranslation");
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
            Log.trace("name : " + name);

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
            Log.error(nfe.toString());
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
            return EGravity.LEFT;
        } else if (gravity.equalsIgnoreCase("center")) {
            return EGravity.CENTER;
        } else if (gravity.equalsIgnoreCase("right")) {
            return EGravity.RIGHT;
        } else {
            return EGravity.LEFT;
        }
    }

    /**
     * Returns a tag
     *
     * @param tagString string value representing a tag
     * @return list of tags
     */
    private Tag parseTag(String tagString) {
        return new Tag(tagString);
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
     * Returns a text style
     *
     * @param style string value representing style
     * @return text style
     */
    private EStyle parseStyle(String style) {
        if (style.equalsIgnoreCase("code")) {
            return EStyle.CODE;
        } else {
            return EStyle.NORMAL;
        }
    }


    /**
     * Skips a tag that does not fit
     *
     * @param parser the XmlPullParser
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.trace("skip()");
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            Log.trace("throw new IllegalStateException()");
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
