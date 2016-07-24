package de.interoberlin.lymbo.model.persistence.filesystem;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.converters.Serializer;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.mate.lib.model.Log;

/**
 * This class can be used to write a lymbo object into an xml file
 */
public class LymboWriter {
    private static final String TAG = LymboWriter.class.getSimpleName();

    /**
     * Writes a {@code stack} into a {@code file}
     *
     * @param stack stack to save
     * @param file  file wo write into
     * @return whether save worked or not
     */
    public static boolean writeXml(Stack stack, File file) {
        // Check external storage
        if (!isExternalStorageReadable() || !isExternalStorageWritable()) {
            Log.e(TAG, "External storage not available");
            return false;
        }

        // Create save directory
        if (createSaveDir()) {
            Log.e(TAG, "Could not create lymbo save dir");
            return false;
        }

        stack.setModificationDate(new GregorianCalendar());

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(Serializer.toPrettyJson(stack));
            fw.flush();
            fw.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if external storage is available for read and write
     *
     * @return whether external storage is writable
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if external storage is available to at least read
     *
     * @return whether external storage is readable
     */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Creates the directory to save new lymbo file in
     *
     * @return whether or not directory creation worked
     */
    private static boolean createSaveDir() {
        String LYMBO_SAVE_PATH = App.getContext().getResources().getString(R.string.lymbo_save_path);
        String COMPLETE_PATH = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH;

        Log.d(TAG, COMPLETE_PATH);

        return new File(COMPLETE_PATH).mkdirs();
    }
}
