package de.interoberlin.lymbo.controller;

import android.app.Activity;
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
import java.util.List;

import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboLoader;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboWriter;
import de.interoberlin.lymbo.model.persistence.sqlite.LymboSQLiteOpenHelper;
import de.interoberlin.lymbo.model.persistence.sqlite.location.Location;
import de.interoberlin.lymbo.model.persistence.sqlite.location.LocationDatasource;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EProperty;
import de.interoberlin.mate.lib.model.Log;

public class LymbosController {
    // Application
    private App app;

    // Activity
    private Activity activity;

    // Database
    private LocationDatasource datasource;

    // Model
    private List<Lymbo> lymbos;
    private List<Lymbo> lymbosStashed;

    // Properties
    private static String LYMBO_FILE_EXTENSION;
    private static String LYMBO_LOOKUP_PATH;
    private static String LYMBO_SAVE_PATH;

    private boolean loaded = false;

    private static LymbosController instance;

    // --------------------
    // Constructors
    // --------------------

    private LymbosController(Activity activity) {
        this.activity = activity;
        init();
    }

    public static LymbosController getInstance(Activity activity) {
        if (instance == null) {
            instance = new LymbosController(activity);
        }

        return instance;
    }

    // --------------------
    // Methods
    // --------------------

    public void init() {
        app = App.getInstance();

        lymbos = new ArrayList<>();
        lymbosStashed = new ArrayList<>();

        // Properties
        LYMBO_FILE_EXTENSION = Configuration.getProperty(activity, EProperty.LYMBO_FILE_EXTENSION);
        LYMBO_LOOKUP_PATH = Configuration.getProperty(activity, EProperty.LYMBO_LOOKUP_PATH);
        LYMBO_SAVE_PATH = Configuration.getProperty(activity, EProperty.LYMBO_SAVE_PATH);
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
     * Updates a stack
     *
     * @param uuid     id of stack to be updated
     * @param title    title
     * @param subtitle subtitle
     * @param author   author
     */
    public void updateStack(String uuid, String title, String subtitle, String author) {
        if (lymbosContainsId(uuid)) {
            Lymbo lymbo = getLymboById(uuid);

            Lymbo fullLymbo = LymboLoader.getLymboFromFile(new File(lymbo.getPath()), false);

            lymbo.setCards(fullLymbo.getCards());
            lymbo.setHint(fullLymbo.getHint());
            lymbo.setImage(fullLymbo.getImage());

            lymbo.setTitle(title);
            lymbo.setSubtitle(subtitle);
            lymbo.setAuthor(author);

            String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH + "/" + lymbo.getTitle().trim().replaceAll(" ", "_").toLowerCase() + LYMBO_FILE_EXTENSION;

            if (lymbo.getPath().equals(path)) {
                save(lymbo);
            } else {
                saveAs(lymbo, path);
            }
        }
    }

    /**
     * Saves lymbo location in database
     *
     * @param lymbo lymbo to be saved
     */
    public void save(Lymbo lymbo) {
        LymboWriter.createLymboSavePath(new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH));
        LymboWriter.writeXml(lymbo, new File(lymbo.getPath()));

        datasource = new LocationDatasource(activity);
        datasource.open();
        datasource.updateLocation(lymbo.getPath(), false);
        datasource.close();
    }

    /**
     * Saves lymbo under a new name
     *
     * @param lymbo lymbo to be saved
     * @param path  new path
     */
    public void saveAs(Lymbo lymbo, String path) {
        LymboWriter.createLymboSavePath(new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH));
        LymboWriter.writeXml(lymbo, new File(lymbo.getPath()));

        new File(lymbo.getPath()).renameTo(new File(path));
        lymbo.setPath(path);

        datasource = new LocationDatasource(activity);
        datasource.open();
        datasource.updateLocation(lymbo.getPath(), false);
        datasource.close();
    }

    public void scan() {
        datasource = new LocationDatasource(activity);
        datasource.open();

        for (File l : findFiles(LYMBO_FILE_EXTENSION)) {
            String path = l.getAbsolutePath();

            if (!datasource.contains(LymboSQLiteOpenHelper.COL_PATH, path)) {
                datasource.updateLocation(path, false);
            }
        }

        datasource.close();
    }

    /**
     * Loads lymbo files and updates database status
     */
    public void load() {
        datasource = new LocationDatasource(activity);
        datasource.open();

        // Retrieve lymbo files from locations cache
        Collection<File> lymboFiles = new ArrayList<>();
        Collection<File> lymboFilesStashed = new ArrayList<>();

        for (Location l : datasource.getAllLocations()) {
            if (new File(l.getPath()).exists()) {
                Log.info("Loaded " + l.getPath());

                if (l.getStashed() == 0) {
                    lymboFiles.add(new File(l.getPath()));
                } else if (l.getStashed() == 1) {
                    lymboFilesStashed.add(new File(l.getPath()));
                }
            } else {
                Log.info("Deleted not existing " + l.getPath());
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
        datasource = new LocationDatasource(activity);
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
            for (String asset : Arrays.asList(activity.getAssets().list(""))) {
                if (asset.endsWith(LYMBO_FILE_EXTENSION)) {
                    Lymbo l = LymboLoader.getLymboFromAsset(activity, asset, true);
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
     * This is necessary to display the first element below the toolbar
     *
     * @param list list which shall be extended by a leading null element
     */
    public void addNullElement(List<Lymbo> list) {
        if (list != null) {
            list.removeAll(Collections.singleton(null));

            if (!list.isEmpty()) {
                // Add leading null element
                if (list.get(0) != null) {
                    list.add(0, null);
                }
            }
        }
    }

    // --------------------
    // Getters / Setters
    // --------------------

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

    public boolean lymbosContainsId(String uuid) {
        for (Lymbo l : lymbos) {
            if (l != null && l.getId() != null && l.getId().equals(uuid)) {
                return true;
            }
        }

        return false;
    }

    public Lymbo getLymboById(String uuid) {
        for (Lymbo l : lymbos) {
            if (l != null && l.getId() != null && l.getId().equals(uuid)) {
                return l;
            }
        }

        return null;
    }
}