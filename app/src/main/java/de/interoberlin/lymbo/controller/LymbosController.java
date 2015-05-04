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
import java.util.Date;
import java.util.List;

import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.persistence.LymboLoader;
import de.interoberlin.lymbo.model.persistence.LymboLocation;
import de.interoberlin.lymbo.model.persistence.LymboLocationDatasource;
import de.interoberlin.lymbo.model.persistence.LymboLocationHelper;
import de.interoberlin.mate.lib.model.Log;

public class LymbosController extends Application {
    private static Context context;

    private List<Lymbo> lymbos;
    private List<Lymbo> lymbosStashed;

    private static final String LYMBO_FILE_EXTENSION = ".lymbo";
    private static final String LYMBO_DIR = "";
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
    }

    // --------------------
    // Methods
    // --------------------

    public static Context getContext() {
        return context;
    }

    public void init() {
        lymbos = new ArrayList<>();
        lymbosStashed = new ArrayList<>();
    }

    public void scan() {
        datasource = new LymboLocationDatasource(context);
        datasource.open();

        datasource.printLocations();

        for (File l : findFiles(LYMBO_FILE_EXTENSION)) {
            String location = l.getAbsolutePath();

            if (datasource.contains(LymboLocationHelper.COL_LOCATION, location)) {
                datasource.updateLocation(location, new Date());
            } else {
                datasource.addLocation(location, 0);
            }
        }
    }

    public void load() {
        datasource = new LymboLocationDatasource(context);
        datasource.open();

        // Scan for lymbo files if no files are known
        if (datasource.getAllLocations().isEmpty()) {
            scan();
        }

        datasource.printLocations();

        // Retrieve lymbo files from locations cache
        Collection<File> lymboFiles = new ArrayList<>();
        Collection<File> lymboFilesStashed = new ArrayList<>();

        for (LymboLocation l : datasource.getAllLocations()) {
            if (l.getStashed() == 0) {
                lymboFiles.add(new File(l.getLocation()));
            } else if (l.getStashed() == 1) {
                lymboFilesStashed.add(new File(l.getLocation()));
            }
        }

        lymbos.clear();
        lymbos.addAll(getLymbosFromAssets());
        lymbos.addAll(getLymbosFromFiles(lymboFiles));

        lymbosStashed.clear();
        lymbosStashed.addAll(getLymbosFromFiles(lymboFilesStashed));

        datasource.close();
        loaded = true;
    }

    public void changeLocation(String location, boolean stashed) {
        datasource = new LymboLocationDatasource(context);
        datasource.open();

        datasource.printLocations();

        datasource.updateLocation(location, stashed);

        datasource.printLocations();

        datasource.close();
    }

    /**
     * Finds all files that match a certain pattern on the internal storage
     *
     * @param pattern file extension
     * @return Collection of files
     */
    public Collection<File> findFiles(String pattern) {
        return findFiles(pattern, LYMBO_DIR);
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
            Lymbo l = LymboLoader.getLymboFromFile(f);
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
                    Lymbo l = LymboLoader.getLymboFromAsset(context, asset);
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

    // --------------------
    // Getters / Setters
    // --------------------

    public List<Lymbo> getLymbos() {
        return lymbos;
    }

    public List<Lymbo> getLymbosStashed() {
        return lymbosStashed;
    }

    public boolean isLoaded() {
        return loaded;
    }
}