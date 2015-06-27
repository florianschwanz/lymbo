package de.interoberlin.lymbo.model.persistence.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LymboSQLiteOpenHelper extends SQLiteOpenHelper {
    // Database
    private static final String DATABASE_NAME = "lymbo";

    // Tables
    public static final String TABLE_LOCATION = "location";
    public static final String TABLE_NOTE = "note";
    public static final String TABLE_CARDSTATE = "cardState";

    // Columns
    public static final String COL_PATH = "path";
    public static final String COL_STASHED = "stashed";
    public static final String COL_UUID = "uuid";
    public static final String COL_TEXT = "text";

    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_LOCATION_DROP = "DROP TABLE " + TABLE_LOCATION + ";";
    private static final String TABLE_LOCATION_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION + " (" +
                    COL_PATH + " TEXT PRIMARY KEY, " +
                    COL_STASHED + " INTEGER);";

    private static final String TABLE_NOTE_DROP = "DROP TABLE " + TABLE_NOTE + ";";
    private static final String TABLE_NOTE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NOTE + " (" + COL_UUID
                    + " TEXT PRIMARY KEY, " + COL_TEXT + " TEXT);";

    private static final String TABLE_CARDSTATE_DROP = "DROP TABLE " + TABLE_CARDSTATE + ";";
    private static final String TABLE_CARDSTATE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CARDSTATE + " (" + COL_UUID
                    + " TEXT PRIMARY KEY, " + COL_STASHED + " INTEGER);";

    public LymboSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // --------------------
    // Methods - Lifecycle
    // --------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDB, int oldVersion, int newVersion) {

    }

    // --------------------
    // Methods
    // --------------------

    public void createTables(SQLiteDatabase db) {
        db.execSQL(TABLE_LOCATION_CREATE);
        db.execSQL(TABLE_NOTE_CREATE);
        db.execSQL(TABLE_CARDSTATE_CREATE);
    }

    public void recreateTableLocation(SQLiteDatabase db) {
        db.execSQL(TABLE_LOCATION_DROP);
        db.execSQL(TABLE_LOCATION_CREATE);
    }

    public void recreateTableNote(SQLiteDatabase db) {
        db.execSQL(TABLE_NOTE_DROP);
        db.execSQL(TABLE_NOTE_CREATE);
    }

    public void recreateTableCardState(SQLiteDatabase db) {
        db.execSQL(TABLE_CARDSTATE_DROP);
        db.execSQL(TABLE_CARDSTATE_CREATE);
    }
}
