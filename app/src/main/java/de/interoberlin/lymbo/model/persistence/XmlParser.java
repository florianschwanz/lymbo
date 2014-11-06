package de.interoberlin.lymbo.model.persistence;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.card.XmlCard;
import de.interoberlin.lymbo.model.card.XmlChoice;
import de.interoberlin.lymbo.model.card.XmlLymbo;
import de.interoberlin.lymbo.model.card.XmlSide;
import de.interoberlin.lymbo.model.card.XmlStack;
import de.interoberlin.lymbo.model.card.XmlText;
import de.interoberlin.lymbo.model.card.XmlTextType;

public class XmlParser {
    private static XmlParser instance;
    private static int i;

    // --------------------
    // Constructors
    // --------------------

    private XmlParser() {

    }

    public static XmlParser getInstance() {
        if (instance == null) {
            instance = new XmlParser();
        }

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

    /**
     * Parses xml file an returns a map
     *
     * @param in
     * @return xmlLymbo
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    public XmlLymbo parse(InputStream in) throws XmlPullParserException, IOException {
        i = 0;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readLymbo(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Returns a lymbo
     *
     * @param parser
     * @return xmlLymbo
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private XmlLymbo readLymbo(XmlPullParser parser) throws XmlPullParserException, IOException {
        String text = null;
        String image = null;
        XmlStack stack = null;

        parser.require(XmlPullParser.START_TAG, null, "lymbo");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            // Starts by looking for the entry tag
            if (name.equals("text")) {
                text = (readText(parser));
            } else if (name.equals("image")) {
                image = (readText(parser));
            } else if (name.equals("stack")) {
                stack = (readStack(parser));
            } else {
                skip(parser);
            }
        }

        return new XmlLymbo(text, image, stack);
    }

    /**
     * Returns a stack which is a List of cards
     *
     * @param parser
     * @return xmlStack
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private XmlStack readStack(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<XmlCard> cards = new ArrayList<XmlCard>();

        parser.require(XmlPullParser.START_TAG, null, "stack");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            // Starts by looking for the entry tag
            if (name.equals("card")) {
                cards.add(readCard(parser));
            } else {
                skip(parser);
            }
        }
        return new XmlStack(cards);
    }

    /**
     * Returns a card which contains one or two sides
     *
     * @param parser
     * @return xmlCard
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private XmlCard readCard(XmlPullParser parser) throws XmlPullParserException, IOException {
        String title = "";
        String color = "#FFFFFF";
        XmlSide front = null;
        XmlSide back = null;

        parser.require(XmlPullParser.START_TAG, null, "card");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            // Starts by looking for the entry tag
            if (name.equals("title")) {
                title = readString(parser, "title");
            } else if (name.equals("color")) {
                color = readString(parser, "color");
            } else if (name.equals("front")) {
                front = readSide(parser, "front");
            } else if (name.equals("back")) {
                back = readSide(parser, "back");
            } else {
                skip(parser);
            }
        }
        return new XmlCard(i++, title, color, front, back);
    }

    /**
     * Returns the front side of a card
     *
     * @param parser
     * @return xmlSide
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private XmlSide readSide(XmlPullParser parser, String tag) throws XmlPullParserException, IOException {
        List<XmlText> texts = new ArrayList<XmlText>();
        String image = "";
        String hint = "";
        List<XmlChoice> choices = new ArrayList<XmlChoice>();

        parser.require(XmlPullParser.START_TAG, null, tag);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("text")) {
                texts.add(new XmlText(XmlTextType.NORMAL, readString(parser, "text")));
            } else if (name.equals("code")) {
                texts.add(new XmlText(XmlTextType.CODE, readString(parser, "code")));
            } else if (name.equals("image")) {
                image = readString(parser, "image");
            } else if (name.equals("hint")) {
                hint = readString(parser, "hint");
            } else if (name.equals("right")) {
                choices.add(new XmlChoice(readString(parser, "right"), true));
            } else if (name.equals("wrong")) {
                choices.add(new XmlChoice(readString(parser, "wrong"), false));
            } else {
                skip(parser);
            }
        }

        return new XmlSide(texts, image, hint, choices);
    }

    /**
     * Returns the title of a card
     *
     * @param parser
     * @return title
     * @throws java.io.IOException
     * @throws org.xmlpull.v1.XmlPullParserException
     */
    private String readString(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, tag);
        return title;
    }

    /**
     * Reads the value of a cell
     *
     * @param parser
     * @return result
     * @throws java.io.IOException
     * @throws org.xmlpull.v1.XmlPullParserException
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Skips a tag that does not fit
     *
     * @param parser
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
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
