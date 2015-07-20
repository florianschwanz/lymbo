package de.interoberlin.lymbo.view.activities;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EProperty;

public class LoggingUtil {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS z");

    // Properties
    private static String LYMBO_SAVE_PATH;

    // --------------------
    // Methods
    // --------------------

    public static void writeException(Activity activity, Exception e) {
        try {
            LYMBO_SAVE_PATH = Configuration.getProperty(activity, EProperty.LYMBO_SAVE_PATH);

            File path = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH);
            path.mkdirs();

            FileWriter fw = new FileWriter(path + "/lymbo.log");
            fw.write(format.format(new Date()) + " ERROR " + getStackTrace(e));
            fw.flush();
            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
