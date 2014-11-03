package de.interoberlin.lymbo.controller;

import android.app.Application;
import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Collection;

import de.interoberlin.mate.lib.model.Log;

public class LymboController extends Application {
    private Collection<File> lymboFiles;

    private static LymboController instance;

    // --------------------
    // Singleton
    // --------------------

    private LymboController() {
    }

    public static LymboController getInstance() {
        if (instance == null) {
            instance = new LymboController();
        }

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

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
