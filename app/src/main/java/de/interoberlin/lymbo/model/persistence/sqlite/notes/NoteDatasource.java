package de.interoberlin.lymbo.model.persistence.sqlite.notes;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.persistence.sqlite.LymboSQLiteOpenHelper;

/**
 * Data source for SQLite database table NOTE
 */
public class NoteDatasource {
    private SQLiteDatabase database;
    private LymboSQLiteOpenHelper dbLymboSQLiteOpenHelper;
    private String[] allColumns = {LymboSQLiteOpenHelper.COL_UUID, LymboSQLiteOpenHelper.COL_TEXT};

    // --------------------
    // Constructors
    // --------------------

    public NoteDatasource(Context context) {
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
        Cursor cursor = database.query(LymboSQLiteOpenHelper.TABLE_NOTE,
                allColumns, columnName + "='" + value + "'", null,
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    /**
     * Deletes all entries from table NOTE
     */
    public void clear() {
        for (Note n : getAllNotes()) {
            deleteNote(n);
        }
    }

    /**
     * Retrieve text text of card identified by uuid {@parm uuid}
     *
     * @param uuid entry identified by this uuid
     */
    public Note getNote(String uuid) {
        List<Note> notes = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM " + LymboSQLiteOpenHelper.TABLE_NOTE + "  WHERE TRIM(" + LymboSQLiteOpenHelper.COL_UUID + ") = '" + uuid.trim() + "'", null);
        //Cursor cursor = database.query(LymboSQLiteOpenHelper.TABLE_NOTE, allColumns, LymboSQLiteOpenHelper.COL_UUID, new String[]{uuid}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = cursorToNote(cursor);
            notes.add(note);
            cursor.moveToNext();
        }

        cursor.close();

        if (notes.size() > 0)
            return notes.get(0);
        else
            return null;
    }

    /**
     * Deletes an entry identified by {@parm note}
     *
     * @param note entry identified by this uuid will be deleted
     */
    public void deleteNote(Note note) {
        database.delete(LymboSQLiteOpenHelper.TABLE_NOTE, LymboSQLiteOpenHelper.COL_UUID
                + " = '" + note.getUuid() + "'", null);
    }

    /**
     * Updates the field NOTE of a note identified by {@param uuid} to {@param text}
     *
     * @param uuid entry identified by this uuid will be modified
     * @param text new text
     */
    public void updateNote(String uuid, String text) {
        String statement = "INSERT OR REPLACE INTO " + LymboSQLiteOpenHelper.TABLE_NOTE + " (" + LymboSQLiteOpenHelper.COL_UUID + ", " + LymboSQLiteOpenHelper.COL_TEXT + ") VALUES ('" + uuid + "', '" + text + "');";
        database.execSQL(statement);
    }

    /**
     * Retrieves a list of all entries of table NOTE
     *
     * @return list of LymboNote objects
     */
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        Cursor cursor = database.query(LymboSQLiteOpenHelper.TABLE_NOTE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = cursorToNote(cursor);
            notes.add(note);
            cursor.moveToNext();
        }

        cursor.close();

        return notes;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setUuid(cursor.getString(0));
        note.setText(cursor.getString(1));
        return note;
    }

    public void recreateOnSchemaChange() {
        try {
            database.query(LymboSQLiteOpenHelper.TABLE_NOTE,
                    allColumns, null, null, null, null, null);
        } catch (SQLException e) {
            dbLymboSQLiteOpenHelper.recreateTableNote(database);
        }
    }
}
