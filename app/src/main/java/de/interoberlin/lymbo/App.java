package de.interoberlin.lymbo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import de.interoberlin.lymbo.model.persistence.sqlite.LymboSQLiteOpenHelper;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.TableCardDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.stack.TableStackDatasource;
import de.interoberlin.lymbo.model.webservice.translate.MicrosoftAccessControlItemTask;
import de.interoberlin.lymbo.model.webservice.web.LymboWebAccessControlItemTask;

public class App extends Application {
    // Context
    private static Context context;

    // Database
    private static SQLiteDatabase sqliteDatabase;
    private static LymboSQLiteOpenHelper sqliteOpenLymboSQLiteOpenHelper;

    private boolean giveFeedbackDialogActive = false;

    private static App instance;

    // --------------------
    // Constructors
    // --------------------

    public App() {
    }

    public static App getInstance() {
        if (instance == null) {
            instance = new App();
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

        String tmpPath = getResources().getString(R.string.lymbo_tmp_path);
        try {
            FileUtils.deleteDirectory(new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + tmpPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Database
        sqliteOpenLymboSQLiteOpenHelper = new LymboSQLiteOpenHelper(this);
        sqliteDatabase = sqliteOpenLymboSQLiteOpenHelper.getWritableDatabase();
        recreateOnSchemaChange();

        // Access tokens
        Resources res = getResources();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            String clientId = res.getString(R.string.pref_translator_client_id);
            String clientSecret = prefs.getString(res.getString(R.string.pref_translator_api_secret), null);

            new MicrosoftAccessControlItemTask().execute(clientId, clientSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String username = prefs.getString(res.getString(R.string.pref_lymbo_web_user_name), null);
            String password = prefs.getString(res.getString(R.string.pref_lymbo_web_password), null);
            String clientId = res.getString(R.string.pref_lymbo_web_client_id);
            String clientSecret = prefs.getString(res.getString(R.string.pref_lymbo_web_api_secret), null);

            new LymboWebAccessControlItemTask().execute(username, password, clientId, clientSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        if (sqliteDatabase != null) {
            sqliteDatabase.close();
        }

        if (sqliteOpenLymboSQLiteOpenHelper != null) {
            sqliteOpenLymboSQLiteOpenHelper.close();
        }

        super.onTerminate();
    }

    /**
     * Recreates database tables when schema has changed
     */
    public void recreateOnSchemaChange() {
        TableStackDatasource dsLocation = new TableStackDatasource(getApplicationContext());
        dsLocation.open();
        dsLocation.recreateOnSchemaChange();
        dsLocation.close();

        TableCardDatasource dsCardState = new TableCardDatasource(getApplicationContext());
        dsCardState.open();
        dsCardState.recreateOnSchemaChange();
        dsCardState.close();

        TableStackDatasource dsStackState = new TableStackDatasource(getApplicationContext());
        dsStackState.open();
        dsStackState.recreateOnSchemaChange();
        dsStackState.close();
    }

    // --------------------
    // Methods
    // --------------------

    public static Context getContext() {
        return context;
    }

    // --------------------
    // Getters / Setters
    // --------------------

    public boolean isGiveFeedbackDialogActive() {
        return giveFeedbackDialogActive;
    }

    public void setGiveFeedbackDialogActive(boolean giveFeedbackDialogActive) {
        this.giveFeedbackDialogActive = giveFeedbackDialogActive;
    }
}
