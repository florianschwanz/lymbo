package de.interoberlin.lymbo.model.card.aspects;

import java.util.HashMap;
import java.util.Map;

import de.interoberlin.lymbo.model.webservice.translate.Language;

public class LanguageAspect {
    public static final String TAG = "language";

    private Language from;
    private Language to;

    // --------------------
    // Constructors
    // --------------------

    public LanguageAspect() {
    }

    // --------------------
    // Methods
    // --------------------

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        if (this != null && getFrom() != null && getTo() != null) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("from", getFrom().getLangCode());
            attributes.put("to", getTo().getLangCode());

            result.append("\n<").append(TAG);
            for (Map.Entry<String, String> e : attributes.entrySet()) {
                if (e.getValue() != null) {
                    result.append("\n ").append(e.getKey()).append("=\"").append(e.getValue()).append("\"");
                }
            }
            result.append(" />");
        }

        return result.toString();
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public Language getFrom() {
        return from;
    }

    public void setFrom(Language from) {
        this.from = from;
    }

    public Language getTo() {
        return to;
    }

    public void setTo(Language to) {
        this.to = to;
    }
}
