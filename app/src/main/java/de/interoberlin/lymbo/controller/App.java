package de.interoberlin.lymbo.controller;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import de.interoberlin.lymbo.model.persistence.sqlite.LymboSQLiteOpenHelper;
import de.interoberlin.lymbo.model.persistence.sqlite.cards.CardStateDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.location.LocationDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.notes.NoteDatasource;

public class App extends Application {
    // Context
    private static Context context;

    // Database
    private static SQLiteDatabase sqliteDatabase;
    private static LymboSQLiteOpenHelper sqliteOpenLymboSQLiteOpenHelper;

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
        LocationDatasource dsLocation = new LocationDatasource(getApplicationContext());
        dsLocation.open();
        dsLocation.recreateOnSchemaChange();
        dsLocation.close();

        NoteDatasource dsNote = new NoteDatasource(getApplicationContext());
        dsNote.open();
        dsNote.recreateOnSchemaChange();
        dsNote.close();

        CardStateDatasource dsCardState = new CardStateDatasource(getApplicationContext());
        dsCardState.open();
        dsCardState.recreateOnSchemaChange();
        dsCardState.close();
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
