package de.interoberlin.lymbo.model.translate;


public enum Language {
    AUTO_DETECT(""), ARABIC("ar"), BULGARIAN("bg"), CATALAN("ca"), CHINESE_SIMPLIFIED("zh-CHS"), CHINESE_TRADITIONAL("zh-CHT"), CZECH("cs"), DANISH("da"), DUTCH("nl"), ENGLISH("en"), ESTONIAN("et"), FINNISH(
            "fi"), FRENCH("fr"), GERMAN("de"), GREEK("el"), HAITIAN_CREOLE("ht"), HEBREW("he"), HINDI("hi"), HMONG_DAW("mww"), HUNGARIAN("hu"), INDONESIAN("id"), ITALIAN("it"), JAPANESE("ja"), KOREAN(
            "ko"), LATVIAN("lv"), LITHUANIAN("lt"), MALAY("ms"), NORWEGIAN("no"), PERSIAN("fa"), POLISH("pl"), PORTUGUESE("pt"), ROMANIAN("ro"), RUSSIAN("ru"), SLOVAK("sk"), SLOVENIAN("sl"), SPANISH(
            "es"), SWEDISH("sv"), THAI("th"), TURKISH("tr"), UKRAINIAN("uk"), URDU("ur"), VIETNAMESE("vi");

    private final String lang;

    // --------------------
    // Constructors
    // --------------------

    Language(final String lang) {
        this.lang = lang;
    }

    // --------------------
    // Methods
    // --------------------

    public static Language fromString(final String pLanguage) {
        for (Language l : values()) {
            if (l.toString().equals(pLanguage)) {
                return l;
            }
        }
        return null;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getLang() {
        return lang;
    }
}