package de.interoberlin.lymbo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    public static String getProperty(Context c, EPreference preference) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

        return (preference.getType() == String.class) ? prefs.getString(preference.getPreferenceName(), "") : "";
    }
}
