package de.interoberlin.lymbo.controller;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.persistence.LymboLoader;
import de.interoberlin.mate.lib.model.Log;

public class LymbosController extends Application {
    private static Context context;

    private Collection<File> lymboFiles;
    private List<Lymbo> lymbos;

    private static final String LYMBO_FILE_EXTENSION = ".lymbo";
    private boolean loading = false;

    private static LymbosController instance;

    // --------------------
    // Constructors
    // --------------------

    public LymbosController() {
        init();
    }

    public static LymbosController getInstance() {
        if (instance == null) {
            instance = new LymbosController();
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
    }

    // --------------------
    // Methods
    // --------------------

    public static Context getContext() {
        return context;
    }

    public void init() {
        lymbos = new ArrayList<>();
        lymboFiles = new ArrayList<>();
    }

    public void load() {
        loading = true;
        getLymbosFromAssets();
        findLymboFiles();
        getLymbosFromFiles();
        loading = false;
    }

    /**
     * Finds all files having the extension .lymbo
     */
    private void findLymboFiles() {
        Log.trace("LymboController.findLymboFiles()");
        // lymboFiles = findFiles(LYMBO_FILE_EXTENSION);
    }

    /**
     * Finds all files that match a certain pattern on the internal storage
     *
     * @param pattern file extension
     * @return Collection of files
     */
    public Collection<File> findFiles(String pattern) {
        Log.trace("LymboController.findFiles()");
        return FileUtils.listFiles(Environment.getExternalStorageDirectory(), new RegexFileFilter(".*" + pattern), TrueFileFilter.TRUE);
    }

    /**
     * Converts lymbo files into lymbo objects
     */
    private void getLymbosFromFiles() {
        // Add lymbos from file system
        for (File f : lymboFiles) {
            try {
                Lymbo l = LymboLoader.getLymboFromFile(new FileInputStream(f));
                l.setPath(f.getAbsolutePath());
                lymbos.add(l);
                Log.debug("Found lymbo " + f.getName());
            } catch (FileNotFoundException fnfe) {
                Log.error(fnfe.toString());
            }
        }
    }

    private void getLymbosFromAssets() {
        lymbos.add(LymboLoader.getLymboFromAsset(context, "spanish-a1.lymbo"));
        lymbos.add(LymboLoader.getLymboFromAsset(context, "demo.lymbo"));
        lymbos.add(LymboLoader.getLymboFromAsset(context, "java.lymbo"));
    }

    public boolean checkStorage() {
        boolean externalStorageAvailable;
        boolean externalStorageWriteable;

        String state = Environment.getExternalStorageState();

        switch (state) {
            case Environment.MEDIA_MOUNTED: {
                // We can read and write the media
                externalStorageAvailable = externalStorageWriteable = true;
                break;
            }
            case Environment.MEDIA_MOUNTED_READ_ONLY: {
                // We can only read the media
                externalStorageAvailable = true;
                externalStorageWriteable = false;
                break;
            }
            default: {
                // Something else is wrong. It may be one of many other states, but
                // all we need to know is we can neither read nor write
                externalStorageAvailable = externalStorageWriteable = false;
            }
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

    public List<Lymbo> getLymbos() {
        return lymbos;
    }

    public void setLymbos(List<Lymbo> lymbos) {
        this.lymbos = lymbos;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}