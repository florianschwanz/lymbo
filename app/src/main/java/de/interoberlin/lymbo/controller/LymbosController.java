package de.interoberlin.lymbo.controller;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.persistence.LymboLoader;
import de.interoberlin.lymbo.model.persistence.LymboLocation;
import de.interoberlin.lymbo.model.persistence.LymboLocationDatasource;
import de.interoberlin.lymbo.model.persistence.LymboLocationHelper;
import de.interoberlin.lymbo.model.persistence.LymboWriter;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EProperty;
import de.interoberlin.mate.lib.model.Log;

public class LymbosController extends Application {
    private static Context context;

    private List<Lymbo> lymbos;
    private List<Lymbo> lymbosStashed;

    private static String LYMBO_FILE_EXTENSION;
    private static String LYMBO_LOOKUP_PATH;
    private static String LYMBO_SAVE_PATH;

    private boolean loaded = false;

    private LymboLocationDatasource datasource;

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

        LYMBO_FILE_EXTENSION = Configuration.getProperty(this, EProperty.LYMBO_FILE_EXTENSION);
        LYMBO_LOOKUP_PATH = Configuration.getProperty(this, EProperty.LYMBO_LOOKUP_PATH);
        LYMBO_SAVE_PATH = Configuration.getProperty(this, EProperty.LYMBO_SAVE_PATH);
    }

    // --------------------
    // Methods
    // --------------------

    public void init() {
        lymbos = new ArrayList<>();
        lymbosStashed = new ArrayList<>();
    }

    /**
     * Returns an empty lymbo stack
     *
     * @param title    title of new stack
     * @param subtitle subtitle of new stack
     * @param author   author of new stack
     * @return empty lymbo stack
     */
    public Lymbo getEmptyLymbo(String title, String subtitle, String author) {
        Lymbo lymbo = new Lymbo();
        lymbo.setTitle(title);
        lymbo.setSubtitle(subtitle);
        lymbo.setAuthor(author);
        lymbo.setPath(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH + "/" + title.trim().replaceAll(" ", "_").toLowerCase() + LYMBO_FILE_EXTENSION);

        return lymbo;
    }

    /**
     * Creates a new lymbo stack
     *
     * @param lymbo lymbo to be created
     */
    public void addStack(Lymbo lymbo) {
        lymbos.add(lymbo);
        addNullElement(lymbos);
        save(lymbo);
    }

    /**
     * Saves lymbo location in database
     */
    public void save(Lymbo lymbo) {
        LymboWriter.createLymboSavePath(new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH));
        LymboWriter.writeXml(lymbo, new File(lymbo.getPath()));

        datasource = new LymboLocationDatasource(context);
        datasource.open();
        datasource.addLocation(lymbo.getPath(), 0);
        datasource.close();
    }

    public void scan() {
        datasource = new LymboLocationDatasource(context);
        datasource.open();

        for (File l : findFiles(LYMBO_FILE_EXTENSION)) {
            String location = l.getAbsolutePath();

            if (datasource.contains(LymboLocationHelper.COL_LOCATION, location)) {
                datasource.updateLocation(location, new Date());
            } else {
                datasource.addLocation(location, 0);
            }
        }

        datasource.close();
    }

    public void load() {
        datasource = new LymboLocationDatasource(context);
        datasource.open();

        // Retrieve lymbo files from locations cache
        Collection<File> lymboFiles = new ArrayList<>();
        Collection<File> lymboFilesStashed = new ArrayList<>();

        for (LymboLocation l : datasource.getAllLocations()) {
            if (new File(l.getLocation()).exists()) {
                if (l.getStashed() == 0) {
                    lymboFiles.add(new File(l.getLocation()));
                } else if (l.getStashed() == 1) {
                    lymboFilesStashed.add(new File(l.getLocation()));
                }
            } else {
                datasource.deleteLocation(l);
            }
        }

        lymbos.clear();
        lymbos.addAll(getLymbosFromAssets());
        lymbos.addAll(getLymbosFromFiles(lymboFiles));
        addNullElement(lymbos);

        lymbosStashed.clear();
        lymbosStashed.addAll(getLymbosFromFiles(lymboFilesStashed));
        addNullElement(lymbosStashed);

        datasource.close();
        loaded = true;
    }

    public void changeLocation(String location, boolean stashed) {
        datasource = new LymboLocationDatasource(context);
        datasource.open();
        datasource.updateLocation(location, stashed);
        datasource.close();
    }

    /**
     * Finds all files that match a certain pattern on the internal storage
     *
     * @param pattern file extension
     * @return Collection of files
     */
    public Collection<File> findFiles(String pattern) {
        return findFiles(pattern, LYMBO_LOOKUP_PATH);
    }

    /**
     * Finds all files that match a certain pattern in a specific directory on the internal storage
     *
     * @param pattern pattern that files have to match
     * @param dir     directory to look for files
     * @return collection of files
     */
    public Collection<File> findFiles(String pattern, String dir) {
        Log.trace("LymbosController.findFiles()");
        if (checkStorage()) {
            return FileUtils.listFiles(new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + dir), new RegexFileFilter(".*" + pattern), TrueFileFilter.TRUE);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Converts lymbo files into lymbo objects
     */
    private List<Lymbo> getLymbosFromFiles(Collection<File> files) {
        List<Lymbo> lymbos = new ArrayList<>();

        // Add lymbos from file system
        for (File f : files) {
            Lymbo l = LymboLoader.getLymboFromFile(f, true);
            if (l != null) {
                lymbos.add(l);
                Log.debug("Found lymbo " + f.getName());
            }
        }

        return lymbos;
    }

    /**
     * Adds lymbos from assets
     */
    private List<Lymbo> getLymbosFromAssets() {
        List<Lymbo> lymbos = new ArrayList<>();

        try {
            for (String asset : Arrays.asList(context.getAssets().list(""))) {
                if (asset.endsWith(LYMBO_FILE_EXTENSION)) {
                    Lymbo l = LymboLoader.getLymboFromAsset(context, asset, true);
                    if (l != null) {
                        lymbos.add(l);
                    }
                }
            }
        } catch (IOException ioe) {
            Log.fatal(ioe.toString());
        }

        return lymbos;
    }

    /**
     * Checks if storage is available
     *
     * @return true if storage is available
     */
    private boolean checkStorage() {
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

    /**
     * This is necessary to display the first element below the toolbar and to leave enough space at
     * the bottom for the floating action point not to cover other elements
     *
     * @param list list which shall be extended by a leading and trailing null element
     */
    public void addNullElement(List<Lymbo> list) {
        if (!list.isEmpty()) {
            list.removeAll(Collections.singleton(null));

            // Add leading null element
            if (list.get(0) != null) {
                list.add(0, null);
            }

            // Add trailling null element
            if (list.get(list.size() - 1) != null) {
                list.add(null);
            }
        }
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public Context getContext() {
        return context;
    }

    public List<Lymbo> getLymbos() {
        addNullElement(lymbos);

        return lymbos;
    }

    public List<Lymbo> getLymbosStashed() {
        addNullElement(lymbosStashed);

        return lymbosStashed;
    }

    public boolean isLoaded() {
        return loaded;
    }
}