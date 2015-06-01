package de.interoberlin.lymbo.util;


import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.interoberlin.mate.lib.model.Log;

public class Configuration {

    private static final String PROPERTIES_FILE = "lymbo.properties";

    public static String getProperty(Context c, EProperty property) {
        try {
            InputStream inputStream = c.getAssets().open(PROPERTIES_FILE);
            Properties props = new Properties();
            props.load(inputStream);
            return props.getProperty(property.getPropertyName());
        } catch (IOException e) {
            Log.error(e.toString());
            e.printStackTrace();
        }

        return null;
    }

    public static String getLanguage(Context c) {
        return c.getResources().getConfiguration().locale.getLanguage();
    }
}
