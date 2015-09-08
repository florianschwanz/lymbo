package de.interoberlin.lymbo.controller;

import android.app.Activity;
import android.content.res.Resources;
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
import java.util.Locale;

import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.model.card.Lymbo;
import de.interoberlin.lymbo.model.card.Tag;
import de.interoberlin.lymbo.model.card.aspects.LanguageAspect;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboLoader;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboWriter;
import de.interoberlin.lymbo.model.persistence.sqlite.stack.TableStackDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.stack.TableStackEntry;
import de.interoberlin.lymbo.model.translate.Language;
import de.interoberlin.mate.lib.model.Log;

public class LymbosController {
    // Activity
    private Activity activity;

    // Database
    private TableStackDatasource datasource;

    // Model
    private List<Lymbo> lymbos;
    private List<Lymbo> lymbosStashed;

    private List<Tag> tagsSelected;

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
        lymbos = new ArrayList<>();
        lymbosStashed = new ArrayList<>();
        tagsSelected = new ArrayList<>();

        // Properties
        LYMBO_FILE_EXTENSION = getResources().getString(R.string.lymbo_file_extension);
        LYMBO_LOOKUP_PATH = getResources().getString(R.string.lymbo_lookup_path);
        LYMBO_SAVE_PATH = getResources().getString(R.string.lymbo_save_path);
    }

    /**
     * Determines whether a given lymbo shall be displayed considering all filters
     *
     * @param lymbo lymbo to determine visibility of
     * @return whether lymbo is visbible or not
     */
    public boolean isVisible(Lymbo lymbo) {
        return (lymbo != null &&
                lymbo.matchesTag(getTagsSelected()));
    }

    /**
     * Returns an empty lymbo stack
     *
     * @param title        title of new stack
     * @param subtitle     subtitle of new stack
     * @param author       author of new stack
     * @param languageFrom source language
     * @param languageTo   target language
     * @return an empty lymbo
     */
    public Lymbo getEmptyLymbo(String title, String subtitle, String author, Language languageFrom, Language languageTo, List<Tag> tags) {
        Lymbo lymbo = new Lymbo();
        lymbo.setTitle(title);
        lymbo.setSubtitle(subtitle);
        lymbo.setAuthor(author);
        lymbo.setTags(tags);

        if (languageFrom != null && languageTo != null) {
            LanguageAspect languageAspect = new LanguageAspect();
            languageAspect.setFrom(languageFrom);
            languageAspect.setTo(languageTo);
            lymbo.setLanguageAspect(languageAspect);
        }

        lymbo.setPath(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH + "/" + title.trim().replaceAll(" ", "_").toLowerCase(Locale.getDefault()) + LYMBO_FILE_EXTENSION);

        return lymbo;
    }

    /**
     * Creates a new lymbo stack
     *
     * @param lymbo lymbo to be created
     */
    public void addStack(Lymbo lymbo) {
        lymbos.add(lymbo);
        save(lymbo);
    }

    /**
     * Updates a stack
     *
     * @param uuid         id of stack to be updated
     * @param title        title
     * @param subtitle     subtitle
     * @param author       author
     * @param languageFrom source language
     * @param languageTo   target language
     */
    public void updateStack(String uuid, String title, String subtitle, String author, Language languageFrom, Language languageTo, List<Tag> tags) {
        if (lymbosContainsId(uuid)) {
            Lymbo lymbo = getLymboById(uuid);

            Lymbo fullLymbo = LymboLoader.getLymboFromFile(new File(lymbo.getPath()), false);

            if (fullLymbo != null) {
                lymbo.setCards(fullLymbo.getCards());
                lymbo.setHint(fullLymbo.getHint());
                lymbo.setImage(fullLymbo.getImage());

                lymbo.setTitle(title);
                lymbo.setSubtitle(subtitle);
                lymbo.setAuthor(author);
                lymbo.setTags(tags);

                if (languageFrom != null && languageTo != null) {
                    LanguageAspect languageAspect = new LanguageAspect();
                    languageAspect.setFrom(languageFrom);
                    languageAspect.setTo(languageTo);
                    lymbo.setLanguageAspect(languageAspect);
                }

                String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH + "/" + lymbo.getTitle().trim().replaceAll(" ", "_").toLowerCase(Locale.getDefault()) + LYMBO_FILE_EXTENSION;

                if (lymbo.getPath().equals(path)) {
                    save(lymbo);
                } else {
                    saveAs(lymbo, path);
                }
            }
        }
    }

    /**
     * Stashes a lymbo
     *
     * @param lymbo lymbo to be stashed
     */
    public void stash(Lymbo lymbo) {
        getLymbos().remove(lymbo);
        getLymbosStashed().add(lymbo);
        changeState(lymbo.getId(), true);
    }

    /**
     * Restores a lymbo
     *
     * @param lymbo lymbo to be stashed
     */
    public void restore(Lymbo lymbo) {
        getLymbos().add(lymbo);
        getLymbosStashed().remove(lymbo);
        changeState(lymbo.getId(), false);
    }

    /**
     * Saves lymbo location in database
     *
     * @param lymbo lymbo to be saved
     * @return whether save worked or not
     */
    public boolean save(Lymbo lymbo) {
        if (lymbo.getPath() != null) {
            lymbo.setModificationDate(new Date().toString());

            LymboWriter.createLymboSavePath(new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH));
            LymboWriter.writeXml(lymbo, new File(lymbo.getPath()));

            datasource = new TableStackDatasource(activity);
            datasource.open();
            datasource.updateStackLocation(lymbo.getId(), lymbo.getPath());
            datasource.close();

            return true;
        }

        return false;
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

        datasource = new TableStackDatasource(activity);
        datasource.open();
        datasource.updateStackLocation(lymbo.getId(), lymbo.getPath());
        datasource.close();
    }

    /**
     * Searches for lymbo files on storage
     */
    public void scan() {
        datasource = new TableStackDatasource(activity);
        datasource.open();

        for (File f : findFiles(LYMBO_FILE_EXTENSION)) {
            Lymbo lymbo = LymboLoader.getLymboFromFile(f, true);


            if (lymbo != null && !datasource.contains(TableStackDatasource.colPath.getName(), lymbo.getPath())) {
                datasource.updateStackLocation(lymbo.getId(), lymbo.getPath());
            }
        }

        datasource.close();
    }

    /**
     * Loads lymbo files and updates databasestatus
     */
    public void load() {
        datasource = new TableStackDatasource(activity);
        datasource.open();

        // Retrieve lymbo files from locations cache
        Collection<File> lymboFiles = new ArrayList<>();
        Collection<File> lymboFilesStashed = new ArrayList<>();

        for (TableStackEntry entry : datasource.getEntries()) {
            if (entry.getPath() != null && new File(entry.getPath()).exists()) {
                Log.info("Loaded " + entry.getPath());

                if (entry.isNormal()) {
                    lymboFiles.add(new File(entry.getPath()));
                } else if (entry.isStashed()) {
                    lymboFilesStashed.add(new File(entry.getPath()));
                }
            } else {
                Log.info("Deleted not existing " + entry.getPath());
                datasource.deleteStackEntry(entry.getUuid());
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

    public void changeState(String uuid, boolean stashed) {
        datasource = new TableStackDatasource(activity);
        datasource.open();

        if (stashed) {
            datasource.updateStackStateStashed(uuid);
        } else {
            datasource.updateStackStateNormal(uuid);
        }

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
            Lymbo l = LymboLoader.getLymboFromFile(f, false);
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
                    Lymbo l = LymboLoader.getLymboFromAsset(activity, asset, false);
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
    // Methods - Util
    // --------------------

    private Resources getResources() {
        return activity.getResources();
    }

    public void addTagsSelected(List<Tag> tags) {
        for (Tag tag : tags) {
            if (!tag.containedIn(tagsSelected)) {
                tagsSelected.add(tag);
            }
        }
    }

    public ArrayList<Tag> getTagsAll() {
        ArrayList<Tag> tagsAll = new ArrayList<>();

        for (Lymbo lymbo : getLymbos()) {
            for (Tag tag : lymbo.getTags()) {
                if (tag != null && !tag.containedIn(tagsAll) && tag.getName() != getResources().getString(R.string.no_tag))
                    tagsAll.add(tag);
            }
        }

        tagsAll.add(new Tag(getResources().getString(R.string.no_tag)));

        return tagsAll;
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

    // --------------------
    // Getters / Setters
    // --------------------

    public List<Lymbo> getLymbos() {
        return lymbos;
    }

    public List<Lymbo> getLymbosStashed() {
        return lymbosStashed;
    }

    public List<Tag> getTagsSelected() {
        return tagsSelected;
    }

    public void setTagsSelected(List<Tag> tagsSelected) {
        this.tagsSelected = tagsSelected;
    }

    public boolean isLoaded() {
        return loaded;
    }
}