package de.interoberlin.lymbo.controller;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Collection;

import de.interoberlin.mate.lib.model.Log;

public class LymboController extends Application {
    private static Context context;

    private Collection<File> lymboFiles;

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
    public void onCreate()
    {
        super.onCreate();
        context = this;
    }

    // --------------------
    // Methods
    // --------------------

    public static Context getContext()
    {
        return context;
    }
    /**
     * Finds all files having the extension .lymbo
     */
    public void findLymboFiles() {
        Log.trace("LymboController.findLymboFiles()");
        lymboFiles = findFiles(".lymbo");
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

    public boolean checkStorage()
    {
        boolean externalStorageAvailable = false;
        boolean externalStorageWriteable = false;

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            // We can read and write the media
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            // We can only read the media
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else
        {
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
}
