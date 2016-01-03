package de.interoberlin.lymbo.model.persistence.filesystem;

import android.content.Context;
import android.os.Environment;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.converters.Deserializer;
import de.interoberlin.lymbo.core.model.v1.impl.EFormat;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.lymbo.util.ZipUtil;
import de.interoberlin.mate.lib.model.Log;

public class LymboLoader {
    public static final String TAG = LymboLoader.class.toString();

    // --------------------
    // Methods - Facade
    // --------------------

    public static Stack getLymboFromString(Context context, String string, boolean onlyTopLevel) {
        Stack stack = getLymboFromInputStream(context, new ByteArrayInputStream(string.getBytes()), null, onlyTopLevel);

        if (stack == null || stack.getTitle() == null)
            return null;

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
                    Stack stack = getLymboFromInputStream(context, inputStream, null, onlyTopLevel);
                    if (stack != null) {
                        stack.setFile(fileName);
                        stack.setPath(fileName);
                        stack.setAsset(true);
                        stack.setFormat(EFormat.LYMBO);
                    }

                    return stack;
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
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

                return getLymbo(context, file, path, onlyTopLevel, true, EFormat.LYMBOX);
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
                return getLymbo(context, file, path, onlyTopLevel, false, EFormat.LYMBO);
            } else if (file.getAbsolutePath().endsWith(context.getResources().getString(R.string.lymbox_file_extension))) {
                File path = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + context.getResources().getString(R.string.lymbo_tmp_path) + "/" + UUID.randomUUID());

                try {
                    if (!path.mkdirs())
                        return null;

                    ZipUtil.unzip(file, path);
                    return getLymbo(context, file, path, onlyTopLevel, false, EFormat.LYMBOX);
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

    private static String getString(InputStream is) {
        try {
            return IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * Load a stack from a *.lymbox file
     *
     * @param context      context
     * @param file         *.lymbo(x) file
     * @param path         path of *.lymbo(x) file
     * @param onlyTopLevel whether to load only the top level element or not
     * @param asset        whether this file is an asset or not
     * @return stack
     */
    private static Stack getLymbo(Context context, File file, File path, boolean onlyTopLevel, boolean asset, EFormat format) {
        try {
            Stack stack = null;
            switch (format) {
                case LYMBO: {
                    if (file.exists())
                        stack = getLymboFromInputStream(context, new FileInputStream(file), path, onlyTopLevel);
                    break;
                }
                case LYMBOX: {
                    if (new File(path.getAbsolutePath() + "/" + App.getContext().getResources().getString(R.string.lymbo_main_file)).exists())
                        stack = getLymboFromInputStream(context, new FileInputStream(new File(path.getAbsolutePath() + "/" + App.getContext().getResources().getString(R.string.lymbo_main_file))), path, onlyTopLevel);
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
                    stack.setModificationDate(new GregorianCalendar());
                    LymboWriter.writeXml(stack, new File(stack.getFile()));
                }

                return stack;
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    private static Stack getLymboFromInputStream(Context context, InputStream is, File path, boolean onlyTopLevel) {
        try {
            String content = getString(is);
            Stack stack;

            // Try to parse as xml
            stack = LymboParser.fromXml(IOUtils.toInputStream(content, "UTF-8"), path, onlyTopLevel);

            // Try to parse as json
            if (stack == null || stack.getError() != null) {
                stack = Deserializer.fromJson(content);
            }

            // Show error
            if (stack == null) {
                stack = new Stack();
                stack.setError(context.getResources().getString(R.string.this_file_does_not_match_lymbo_format));
            }

            return stack;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
    }
}
