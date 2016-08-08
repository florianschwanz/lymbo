package de.interoberlin.lymbo.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import de.interoberlin.lymbo.App;
import de.interoberlin.lymbo.R;
import de.interoberlin.lymbo.core.model.v1.impl.EFormat;
import de.interoberlin.lymbo.core.model.v1.impl.ELanguage;
import de.interoberlin.lymbo.core.model.v1.impl.Language;
import de.interoberlin.lymbo.core.model.v1.impl.Stack;
import de.interoberlin.lymbo.core.model.v1.impl.Tag;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboLoader;
import de.interoberlin.lymbo.model.persistence.filesystem.LymboWriter;
import de.interoberlin.lymbo.model.persistence.sqlite.stack.TableStackDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.stack.TableStackEntry;
import de.interoberlin.lymbo.model.webservice.AccessControlItem;
import de.interoberlin.lymbo.model.webservice.web.LymboWebAccessControlItemTask;
import de.interoberlin.lymbo.model.webservice.web.LymboWebDownloadTask;
import de.interoberlin.lymbo.util.ZipUtil;
import de.interoberlin.mate.lib.model.Log;

public class StacksController {
    // <editor-fold defaultstate="collapsed" desc="Members">

    public static final String TAG = StacksController.class.toString();

    // Database
    private TableStackDatasource datasource;

    // Model
    private List<Stack> stacks;
    private List<Stack> stacksStashed;
    private List<Tag> tagsSelected;

    // Properties
    private static String LYMBO_FILE_EXTENSION;
    private static String LYMBOX_FILE_EXTENSION;
    private static String LYMBO_LOOKUP_PATH;
    private static String LYMBO_SAVE_PATH;
    private static String LYMBO_TMP_PATH;
    private static String RECYCLE_PATH;

    private boolean loaded = false;

    private static StacksController instance;

    // </editor-fold>

    // --------------------
    // Constructors
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    private StacksController() {
        init();
    }

    public static StacksController getInstance() {
        if (instance == null) {
            instance = new StacksController();
        }

        return instance;
    }

    // </editor-fold>

    // --------------------
    // Methods
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Methods">

    public void init() {
        stacks = new ArrayList<>();
        stacksStashed = new ArrayList<>();
        tagsSelected = new ArrayList<>();

        // Properties
        LYMBO_FILE_EXTENSION = getResources().getString(R.string.lymbo_file_extension);
        LYMBOX_FILE_EXTENSION = getResources().getString(R.string.lymbox_file_extension);
        LYMBO_LOOKUP_PATH = getResources().getString(R.string.lymbo_lookup_path);
        LYMBO_SAVE_PATH = getResources().getString(R.string.lymbo_save_path);
        LYMBO_TMP_PATH = getResources().getString(R.string.lymbo_tmp_path);
        RECYCLE_PATH = getResources().getString(R.string.recycle_path);
    }

    /**
     * Determines whether a given lymbo shall be displayed considering all filters
     *
     * @param stack lymbo to determine visibility of
     * @return whether lymbo is visible or not
     */
    public boolean isVisible(Stack stack) {
        if (stack != null) {
            Tag noTag = new Tag(getResources().getString(R.string.no_tag));
            boolean includeStacksWithoutTag = noTag.containedInList(getTagsSelected());
            return stack.matchesTag(getTagsSelected(), includeStacksWithoutTag);
        } else {
            return false;
        }
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
    public Stack getEmptyStack(String title, String subtitle, String author, ELanguage languageFrom, ELanguage languageTo, List<Tag> tags) {
        Stack stack = new Stack();
        stack.setId(UUID.randomUUID().toString());
        stack.setTitle(title);
        stack.setSubtitle(subtitle);
        stack.setAuthor(author);
        stack.setTags(tags);

        if (languageFrom != null && languageTo != null) {
            Language language = new Language();
            language.setFrom(languageFrom.getLangCode());
            language.setTo(languageTo.getLangCode());
            stack.setLanguage(language);
        }

        stack.setFile(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH + "/" + title.trim().replaceAll(" ", "_").toLowerCase(Locale.getDefault()) + LYMBO_FILE_EXTENSION);
        stack.setPath(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + LYMBO_SAVE_PATH);
        stack.setFormat(EFormat.LYMBO);

        return stack;
    }

    /**
     * Creates a new lymbo stack
     *
     * @param context context
     * @param stack lymbo to be created
     */
    public void addStack(Context context, Stack stack) {
        stacks.add(stack);
        save(context, stack);
    }

    /**
     * Updates a stack
     *
     * @param context context
     * @param uuid         id of stack to be updated
     * @param title        title
     * @param subtitle     subtitle
     * @param author       author
     * @param languageFrom source language
     * @param languageTo   target language
     */
    public void updateStack(Context context, String uuid, String title, String subtitle, String author, ELanguage languageFrom, ELanguage languageTo, List<Tag> tags) {
        if (lymbosContainsId(uuid)) {
            Stack stack = getLymboById(uuid);

            Stack fullStack = LymboLoader.getLymboFromFile(App.getContext(), new File(stack.getFile()), false);

            if (fullStack != null) {
                stack.setCards(fullStack.getCards());
                stack.setImage(fullStack.getImage());

                stack.setTitle(title);
                stack.setSubtitle(subtitle);
                stack.setAuthor(author);
                stack.setTags(tags);

                if (languageFrom != null && languageTo != null) {
                    Language language = new Language();
                    language.setFrom(languageFrom.getLangCode());
                    language.setTo(languageTo.getLangCode());
                    stack.setLanguage(language);
                }

                save(context, stack);
            }
        }
    }

    /**
     * Stashes a lymbo
     *
     * @param context context
     * @param stack lymbo to be stashed
     */
    public void stash(Context context, Stack stack) {
        getStacks().remove(stack);
        getStacksStashed().add(stack);
        changeState(context, stack.getId(), true);
    }

    /**
     * Restores a lymbo
     *
     * @param context context
     * @param stack lymbo to be stashed
     */
    public void restore(Context context, Stack stack) {
        getStacks().add(stack);
        getStacksStashed().remove(stack);
        changeState(context, stack.getId(), false);
    }

    /**
     * Saves an existing stack
     *
     * @param context context
     * @param stack   stack to be saved
     * @return whether save worked or not
     */
    public boolean save(Context context, Stack stack) {
        if (stack.getFile() != null) {
            switch (stack.getFormat()) {
                case LYMBO: {
                    LymboWriter.writeXml(stack, new File(stack.getFile()));

                    datasource = new TableStackDatasource(context);
                    datasource.open();
                    datasource.updateStackLocation(stack.getId(), stack.getFile(), stack.getPath(), TableStackDatasource.FORMAT_LYMBO);
                    datasource.close();

                    return true;
                }
                case LYMBOX: {
                    LymboWriter.writeXml(stack, new File(stack.getFile()));
                    ZipUtil.zip(new File(stack.getPath()).listFiles(), new File(stack.getPath() + "/" + context.getResources().getString(R.string.lymbo_main_file)));

                    datasource = new TableStackDatasource(context);
                    datasource.open();
                    datasource.updateStackLocation(stack.getId(), stack.getFile(), stack.getPath(), TableStackDatasource.FORMAT_LYMBOX);
                    datasource.close();

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Saves lymbo under a new name
     *
     * @param context context
     * @param stack   lymbo to be saved
     * @param file    new file
     */
    @SuppressWarnings("unused")
    public void saveAs(Context context, Stack stack, String file) {
        switch (stack.getFormat()) {
            case LYMBO: {
                LymboWriter.writeXml(stack, new File(stack.getFile()));

                if (!new File(stack.getFile()).renameTo(new File(file)))
                    return;

                stack.setPath(file);

                datasource = new TableStackDatasource(context);
                datasource.open();
                datasource.updateStackLocation(stack.getId(), stack.getFile(), stack.getPath(), TableStackDatasource.FORMAT_LYMBO);
                datasource.close();
                break;
            }
            case LYMBOX: {
                if (stack.getFile() != null) {
                    stack.setModificationDate(new GregorianCalendar());

                    LymboWriter.writeXml(stack, new File(stack.getFile()));
                    ZipUtil.zip(new File(stack.getPath()).listFiles(), new File(stack.getPath() + "/main.lymbo"));

                    if (!new File(stack.getFile()).renameTo(new File(file)))
                        return;

                    datasource = new TableStackDatasource(context);
                    datasource.open();
                    datasource.updateStackLocation(stack.getId(), stack.getFile(), stack.getPath(), TableStackDatasource.FORMAT_LYMBOX);
                    datasource.close();
                }
            }
        }
    }

    /**
     * Searches for lymbo and lymbox files on storage and saves location in database
     *
     * @param context context
     */
    public void scan(Context context) {
        datasource = new TableStackDatasource(context);
        datasource.open();

        // Scan for *.lymbo and *.lymbox files
        for (File f : findFiles(LYMBO_LOOKUP_PATH, LYMBO_FILE_EXTENSION, LYMBOX_FILE_EXTENSION)) {
            if (!f.getAbsolutePath().contains(LYMBO_TMP_PATH) && !f.getAbsolutePath().contains(RECYCLE_PATH)) {
                Stack stack = LymboLoader.getLymboFromFile(App.getContext(), f, true);

                if (stack != null && !datasource.contains(TableStackDatasource.colFile.getName(), stack.getFile())) {
                    int format = TableStackDatasource.FORMAT_LYMBO;
                    switch (stack.getFormat()) {
                        case LYMBO: {
                            format = TableStackDatasource.FORMAT_LYMBO;
                            break;
                        }
                        case LYMBOX: {
                            format = TableStackDatasource.FORMAT_LYMBOX;
                            break;
                        }
                    }

                    datasource.updateStackLocation(stack.getId(), stack.getFile(), stack.getPath(), format);
                }
            }
        }

        datasource.close();
    }

    /**
     * Loads lymbo files and updates database status if necessary
     *
     * @param context context
     */
    public void load(Context context) {
        datasource = new TableStackDatasource(context);
        datasource.open();

        // Retrieve lymbo files from database table
        Collection<File> lymboFiles = new ArrayList<>();
        Collection<File> lymboFilesStashed = new ArrayList<>();

        datasource.printTable();

        for (TableStackEntry entry : datasource.getEntries()) {
            if (entry.getFile() != null && new File(entry.getFile()).exists() && !entry.getFile().contains(RECYCLE_PATH)) {

                if (entry.isNormal()) {
                    lymboFiles.add(new File(entry.getFile()));
                } else if (entry.isStashed()) {
                    lymboFilesStashed.add(new File(entry.getFile()));
                }
            } else {
                Log.i(TAG, "Deleted not existing " + entry.getFile());
                datasource.deleteStackEntry(entry.getUuid());
            }
        }

        stacks.clear();
        stacks.addAll(getStacksFromAssets(context));
        stacks.addAll(getStacksFromFiles(lymboFiles));

        stacksStashed.clear();
        stacksStashed.addAll(getStacksFromFiles(lymboFilesStashed));

        datasource.close();
        loaded = true;
    }

    public void changeState(Context context, String uuid, boolean stashed) {
        datasource = new TableStackDatasource(context);
        datasource.open();

        if (stashed) {
            datasource.updateStackStateStashed(uuid);
        } else {
            datasource.updateStackStateNormal(uuid);
        }

        datasource.close();
    }

    /**
     * Finds all files that match a certain pattern in a specific directory on the internal storage
     *
     * @param dir        directory to look for files
     * @param extensions extensions that files must have to match
     * @return collection of files
     */
    public Collection<File> findFiles(String dir, String... extensions) {
        String pattern = "";

        for (String e : extensions) {
            pattern += ".*" + e + "|";
        }

        // Remove trailing |
        if (pattern.length() > 0 && pattern.charAt(pattern.length() - 1) == '|') {
            pattern = pattern.substring(0, pattern.length() - 1);
        }

        if (checkStorage()) {
            return FileUtils.listFiles(new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + dir), new RegexFileFilter(pattern), TrueFileFilter.TRUE);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Converts lymbo files into lymbo objects
     */
    private List<Stack> getStacksFromFiles(Collection<File> files) {
        List<Stack> stacks = new ArrayList<>();

        // Add lymbos from file system
        for (File f : files) {
            Stack s = LymboLoader.getLymboFromFile(App.getContext(), f, false);

            if (s != null) {
                Log.i(TAG, "Loaded file " + f.getName());
                stacks.add(s);
            }
        }

        return stacks;
    }

    /**
     * Adds lymbos from assets
     *
     * @param context context
     */
    private List<Stack> getStacksFromAssets(Context context) {
        List<Stack> stacks = new ArrayList<>();

        try {
            for (String asset : Arrays.asList(context.getAssets().list(""))) {
                Stack s = LymboLoader.getLymboFromAsset(context, asset, false);

                if (s != null) {
                    Log.i(TAG, "Loaded asset " + s.getFile());
                    stacks.add(s);
                }
            }
        } catch (IOException ioe) {
            Log.e(TAG, ioe.toString());
        }

        return stacks;
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

    // </editor-fold>

    // --------------------
    // Util
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Util">

    private Resources getResources() {
        return App.getContext().getResources();
    }

    public void addTagsSelected(List<Tag> tags) {
        for (Tag tag : tags) {
            if (!tag.containedInList(tagsSelected)) {
                tagsSelected.add(tag);
            }
        }
    }

    public ArrayList<Tag> getTagsAll() {
        ArrayList<Tag> tagsAll = new ArrayList<>();

        for (Stack stack : getStacks()) {
            for (Tag tag : stack.getTags()) {
                if (tag != null && !tag.containedInList(tagsAll) && !tag.getValue().equals(getResources().getString(R.string.no_tag)))
                    tagsAll.add(tag);
            }
        }

        tagsAll.add(new Tag(getResources().getString(R.string.no_tag)));

        return tagsAll;
    }

    public boolean lymbosContainsId(String uuid) {
        for (Stack l : stacks) {
            if (l != null && l.getId() != null && l.getId().equals(uuid)) {
                return true;
            }
        }

        return false;
    }

    public Stack getLymboById(String uuid) {
        for (Stack l : stacks) {
            if (l != null && l.getId() != null && l.getId().equals(uuid)) {
                return l;
            }
        }

        return null;
    }

    public void download(Context context, LymboWebDownloadTask.OnCompleteListener ocListener, String id) {
        Resources res = getResources();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String username = prefs.getString(res.getString(R.string.pref_lymbo_web_user_name), null);
        String password = prefs.getString(res.getString(R.string.pref_lymbo_web_password), null);
        String clientId = res.getString(R.string.pref_lymbo_web_client_id);
        String clientSecret = prefs.getString(res.getString(R.string.pref_lymbo_web_api_secret), null);

        try {
            AccessControlItem accessControlItem = new LymboWebAccessControlItemTask().execute(username, password, clientId, clientSecret).get();

            if (accessControlItem != null && accessControlItem.getAccess_token() != null) {
                new LymboWebDownloadTask(ocListener).execute(accessControlItem.getAccess_token(), id, username).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // </editor-fold>

    // --------------------
    // Getters ( Setters
    // --------------------

    // <editor-fold defaultstate="collapsed" desc="Getters ( Setters">

    public List<Stack> getStacks() {
        return stacks;
    }

    public List<Stack> getStacksStashed() {
        return stacksStashed;
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

    // </editor-fold>
}