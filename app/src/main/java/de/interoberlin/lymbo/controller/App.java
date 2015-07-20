package de.interoberlin.lymbo.controller;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import de.interoberlin.lymbo.model.persistence.sqlite.LymboSQLiteOpenHelper;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.TableCardDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.stack.TableStackDatasource;
import de.interoberlin.lymbo.util.Configuration;
import de.interoberlin.lymbo.util.EProperty;

public class App extends Application {
    // Context
    private static Context context;

    // Database
    private static SQLiteDatabase sqliteDatabase;
    private static LymboSQLiteOpenHelper sqliteOpenLymboSQLiteOpenHelper;

    // Properties
    private static String LYMBO_SAVE_PATH;

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

        // Database
        sqliteOpenLymboSQLiteOpenHelper = new LymboSQLiteOpenHelper(this);
        if (sqliteOpenLymboSQLiteOpenHelper != null) {
            sqliteDatabase = sqliteOpenLymboSQLiteOpenHelper.getWritableDatabase();
            recreateOnSchemaChange();
        }

        // Properties
        readProperties();
    }

    private void readProperties() {
        LYMBO_SAVE_PATH = Configuration.getProperty(this, EProperty.LYMBO_SAVE_PATH);
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

    public static SQLiteDatabase getSqliteDatabase() {
        return sqliteDatabase;
    }

    public static LymboSQLiteOpenHelper getSqliteOpenLymboSQLiteOpenHelper() {
        return sqliteOpenLymboSQLiteOpenHelper;
    }
}
