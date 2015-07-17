package de.interoberlin.lymbo.model.persistence.sqlite.cards;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.persistence.sqlite.Column;
import de.interoberlin.lymbo.model.persistence.sqlite.ColumnHolder;
import de.interoberlin.lymbo.model.persistence.sqlite.LymboSQLiteOpenHelper;
import de.interoberlin.lymbo.model.persistence.sqlite.Type;

/**
 * Data source for SQLite database table
 */
public class TableCardDatasource {
    private SQLiteDatabase database;
    private LymboSQLiteOpenHelper dbLymboSQLiteOpenHelper;

    public static final String table = "card";
    public static final ColumnHolder columnHolder = new ColumnHolder();
    public static final Column colUuid = new Column("uuid", Type.TEXT_PRIMARY_KEY);
    public static final Column colNote = new Column("note", Type.TEXT);
    public static final Column colState = new Column("state", Type.INTEGER);
    public static final Column colFavorite = new Column("favorite", Type.INTEGER);

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DISMISSED = 2;
    public static final int STATE_STASHED = 3;

    static {
        columnHolder.add(colUuid);
        columnHolder.add(colNote);
        columnHolder.add(colState);
        columnHolder.add(colFavorite);
    }

    // --------------------
    // Constructors
    // --------------------

    public TableCardDatasource(Context context) {
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

    public static String getCreateStatement() {
        String createStatement = "create table if not exists " + table + "(";

        createStatement += colUuid.getName() + " " + colUuid.getType().getName() + ", ";
        createStatement += colNote.getName() + " " + colNote.getType().getName() + ", ";
        createStatement += colState.getName() + " " + colState.getType().getName() + ", ";
        createStatement += colFavorite.getName() + " " + colFavorite.getType().getName();

        createStatement += ");";

        return createStatement;
    }

    public static String getDropStatement() {
        return "drop table " + table + ";";
    }

    // --------------------
    // Methods - Common
    // --------------------

    /**
     * Retrieves a list of all entries of table
     *
     * @return list of objects
     */
    private List<TableCardEntry> getAllCards() {
        List<TableCardEntry> tableCardEntries = new ArrayList<>();

        Cursor cursor = database.query(table,
                columnHolder.getColumnNames(), null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TableCardEntry tableCardEntry = cursorToCard(cursor);
            tableCardEntries.add(tableCardEntry);
            cursor.moveToNext();
        }

        cursor.close();

        return tableCardEntries;
    }

    private TableCardEntry cursorToCard(Cursor cursor) {
        TableCardEntry tableCardEntry = new TableCardEntry();
        tableCardEntry.setUuid(cursor.getString(0));
        tableCardEntry.setNote(cursor.getString(1));
        tableCardEntry.setState(cursor.getInt(2));
        tableCardEntry.setFavorite(cursor.getInt(3) == 1);
        return tableCardEntry;
    }

    public void recreateOnSchemaChange() {
        try {
            database.query(table,
                    columnHolder.getColumnNames(), null, null, null, null, null);
        } catch (SQLException e) {
            dbLymboSQLiteOpenHelper.recreateTableCard(database);
        }
    }

    // --------------------
    // Methods - Read
    // --------------------

    /**
     * Retrieves a list of all entries of table
     *
     * @return list of entries
     */
    public List<TableCardEntry> getEntries() {
        List<TableCardEntry> entries = new ArrayList<>();

        Cursor cursor = database.query(table,
                columnHolder.getColumnNames(), null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            entries.add(cursorToCard(cursor));
            cursor.moveToNext();
        }

        cursor.close();

        return entries;
    }

    public List<TableCardEntry> getEntries(Column column, String value) {
        List<TableCardEntry> entries = new ArrayList<>();

        Cursor cursor = database.query(table,
                columnHolder.getColumnNames(), column.getName() + "='" + value + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            entries.add(cursorToCard(cursor));
            cursor.moveToNext();
        }

        cursor.close();

        return entries;
    }

    public List<TableCardEntry> getEntries(Column column1, String value1, Column column2, int value2) {
        List<TableCardEntry> entries = new ArrayList<>();

        Cursor cursor = database.query(table,
                columnHolder.getColumnNames(), column1.getName() + "='" + value1 + "' and " + column2.getName() + "='" + value2 + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            entries.add(cursorToCard(cursor));
            cursor.moveToNext();
        }

        cursor.close();

        return entries;
    }

    public TableCardEntry getEntryByUuid(String uuid) {
        List<TableCardEntry> entries = getEntries(colUuid, uuid);
        if (!entries.isEmpty()) {
            return entries.get(0);
        } else {
            return null;
        }
    }

    /**
     * Determines whether an entry exists whose value of column {@param columnName} is {@param columnValue}
     *
     * @param columnName column name
     * @param value      value
     * @return
     */
    public boolean contains(String columnName, String value) {
        Cursor cursor = database.query(table,
                columnHolder.getColumnNames(), columnName + "='" + value + "'", null,
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    /**
     * Determines whether an entry with a given uuid exists
     *
     * @param uuid uuid to test existence
     * @return
     */
    public boolean containsUuid(String uuid) {
        return contains(colUuid.getName(), uuid);
    }

    /**
     * Determines whether the state of an entry with a given uuid is NORMAL
     *
     * @param uuid uuid
     * @return
     */
    public boolean isNormal(String uuid) {
        for (TableCardEntry entry : getAllCards()) {
            if (entry.getUuid().equals(uuid)) {
                return entry.getState() == STATE_NORMAL;
            }
        }

        return false;
    }

    /**
     * Determines whether the state of an entry with a given uuid is DISMISSED
     *
     * @param uuid uuid
     * @return
     */
    public boolean isDismissed(String uuid) {
        for (TableCardEntry entry : getAllCards()) {
            if (entry.getUuid().equals(uuid)) {
                return entry.getState() == STATE_DISMISSED;
            }
        }

        return false;
    }

    /**
     * Determines whether the state of an entry with a given uuid is STASHED
     *
     * @param uuid uuid
     * @return
     */
    public boolean isStashed(String uuid) {
        return !getEntries(colUuid, uuid, colState, STATE_STASHED).isEmpty();
    }

    /**
     * Determines whether an entry is marked as favorite
     *
     * @param uuid uuid
     * @return
     */
    public boolean isFavorite(String uuid) {
        for (TableCardEntry entry : getAllCards()) {
            if (entry.getUuid().equals(uuid)) {
                return entry.isFavorite();
            }
        }

        return false;
    }

    // --------------------
    // Methods - Delete
    // --------------------

    /**
     * Deletes an entry identified by {@parm uuid}
     *
     * @param uuid uuid of the card to be deleted
     */
    public void deleteCardEntry(String uuid) {
        database.delete(table, colUuid.getName() + " = '" + uuid + "'", null);
    }

    // --------------------
    // Methods - Update
    // --------------------

    /**
     * Updates the field state of a card identified by {@param uuid} to NORMAL
     *
     * @param uuid state of entry identified by this uuid will be set to NORMAL
     */
    public void updateCardStateNormal(String uuid) {
        if (containsUuid(uuid)) {
            ContentValues values = new ContentValues();
            values.put(colState.getName(), STATE_NORMAL);
            database.update(table, values, colUuid.getName() + "='" + uuid + "'", null);
        } else {
            ContentValues values = new ContentValues();
            values.put(colUuid.getName(), uuid);
            values.put(colState.getName(), STATE_NORMAL);
            database.insert(table, null, values);
        }
    }

    /**
     * Updates the field state of a card identified by {@param uuid} to DISMISSED
     *
     * @param uuid state of entry identified by this uuid will be set to DISMISSED
     */
    public void updateCardStateDismissed(String uuid) {
        if (containsUuid(uuid)) {
            ContentValues values = new ContentValues();
            values.put(colState.getName(), STATE_DISMISSED);
            database.update(table, values, colUuid.getName() + "='" + uuid + "'", null);
        } else {
            ContentValues values = new ContentValues();
            values.put(colUuid.getName(), uuid);
            values.put(colState.getName(), STATE_DISMISSED);
            database.insert(table, null, values);
        }
    }

    /**
     * Updates the field state of a card identified by {@param uuid} to STASHED
     *
     * @param uuid state of entry identified by this uuid will be set to STASHED
     */
    public void updateCardStateStashed(String uuid) {
        if (containsUuid(uuid)) {
            ContentValues values = new ContentValues();
            values.put(colState.getName(), STATE_STASHED);
            database.update(table, values, colUuid.getName() + "='" + uuid + "'", null);
        } else {
            ContentValues values = new ContentValues();
            values.put(colUuid.getName(), uuid);
            values.put(colState.getName(), STATE_STASHED);
            database.insert(table, null, values);
        }
    }

    /**
     * Updates the field note of a card identified by {@param uuid} to {@param note}
     *
     * @param uuid note of entry identified by this uuid will be set
     * @param note note to be set
     */
    public void updateCardNote(String uuid, String note) {
        if (containsUuid(uuid)) {
            ContentValues values = new ContentValues();
            values.put(colNote.getName(), note);
            database.update(table, values, colUuid.getName() + "='" + uuid + "'", null);
        } else {
            ContentValues values = new ContentValues();
            values.put(colUuid.getName(), uuid);
            values.put(colNote.getName(), note);
            values.put(colState.getName(), 0);
            database.insert(table, null, values);
        }
    }

    /**
     * Updates the field favorite of a card identified by {@param uuid}
     *
     * @param uuid favorite of entry identified by this uuid will be set
     */
    public void updateCardFavorite(String uuid, boolean favorite) {
        if (containsUuid(uuid)) {
            ContentValues values = new ContentValues();
            values.put(colFavorite.getName(), favorite ? 1 : 0);
            database.update(table, values, colUuid.getName() + "='" + uuid + "'", null);
        } else {
            ContentValues values = new ContentValues();
            values.put(colUuid.getName(), uuid);
            values.put(colFavorite.getName(), favorite ? 1 : 0);
            database.insert(table, null, values);
        }
    }

    // --------------------
    // Methods - Debug
    // --------------------

    public void printTable() {
        for (TableCardEntry entry : getEntries()) {
            if (entry != null) {
                System.out.println(entry.getUuid() + "\t" + entry.getState() + "\t" + entry.getNote() + "\t" + String.valueOf(entry.isFavorite()));
            }
        }
    }
}
