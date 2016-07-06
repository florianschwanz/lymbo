package de.interoberlin.lymbo.model.factories;

import java.util.List;

import de.interoberlin.lymbo.core.model.v1.impl.Card;
import de.interoberlin.lymbo.core.model.v1.impl.Side;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.core.model.v1.impl.components.Answer;
import de.interoberlin.lymbo.core.model.v1.impl.components.Choice;
import de.interoberlin.lymbo.core.model.v1.impl.components.EChoiceType;
import de.interoberlin.lymbo.core.model.v1.impl.components.Result;
import de.interoberlin.lymbo.core.model.v1.impl.components.Text;
import de.interoberlin.lymbo.core.model.v1.impl.components.Title;

public class CardFactory {
    private static CardFactory instance;

    // --------------------
    // Constructors
    // --------------------

    private CardFactory() {

    }

    public static CardFactory getInstance() {
        if (instance == null) {
            instance = new CardFactory();
        }

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

    public Card getCard(String id, String frontTitleValue, List<String> frontTextsValues, String backTitleValue, List<String> backTextsValues, List<Tag> tags, List<Answer> answers) {
        Card card = new Card();

        card.setId(id);

        boolean containsFrontTitle = frontTitleValue != null && !frontTitleValue.isEmpty();
        boolean conatisnFrontTexts = frontTextsValues != null && !frontTextsValues.isEmpty();
        boolean containsAnswers = answers != null && !answers.isEmpty();

        if (containsFrontTitle || conatisnFrontTexts || containsAnswers) {
            Side sideFront = new Side();

            if (containsFrontTitle)
                sideFront.getComponents().add(new Title(frontTitleValue));

            if (conatisnFrontTexts)
                for (String s : frontTextsValues)
                    sideFront.getComponents().add(new Text(s));

            if (containsAnswers)
                sideFront.getComponents().add(new Choice(EChoiceType.MULTIPLE, answers));

            card.getSides().add(sideFront);
        }

        boolean containsBackTitle = backTitleValue != null && !backTitleValue.isEmpty();
        boolean containsBackTexts = backTextsValues != null && !backTextsValues.isEmpty();

        if (containsBackTitle || containsBackTexts || containsAnswers) {
            Side sideBack = new Side();

            if (containsAnswers)
                sideBack.getComponents().add(new Result());

            if (containsBackTitle)
                sideBack.getComponents().add(new Title(backTitleValue));

            if (containsBackTexts)
                for (String s : backTextsValues)
                    sideBack.getComponents().add(new Text(s));

            card.getSides().add(sideBack);
        }

        boolean containsTags = tags != null && !tags.isEmpty();

        if (containsTags)
            card.setTags(tags);

        return card;
    }
}
