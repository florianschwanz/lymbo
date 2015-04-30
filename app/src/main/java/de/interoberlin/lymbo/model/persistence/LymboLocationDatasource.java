package de.interoberlin.lymbo.model.persistence;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

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

    public void deleteLocation(LymboLocation location) {
        long id = location.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(LymboLocationHelper.TABLE_LOCATION, LymboLocationHelper.COL_ID
                + " = " + id, null);
    }

    public void clear() {
        for (LymboLocation l : getAllLocations()) {
            deleteLocation(l);
        }
    }

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
        // make sure to close the cursor
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
