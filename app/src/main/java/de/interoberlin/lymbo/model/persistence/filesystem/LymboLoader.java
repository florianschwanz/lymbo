package de.interoberlin.lymbo.model.persistence.filesystem;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.card.EFormat;
import de.interoberlin.lymbo.model.card.Stack;
import de.interoberlin.lymbo.util.ZipUtil;
import de.interoberlin.mate.lib.model.Log;

public class LymboLoader {
    /**
     * Loads a stack object from a given file inside assets
     *
     * @param c         Context
     * @param lymboPath path to lymbo file
     * @return stack
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
     * Loads a stack object from a given file inside assets
     *
     * @param c Context
     * @param f path to lymbo file
     * @return stack
     */
    public static Stack getLymboxFromAsset(Context c, String f, boolean onlyTopLevel) {
        String LYMBO_TMP_PATH = App.getContext().getResources().getString(R.string.lymbo_tmp_path);

        File tmpDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_TMP_PATH + "/" + UUID.randomUUID());

        try {
            InputStream inputStream = c.getAssets().open(f);
            ZipUtil.unzip(inputStream, tmpDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getLymboFromFile(new File(tmpDir + "/main.lymbo"), new File(f), onlyTopLevel, true);
    }

    /**
     * Load a stack from a *.lymbo file
     *
     * @param f            lymbo file to load
     * @param onlyTopLevel whether to load only the top level element or not
     * @return stack
     */
    public static Stack getLymboFromFile(File f, boolean onlyTopLevel) {
        Stack stack = getLymboFromFile(f, f, onlyTopLevel, false);
        stack.setFormat(EFormat.LYMBO);

        return stack;
    }

    /**
     * Load a stack from a *.lymbox file
     *
     * @param f            lymbo file to load
     * @param originalPath original *.lymbox file
     * @param onlyTopLevel whether to load only the top level element or not
     * @param isAsset      whether this file is an asset or not
     * @return stack
     */
    public static Stack getLymboFromFile(File f, File originalPath, boolean onlyTopLevel, boolean isAsset) {
        try {
            Stack stack = getLymboFromInputStream(new FileInputStream(f), onlyTopLevel);
            if (stack != null) {
                stack.setPath(f.getAbsolutePath());
                stack.setOriginalPath(originalPath.getAbsolutePath());
                stack.setAsset(isAsset);

                // Make sure that newly generated ids will be persistent
                if (!onlyTopLevel && stack.isContainsGeneratedIds()) {
                    stack.setModificationDate(new Date().toString());
                    LymboWriter.writeXml(stack, new File(stack.getPath()));
                }

                return stack;
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            Log.error(e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load a Lymbo object from a given file
     *
     * @param f File
     * @return Lymbo file
     */
    public static Stack getLymboxFromFile(File f, boolean onlyTopLevel) {
        try {
            String LYMBO_TMP_PATH = App.getContext().getResources().getString(R.string.lymbo_tmp_path);

            File tmpDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_TMP_PATH + "/" + UUID.randomUUID());
            System.out.println("FOO f " + f.getPath());
            System.out.println("FOO tmpDir " + tmpDir);

            ZipUtil.unzip(f, tmpDir);

            Stack stack = getLymboFromInputStream(new FileInputStream(new File(tmpDir + "/main.lymbo")), onlyTopLevel);
            if (stack != null) {
                stack.setPath(new File(tmpDir + "/main.lymbo").getAbsolutePath());
                stack.setOriginalPath(f.getAbsolutePath());
                stack.setAsset(false);
                stack.setFormat(EFormat.LYMBOX);

                // Make sure that newly generated ids will be persistent
                if (!onlyTopLevel && stack.isContainsGeneratedIds()) {
                    stack.setModificationDate(new Date().toString());
                    LymboWriter.writeXml(stack, new File(stack.getPath()));
                }

                return stack;
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
