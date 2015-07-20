package de.interoberlin.lymbo.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EProperty;

public class LoggingUtil {
    private static String logFileName = "lymbo.log";
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS z");

    // Properties
    private static String INTEROBERLIN_LOG_PATH;

    // --------------------
    // Methods
    // --------------------

    public static void writeException(Activity activity, Exception e) {
        try {
            getLogDir(activity).mkdirs();

            FileWriter fw = new FileWriter(getLogFile(activity));
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

    private static File getLogDir(Activity activity) {
        INTEROBERLIN_LOG_PATH = Configuration.getProperty(activity, EProperty.INTEROBERLIN_LOG_PATH);
        return new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + INTEROBERLIN_LOG_PATH);
    }

    private static File getLogFile(Activity activity) {
        return new File(getLogDir(activity) + "/" + logFileName);
    }

    /**
     * Send the current log to the developers
     *
     * @param c context
     * @param a activity
     */
    public static void sendErrorLog(Context c, Activity a) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        if (!getLogFile(a).exists() || !getLogFile(a).canRead()) {
            Toast.makeText(a, "Attachment Error", Toast.LENGTH_SHORT).show();
            a.finish();
            return;
        }
        Uri uri = Uri.parse("file://" + getLogFile(a));

        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]
                {"support@interoberlin.de"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, c.getResources().getString(R.string.lymbo_error_log));
        emailIntent.putExtra(Intent.EXTRA_TEXT, c.getResources().getString(R.string.describe_the_error));
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        a.startActivity(Intent.createChooser(emailIntent, c.getResources().getString(R.string.send)));
    }
}