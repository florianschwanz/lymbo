package de.interoberlin.lymbo.model.persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.interoberlin.lymbo.model.card.XmlCard;
import de.interoberlin.lymbo.model.card.XmlChoice;
import de.interoberlin.lymbo.model.card.XmlLymbo;
import de.interoberlin.lymbo.model.card.XmlSide;
import de.interoberlin.lymbo.model.card.XmlStack;
import de.interoberlin.lymbo.model.card.XmlText;

/**
 * This class can be used to write a lymbo object into an xml file
 * 
 * @author Florian
 * 
 */
public class XmlWriter
{
	private static StringBuilder	result;

	public static void writeXml(XmlLymbo lymbo, File file)
	{
		try
		{
			FileWriter fw = new FileWriter(file);
			fw.write(getXmlString(lymbo));
			fw.flush();
			fw.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates an xml formatted string from a lymbo object
	 * 
	 * @param lymbo
	 * @return
	 */
	public static String getXmlString(XmlLymbo lymbo)
	{
		result = new StringBuilder();

		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		appendLymbo("lymbo", lymbo);

		return result.toString();
	}

	/**
	 * Adds a value between to tags
	 * 
	 * @param value
	 */
	private static void addValue(String value)
	{
		result.append(value);
	}

	/**
	 * Adds a start tag
	 * 
	 * @param tag
	 */
	private static void addStartTag(String tag)
	{
		result.append("\n<" + tag + ">");
	}

	/**
	 * Adds an end tag
	 * 
	 * @param tag
	 */
	private static void addEndTag(String tag)
	{
		result.append("</" + tag + ">\n");
	}

	/**
	 * Appends a simple tag to the xml
	 * 
	 * @param tag
	 * @param text
	 */
	private static void appendTag(String tag, String text)
	{
		addStartTag(tag);
		addValue(text);
		addEndTag(tag);
	}

	/**
	 * Appends the lymbo root element to the xml
	 * 
	 * @param tag
	 * @param lymbo
	 */
	private static void appendLymbo(String tag, XmlLymbo lymbo)
	{
		addStartTag(tag);

		appendTag("text", lymbo.getText().toString());
		appendTag("image", lymbo.getImage().toString());
		appendStack("stack", lymbo.getStack());

		addEndTag(tag);
	}

	/**
	 * Appends a stack to the xml
	 * 
	 * @param tag
	 * @param stack
	 */
	private static void appendStack(String tag, XmlStack stack)
	{
		addStartTag(tag);

		for (XmlCard c : stack.getCards())
		{
			appendCard("card", c);
		}

		addEndTag(tag);
	}

	/**
	 * Appends a card to the xml
	 * 
	 * @param tag
	 * @param card
	 */
	private static void appendCard(String tag, XmlCard card)
	{
		addStartTag(tag);

		appendTag("title", card.getTitle());
		appendSide("front", card.getFront());
		appendSide("back", card.getBack());

		addEndTag(tag);
	}

	/**
	 * Appends a side (front or back) to the xml
	 * 
	 * @param tag
	 * @param side
	 */
	private static void appendSide(String tag, XmlSide side)
	{
		addStartTag(tag);

		appendTexts(side.getTexts());
		appendTag("image", side.getImage());
		appendTag("hint", side.getHint());
		appendChoices("choices", side.getChoices());
		addEndTag(tag);
	}

	/**
	 * Appends a text tag (text or code) to the xml file
	 */
	private static void appendTexts(List<XmlText> texts)
	{
		if (texts != null)
		{
			for (XmlText t : texts)
			{
				switch (t.getType())
				{
					case NORMAL:
					{
						appendTag("text", t.getText());
						break;
					}
					case CODE:
					{
						appendTag("code", t.getText());
						break;
					}
					default:
					{
						break;
					}
				}
			}
		}
	}

	/**
	 * Appends a choice tag (right or wrong) to the xml file
	 * 
	 * @param tag
	 * @param choices
	 */
	private static void appendChoices(String tag, List<XmlChoice> choices)
	{
		if (choices != null)
		{
			// addStartTag(tag);

			for (XmlChoice c : choices)
			{
				if (c.isRight())
				{
					appendTag("right", c.getText());
				} else
				{
					appendTag("wrong", c.getText());
				}
			}

			// addEndTag(tag);
		}
	}
}
