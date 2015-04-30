package de.interoberlin.lymbo.model.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LymboLocationHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lymbo";
    public static final String TABLE_LOCATION = "location";

    public static final String COL_ID = "id";
    public static final String COL_LOCATION = "location";
    public static final String COL_STASHED = "stashed";
    public static final String COL_DATE = "date";

    private static final int DATABASE_VERSION = 3;

    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + TABLE_LOCATION + " (" + COL_ID
                    + " integer primary key autoincrement, " +
                    COL_LOCATION + " TEXT NOT NULL, " +
                    COL_STASHED + " INTEGER, " +
                    COL_DATE + " TEXT NOT NULL);";

    public LymboLocationHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDB, int oldVersion, int newVersion) {

    }
}
