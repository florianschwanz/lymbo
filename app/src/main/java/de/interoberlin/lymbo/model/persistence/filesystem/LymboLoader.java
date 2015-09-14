package de.interoberlin.lymbo.model.persistence.filesystem;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import de.interoberlin.lymbo.model.card.Stack;
import de.interoberlin.mate.lib.model.Log;

public class LymboLoader {

    /**
     * Loads a Lymbo object from a given file inside assets
     *
     * @param c         Context
     * @param lymboPath path to lymbo file
     * @return Lymbo object
     */
    public static Stack getLymboFromAsset(Context c, String lymboPath, boolean onlyTopLevel) {
        try {
            InputStream inputStream = c.getAssets().open(lymboPath);
            Stack l = getLymboFromInputStream(inputStream, onlyTopLevel);
            if (l != null) {
                l.setPath(lymboPath);
                l.setAsset(true);
                inputStream.close();
                return l;
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.error(e.toString());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Load a Lymbo object from a given InputStream
     *
     * @param f File
     * @return Lymbo file
     */
    public static Stack getLymboFromFile(File f, boolean onlyTopLevel) {
        try {
            Stack l = getLymboFromInputStream(new FileInputStream(f), onlyTopLevel);
            if (l != null) {
                l.setPath(f.getAbsolutePath());
                l.setAsset(false);

                // Make sure that newly generated ids will be persistent
                if (!onlyTopLevel && l.isContainsGeneratedIds()) {
                    l.setModificationDate(new Date().toString());
                    LymboWriter.writeXml(l, new File(l.getPath()));
                }

                return l;
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            Log.error(e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public static Stack getLymboFromInputStream(InputStream is, boolean onlyTopLevel) {
        try {
            return LymboParser.getInstance().parse(is, onlyTopLevel);
        } catch (IOException e) {
            Log.error(e.toString());
            e.printStackTrace();
            return null;
        }
    }
}
