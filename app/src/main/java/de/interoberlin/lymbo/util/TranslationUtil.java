package de.interoberlin.lymbo.util;

import java.util.Map;

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
    public static boolean contains(Map<String, String> translations, String language) {
        for (Map.Entry<String, String> t : translations.entrySet()) {
            if (t.getValue().equals(language))
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
    public static String get(Map<String, String> translations, String language) {
        for (Map.Entry<String, String> t : translations.entrySet()) {
            if (t.getKey().equals(language))
                return t.getValue();
        }

        return null;
    }
}
