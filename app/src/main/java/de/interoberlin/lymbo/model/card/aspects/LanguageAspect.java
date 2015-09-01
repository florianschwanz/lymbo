package de.interoberlin.lymbo.model.card.aspects;

import de.interoberlin.lymbo.model.translate.Language;

public class LanguageAspect {
    private Language from;
    private Language to;

    // --------------------
    // Constructors
    // --------------------

    public LanguageAspect() {
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
