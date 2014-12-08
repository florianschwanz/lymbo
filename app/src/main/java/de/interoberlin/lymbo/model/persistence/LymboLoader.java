package de.interoberlin.lymbo.model.persistence;

import android.content.Context;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.mate.lib.model.Log;

public class LymboLoader {

    /**
     * Loads a Lymbo object from a given file inside assets
     *
     * @param c
     * @param svgPath
     * @return
     */
    public static Lymbo getLymboFromAsset(Context c, String svgPath) {
        try {
            InputStream inputStream = c.getAssets().open(svgPath);
            Lymbo l = getLymboFromFile(inputStream);
            inputStream.close();
            return l;
        } catch (IOException e) {
            Log.error(e.toString());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Load a Lymbo object from a given InputStream
     *
     * @param is
     * @return
     */
    public static Lymbo getLymboFromFile(InputStream is) {
        try {
            return LymboParser.getInstance().parse(is);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
