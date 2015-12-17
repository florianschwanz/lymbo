package de.interoberlin.lymbo.util;

import java.util.List;

import de.interoberlin.lymbo.core.model.v1.objects.TranslationObject;

public class TranslationUtil {
    // --------------------
    // Methods
    // --------------------

    /**
     * Determines whether a language is covered by a list of translations
     *
     * @param translations list for translations
     * @param language     language to look for
     * @return whether or not @param language is covered by @param languages
     */
    public static boolean contains(List<TranslationObject> translations, String language) {
        for (TranslationObject t : translations) {
            if (t.getLang().equals(language))
                return true;
        }

        return false;
    }

    /**
     * Gets the specific translation
     *
     * @param translations list of translations
     * @param language     target language
     * @return value in target language
     */
    public static String get(List<TranslationObject> translations, String language) {
        for (TranslationObject t : translations) {
            if (t.getLang().equals(language))
                return t.getValue();
        }

        return null;
    }
}
