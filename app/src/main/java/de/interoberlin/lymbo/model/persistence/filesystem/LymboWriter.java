package de.interoberlin.lymbo.model.persistence.filesystem;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.card.Stack;

/**
 * This class can be used to write a lymbo object into an xml file
 */
public class LymboWriter {
    public static void writeXml(Stack stack, File file) {
        // Create save path
        String LYMBO_SAVE_PATH = App.getContext().getResources().getString(R.string.lymbo_save_path);
        if (new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH).mkdirs())
            return;

        stack.setModificationDate(new Date().toString());

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(stack.toString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
