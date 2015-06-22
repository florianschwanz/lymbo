package de.interoberlin.lymbo.model.persistence.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LymboSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lymbo";
    public static final String TABLE_LOCATION = "location";
    public static final String TABLE_NOTE = "note";

    // Table location
    public static final String COL_PATH = "path";
    public static final String COL_STASHED = "stashed";

    // Table note
    public static final String COL_UUID = "uuid";
    public static final String COL_TEXT = "text";

    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_LOCATION_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION + " (" +
                    COL_PATH + " TEXT PRIMARY KEY, " +
                    COL_STASHED + " INTEGER);";
    private static final String TABLE_NOTE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NOTE + " (" + COL_UUID
                    + " TEXT PRIMARY KEY, " + COL_TEXT + " TEXT);";

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
    }
}
