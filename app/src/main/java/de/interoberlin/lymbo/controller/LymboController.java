package de.interoberlin.lymbo.controller;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.interoberlin.lymbo.model.card.XmlLymbo;
import de.interoberlin.lymbo.model.persistence.XmlParser;
import de.interoberlin.mate.lib.model.Log;

public class LymboController extends Application {
    private static Context context;

    private static Collection<File> lymboFiles;
    private static List<XmlLymbo> lymbos;

    private static final String LYMBO_FILE_EXTENSION = ".lymbo";

    private static LymboController instance;

    // --------------------
    // Constructors
    // --------------------

    public LymboController() {
    }

    public static LymboController getInstance() {
        if (instance == null) {
            instance = new LymboController();
        }

        return instance;
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        init();
    }

    // --------------------
    // Methods
    // --------------------

    public static Context getContext() {
        return context;
    }

    private void init() {
        if (lymbos == null) {
            findLymboFiles();
            getLymbosFromFiles();
        }
    }

    /**
     * Finds all files having the extension .lymbo
     */
    private void findLymboFiles() {
        Log.trace("LymboController.findLymboFiles()");
        // lymboFiles = findFiles(LYMBO_FILE_EXTENSION);

        lymboFiles = new ArrayList<File>();
        lymboFiles.add(new File("/storage/emulated/0/Interoberlin/lymbo/java_oca.lymbo"));
        // lymboFiles.add(new File("/storage/emulated/0/Interoberlin/lymbo/java_oca2.lymbo"));
    }

    /**
     * Finds all files that match a certain pattern on the internal storage
     *
     * @param pattern
     * @return
     */
    public Collection<File> findFiles(String pattern) {
        Log.trace("LymboController.findFiles()");
        return FileUtils.listFiles(Environment.getExternalStorageDirectory(), new RegexFileFilter(".*" + pattern), TrueFileFilter.TRUE);
    }

    /**
     * Converts lymbo files into lymbo objects
     */
    private void getLymbosFromFiles() {
        lymbos = new ArrayList<XmlLymbo>();

        if (lymboFiles != null) {
            for (File f : lymboFiles) {
                try {
                    lymbos.add(XmlParser.getInstance().parse(new FileInputStream(f)));
                } catch (FileNotFoundException fnfe) {
                    Log.error(fnfe.toString());
                } catch (XmlPullParserException xppe) {
                    Log.error(xppe.toString());
                } catch (IOException ioe) {
                    Log.error(ioe.toString());
                }
            }
        }
    }

    public boolean checkStorage() {
        boolean externalStorageAvailable = false;
        boolean externalStorageWriteable = false;

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need to know is we can neither read nor write
            externalStorageAvailable = externalStorageWriteable = false;
        }

        return externalStorageAvailable && externalStorageWriteable;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public Collection<File> getLymboFiles() {
        return lymboFiles;
    }

    public void setLymboFiles(Collection<File> lymboFiles) {
        this.lymboFiles = lymboFiles;
    }

    public List<XmlLymbo> getLymbos() {
        return lymbos;
    }

    public void setLymbos(List<XmlLymbo> lymbos) {
        this.lymbos = lymbos;
    }
}
