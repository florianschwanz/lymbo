package de.interoberlin.lymbo.util;


import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.interoberlin.mate.lib.model.Log;

public class Configuration {
    public static final String TAG = Configuration.class.toString();

    private static final String GRADLE_PROPERTIES_FILE = "gradle.properties";

    public static String getGradleProperty(Context c, String property) {
        try {
            InputStream inputStream = c.getAssets().open(GRADLE_PROPERTIES_FILE);
            Properties props = new Properties();
            props.load(inputStream);
            return props.getProperty(property);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

        return null;
    }

    public static String getLanguage(Context c) {
        return c.getResources().getConfiguration().locale.getLanguage();
    }
}
