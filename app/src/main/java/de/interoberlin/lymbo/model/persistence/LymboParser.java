package de.interoberlin.lymbo.model.persistence;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.Displayable;
import de.interoberlin.lymbo.model.card.Card;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.card.Side;
import de.interoberlin.lymbo.model.card.components.Answer;
import de.interoberlin.lymbo.model.card.components.ChoiceComponent;
import de.interoberlin.lymbo.model.card.components.HintComponent;
import de.interoberlin.lymbo.model.card.components.ImageComponent;
import de.interoberlin.lymbo.model.card.components.TextComponent;
import de.interoberlin.lymbo.model.card.components.TitleComponent;
import de.interoberlin.mate.lib.model.Log;

public class LymboParser {
    private static LymboParser instance;

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
     * @param in InputStream
     * @return xmlLymbo
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    public Lymbo parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return parseLymbo(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Returns a lymbo
     *
     * @param parser the XmlPullParser
     * @return xmlLymbo
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private Lymbo parseLymbo(XmlPullParser parser) throws XmlPullParserException, IOException {
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

        // Read sub elements
        List<Card> cards = new ArrayList<Card>();

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

        // Nothing to do here

        // Read sub elements
        Side front = null;
        Side back = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                Log.trace("parseCard() continue");
                continue;
            }

            name = parser.getName();

            if (name.equals("front")) {
                front = parseSide(parser, "front");
            } else if (name.equals("back")) {
                back = parseSide(parser, "back");
            } else {
                skip(parser);
            }
        }

        // Fill element
        if (front != null)
            card.setFront(front);
        if (back != null)
            card.setBack(back);

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

        // Read sub elements
        List<Displayable> components = new ArrayList<Displayable>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            name = parser.getName();
            Log.trace("name : " + name);

            if (name.equals("title")) {
                components.add(parseTitleComponent(parser));
            } else if (name.equals("text")) {
                components.add(parseTextComponent(parser));
            } else if (name.equals("hint")) {
                components.add(parseHintComponent(parser));
            } else if (name.equals("image")) {
                components.add(parseImageComponent(parser));
            } else if (name.equals("choice")) {
                components.add(parseChoiceComponent(parser));
            } else {
                skip(parser);
            }
        }

        // Fill element
        if (!components.isEmpty())
            side.setComponents(components);

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
        parser.require(XmlPullParser.START_TAG, null, "title");

        // Create element
        TitleComponent component = new TitleComponent();

        // Read attributes
        String value = parser.getAttributeValue(null, "value");

        // Read sub elements
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
        }

        // Fill element
        if (value != null)
            component.setValue(value);

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
        parser.require(XmlPullParser.START_TAG, null, "text");

        // Create element
        TextComponent component = new TextComponent();

        // Read attributes
        String value = parser.getAttributeValue(null, "value");

        // Read sub elements
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
        }

        // Fill element
        if (value != null)
            component.setValue(value);

        return component;
    }

    /**
     * Returns am hint component
     *
     * @param parser the XmlPullParser
     * @return xmlSide
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private HintComponent parseHintComponent(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.trace("parseHintComponent()");
        parser.require(XmlPullParser.START_TAG, null, "hint");

        // Create element
        HintComponent component = new HintComponent();

        // Read attributes
        String value = parser.getAttributeValue(null, "value");

        // Read sub elements
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
        }

        // Fill element
        if (value != null)
            component.setValue(value);

        return component;
    }

    /**
     * Returns am image component
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
        String image = parser.getAttributeValue(null, "image");

        // Read sub elements
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
        }

        // Fill element
        if (image != null)
            component.setImage(image);

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

        // Read sub elements
        List<Answer> answers = new ArrayList<Answer>();

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
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
        }

        // Fill element
        if (value != null)
            answer.setValue(value);
        if (correct != null)
            answer.setCorrect(Boolean.parseBoolean(correct));

        return answer;
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
