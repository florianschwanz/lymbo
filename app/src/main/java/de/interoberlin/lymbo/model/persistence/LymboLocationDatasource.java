package de.interoberlin.lymbo.model.persistence;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data source for SQLite database table LOCATION
 */
public class LymboLocationDatasource {
    private SQLiteDatabase database;
    private LymboLocationHelper dbHelper;
    private String[] allColumns = {LymboLocationHelper.COL_ID, LymboLocationHelper.COL_LOCATION, LymboLocationHelper.COL_STASHED,
            LymboLocationHelper.COL_DATE};

    // --------------------
    // Constructors
    // --------------------

    public LymboLocationDatasource(Context context) {
        dbHelper = new LymboLocationHelper(context);
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
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes database connection
     */
    public void close() {
        dbHelper.close();
    }

    public boolean contains(String columnName, String value) {
        Cursor cursor = database.query(LymboLocationHelper.TABLE_LOCATION,
                allColumns, columnName + "='" + value + "'", null,
                null, null, null);

        return cursor.getCount() > 0;
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
     * Adds a new entry to table LOCATION
     *
     * @param location value of field location of the new entry
     * @param stashed  value of field stashed of the new entry
     * @return
     */
    public LymboLocation addLocation(String location, int stashed) {
        ContentValues values = new ContentValues();

        values.put(LymboLocationHelper.COL_LOCATION, location);
        values.put(LymboLocationHelper.COL_DATE, new Date().toString());
        values.put(LymboLocationHelper.COL_STASHED, stashed);

        long insertId = database.insert(LymboLocationHelper.TABLE_LOCATION, null,
                values);
        Cursor cursor = database.query(LymboLocationHelper.TABLE_LOCATION,
                allColumns, LymboLocationHelper.COL_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();

        LymboLocation l = cursorToLocation(cursor);
        cursor.close();

        return l;
    }

    /**
     * Deletes an entry identified by {@parm location}
     *
     * @param location entry identified by this location will be deleted
     */
    public void deleteLocation(LymboLocation location) {
        long id = location.getId();
        database.delete(LymboLocationHelper.TABLE_LOCATION, LymboLocationHelper.COL_ID
                + " = " + id, null);
    }

    /**
     * Updates the field STASHED of a location identified by {@param location} to {@param stashed}
     *
     * @param location entry identified by this location will be modified
     * @param stashed  whether or nor the entry shall be marked as stashed
     */
    public void updateLocation(String location, boolean stashed) {
        String statement = "UPDATE " + LymboLocationHelper.TABLE_LOCATION + " SET " + LymboLocationHelper.COL_STASHED + "='" + (stashed ? 1 : 0) + "' WHERE " + LymboLocationHelper.COL_LOCATION + "='" + location + "';";
        database.execSQL(statement);
    }

    /**
     * Updates the field DATE of a location identified by {@param location} to {@param date}
     *
     * @param location entry identified by this location will be modified
     * @param date     new date
     */
    public void updateLocation(String location, Date date) {
        String statement = "UPDATE " + LymboLocationHelper.TABLE_LOCATION + " SET " + LymboLocationHelper.COL_DATE + "='" + date.toString() + "' WHERE " + LymboLocationHelper.COL_LOCATION + "='" + location + "';";
        database.execSQL(statement);
    }

    /**
     * Retrieves a list of all entries of table LOCATION
     *
     * @return list of LymboLocation objects
     */
    public List<LymboLocation> getAllLocations() {
        List<LymboLocation> locations = new ArrayList<>();

        Cursor cursor = database.query(LymboLocationHelper.TABLE_LOCATION,
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
        location.setId(cursor.getLong(0));
        location.setLocation(cursor.getString(1));
        location.setStashed(cursor.getInt(2));
        location.setDate(cursor.getString(3));
        return location;
    }
}
