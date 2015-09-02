package de.interoberlin.lymbo.model.translate;


import android.content.Context;

import de.interoberlin.lymbo.R;

public enum Language {
    AUTO_DETECT("", R.string.lang_auto_detect),
    ARABIC("ar", R.string.lang_arabic),
    BULGARIAN("bg", R.string.lang_bulgarian),
    CATALAN("ca", R.string.lang_catalan),
    CHINESE_SIMPLIFIED("zh-CHS", R.string.lang_chinese_simplified),
    CHINESE_TRADITIONAL("zh-CHT", R.string.lang_chinese_traditional),
    CZECH("cs", R.string.lang_czech),
    DANISH("da", R.string.lang_danish),
    DUTCH("nl", R.string.lang_dutch, true),
    ENGLISH("en", R.string.lang_english, true),
    ESTONIAN("et", R.string.lang_estonian),
    FINNISH("fi", R.string.lang_finnish),
    FRENCH("fr", R.string.lang_french, true),
    GERMAN("de", R.string.lang_german, true),
    GREEK("el", R.string.lang_greek),
    HAITIAN_CREOLE("ht", R.string.lang_haitan_creole),
    HEBREW("he", R.string.lang_hebrew),
    HINDI("hi", R.string.lang_hindi),
    HMONG_DAW("mww", R.string.lang_hmong_daw),
    HUNGARIAN("hu", R.string.lang_hungarian),
    INDONESIAN("id", R.string.lang_indonesian),
    ITALIAN("it", R.string.lang_italian, true),
    JAPANESE("ja", R.string.lang_japanese),
    KOREAN("ko", R.string.lang_korean),
    LATVIAN("lv", R.string.lang_latvian),
    LITHUANIAN("lt", R.string.lang_lithuanian),
    MALAY("ms", R.string.lang_malay),
    NORWEGIAN("no", R.string.lang_norwegian),
    PERSIAN("fa", R.string.lang_persian),
    POLISH("pl", R.string.lang_polish),
    PORTUGUESE("pt", R.string.lang_portuguese, true),
    ROMANIAN("ro", R.string.lang_romanian),
    RUSSIAN("ru", R.string.lang_russian),
    SLOVAK("sk", R.string.lang_slovak),
    SLOVENIAN("sl", R.string.lang_slovenian),
    SPANISH("es", R.string.lang_spanish, true),
    SWEDISH("sv", R.string.lang_swedish),
    THAI("th", R.string.lang_thai),
    TURKISH("tr", R.string.lang_turkish),
    UKRAINIAN("uk", R.string.lang_ukrainian),
    URDU("ur", R.string.lang_urdu),
    VIETNAMESE("vi", R.string.lang_vietnamese),;

    private final String langCode;
    private final int name;
    private final boolean active;

    // --------------------
    // Constructors
    // --------------------

    Language(final String langCode, final int name) {
        this(langCode, name, false);
    }

    Language(final String langCode, final int name, final boolean active) {
        this.langCode = langCode;
        this.name = name;
        this.active = active;
    }

    // --------------------
    // Methods
    // --------------------

    public static Language fromString(final String langCode) {
        for (Language l : values()) {
            if (l.getLangCode().equals(langCode)) {
                return l;
            }
        }
        return null;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public String getLangCode() {
        return langCode;
    }

    public String getName(Context context) {
        return context.getResources().getString(name);
    }

    public boolean isActive() {
        return active;
    }
}