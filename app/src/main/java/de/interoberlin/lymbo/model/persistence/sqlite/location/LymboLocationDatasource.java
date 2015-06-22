package de.interoberlin.lymbo.model.persistence.sqlite.location;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.persistence.sqlite.LymboSQLiteOpenHelper;

/**
 * Data source for SQLite database table LOCATION
 */
public class LymboLocationDatasource {
    private SQLiteDatabase database;
    private LymboSQLiteOpenHelper dbLymboSQLiteOpenHelper;
    private String[] allColumns = {LymboSQLiteOpenHelper.COL_PATH, LymboSQLiteOpenHelper.COL_STASHED};

    // --------------------
    // Constructors
    // --------------------

    public LymboLocationDatasource(Context context) {
        dbLymboSQLiteOpenHelper = new LymboSQLiteOpenHelper(context);
    }

    // --------------------
    // Methods
    // --------------------

    /**
     * Opens database connection
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbLymboSQLiteOpenHelper.getWritableDatabase();
        dbLymboSQLiteOpenHelper.createTables(database);
    }

    /**
     * Closes database connection
     */
    public void close() {
        dbLymboSQLiteOpenHelper.close();
    }

    public boolean contains(String columnName, String value) {
        Cursor cursor = database.query(LymboSQLiteOpenHelper.TABLE_LOCATION,
                allColumns, columnName + "='" + value + "'", null,
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    /**
     * Deletes all entries from table LOCATION
     */
    public void clear() {
        for (LymboLocation l : getAllLocations()) {
            deleteLocation(l);
        }
    }

    /**
     * Deletes an entry identified by {@parm location}
     *
     * @param location entry identified by this location will be deleted
     */
    public void deleteLocation(LymboLocation location) {
        String path = location.getPath();
        database.delete(LymboSQLiteOpenHelper.TABLE_LOCATION, LymboSQLiteOpenHelper.COL_PATH
                + " = " + path, null);
    }

    /**
     * Updates the field STASHED of a location identified by {@param location} to {@param stashed}
     *
     * @param path entry identified by this path will be modified
     * @param stashed  whether or nor the entry shall be marked as stashed
     */
    public void updateLocation(String path, boolean stashed) {
        String statement = "INSERT OR REPLACE INTO " + LymboSQLiteOpenHelper.TABLE_LOCATION + " (" + LymboSQLiteOpenHelper.COL_PATH + ", " + LymboSQLiteOpenHelper.COL_STASHED + ") VALUES ('" + path + "', '" + (stashed ? 1 : 0) + "');";
        database.execSQL(statement);
    }

    /**
     * Retrieves a list of all entries of table LOCATION
     *
     * @return list of LymboLocation objects
     */
    public List<LymboLocation> getAllLocations() {
        List<LymboLocation> locations = new ArrayList<>();

        Cursor cursor = database.query(LymboSQLiteOpenHelper.TABLE_LOCATION,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LymboLocation location = cursorToLocation(cursor);
            locations.add(location);
            cursor.moveToNext();
        }

        cursor.close();

        return locations;
    }

    private LymboLocation cursorToLocation(Cursor cursor) {
        LymboLocation location = new LymboLocation();
        location.setPath(cursor.getString(0));
        location.setStashed(cursor.getInt(1));
        return location;
    }
}
