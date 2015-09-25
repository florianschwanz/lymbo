package de.interoberlin.lymbo;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

import de.interoberlin.lymbo.model.persistence.sqlite.LymboSQLiteOpenHelper;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.TableCardDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.stack.TableStackDatasource;

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
        new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + tmpPath).delete();

        // Database
        sqliteOpenLymboSQLiteOpenHelper = new LymboSQLiteOpenHelper(this);
        sqliteDatabase = sqliteOpenLymboSQLiteOpenHelper.getWritableDatabase();
        recreateOnSchemaChange();
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
