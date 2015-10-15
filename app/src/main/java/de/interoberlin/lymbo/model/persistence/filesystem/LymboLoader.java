package de.interoberlin.lymbo.model.persistence.filesystem;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.card.EFormat;
import de.interoberlin.lymbo.model.card.Stack;
import de.interoberlin.lymbo.util.ZipUtil;
import de.interoberlin.mate.lib.model.Log;

public class LymboLoader {
    // --------------------
    // Methods - Facade
    // --------------------

    public static Stack getLymboFromString(Context context, String string, boolean onlyTopLevel) {
        Stack stack = getLymboFromInputStream(new ByteArrayInputStream(string.getBytes()), null, onlyTopLevel);

        String LYMBO_SAVE_PATH = context.getResources().getString(R.string.lymbo_save_path);
        String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH;
        String fileExtension = context.getResources().getString(R.string.lymbo_file_extension);
        String fileName = stack.getTitle().trim().replaceAll(" ", "_").toLowerCase(Locale.getDefault());

        stack.setFile(path + "/" + fileName + fileExtension);
        stack.setPath(path);
        stack.setAsset(false);
        stack.setFormat(EFormat.LYMBO);
        return stack;
    }

    /**
     * Loads a stack object from a given file inside assets
     *
     * @param context  Context
     * @param fileName lymbo(x) file name to load
     * @return stack
     */
    public static Stack getLymboFromAsset(Context context, String fileName, boolean onlyTopLevel) {
        if (fileName != null) {
            if (fileName.endsWith(context.getResources().getString(R.string.lymbo_file_extension))) {
                try {
                    InputStream inputStream = context.getAssets().open(fileName);
                    Stack stack = getLymboFromInputStream(inputStream, null, onlyTopLevel);
                    if (stack != null) {
                        stack.setFile(fileName);
                        stack.setPath(fileName);
                        stack.setAsset(true);
                        stack.setFormat(EFormat.LYMBO);
                    }

                    return stack;
                } catch (IOException e) {
                    Log.error(e.toString());
                    e.printStackTrace();
                }
            } else if (fileName.endsWith(context.getResources().getString(R.string.lymbox_file_extension))) {
                File path = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + context.getResources().getString(R.string.lymbo_tmp_path) + "/" + UUID.randomUUID());

                try {
                    InputStream inputStream = context.getAssets().open(fileName);
                    ZipUtil.unzip(inputStream, path);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File file = new File(path.getAbsolutePath() + "/" + App.getContext().getResources().getString(R.string.lymbo_main_file));

                return getLymbo(file, path, onlyTopLevel, true, EFormat.LYMBOX);
            }
        }

        return null;
    }

    /**
     * Load a stack from a *.lymbo(x) file
     *
     * @param context      Context
     * @param file         lymbo(x) file to load
     * @param onlyTopLevel whether to load only the top level element or not
     * @return stack
     */
    public static Stack getLymboFromFile(Context context, File file, boolean onlyTopLevel) {
        if (file != null) {
            if (file.getAbsolutePath().endsWith(context.getResources().getString(R.string.lymbo_file_extension))) {
                File path = new File(file.getParent());
                return getLymbo(file, path, onlyTopLevel, false, EFormat.LYMBO);
            } else if (file.getAbsolutePath().endsWith(context.getResources().getString(R.string.lymbox_file_extension))) {
                File path = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + context.getResources().getString(R.string.lymbo_tmp_path) + "/" + UUID.randomUUID());

                try {
                    if (!path.mkdirs())
                        return null;

                    ZipUtil.unzip(file, path);
                    return getLymbo(file, path, onlyTopLevel, false, EFormat.LYMBOX);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        return null;
    }

    // --------------------
    // Methods
    // --------------------


    /**
     * Load a stack from a *.lymbox file
     *
     * @param file         *.lymbo(x) file
     * @param path         path of *.lymbo(x) file
     * @param onlyTopLevel whether to load only the top level element or not
     * @param asset        whether this file is an asset or not
     * @return stack
     */
    private static Stack getLymbo(File file, File path, boolean onlyTopLevel, boolean asset, EFormat format) {
        try {
            Stack stack = null;
            switch (format) {
                case LYMBO: {
                    if (file.exists())
                        stack = getLymboFromInputStream(new FileInputStream(file), path, onlyTopLevel);
                    break;
                }
                case LYMBOX: {
                    if (new File(path.getAbsolutePath() + "/" + App.getContext().getResources().getString(R.string.lymbo_main_file)).exists())
                        stack = getLymboFromInputStream(new FileInputStream(new File(path.getAbsolutePath() + "/" + App.getContext().getResources().getString(R.string.lymbo_main_file))), path, onlyTopLevel);
                    break;
                }
            }

            if (stack != null) {
                stack.setFile(file.getAbsolutePath());
                stack.setPath(path.getAbsolutePath());
                stack.setAsset(asset);
                stack.setFormat(format);

                // Make sure that newly generated ids will be persistent
                if (format == EFormat.LYMBO && !onlyTopLevel && !asset && stack.isContainsGeneratedIds()) {
                    stack.setModificationDate(new Date().toString());
                    LymboWriter.writeXml(stack, new File(stack.getFile()));
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

    private static Stack getLymboFromInputStream(InputStream is, File path, boolean onlyTopLevel) {
        try {
            return LymboParser.getInstance().parse(is, path, onlyTopLevel);
        } catch (IOException e) {
            Log.error(e.toString());
            e.printStackTrace();
            return null;
        }
    }
}
