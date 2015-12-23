package de.interoberlin.lymbo.model.persistence.sqlite.settings;


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
import de.interoberlin.mate.lib.model.Log;

/**
 * Data source for SQLite database table
 */
public class TableSettingsDatasource {
    public static final String TAG = TableSettingsDatasource.class.toString();

    private SQLiteDatabase database;
    private LymboSQLiteOpenHelper dbLymboSQLiteOpenHelper;

    public static final String table = "settings";
    public static final ColumnHolder columnHolder = new ColumnHolder();
    public static final Column colKey = new Column("key", Type.TEXT_PRIMARY_KEY);
    public static final Column colValue = new Column("value", Type.TEXT);

    static {
        columnHolder.add(colKey);
        columnHolder.add(colValue);
    }

    // --------------------
    // Constructors
    // --------------------

    public TableSettingsDatasource(Context context) {
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

        createStatement += colKey.getName() + " " + colKey.getType().getName() + ", ";
        createStatement += colValue.getName() + " " + colValue.getType().getName();

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
    private List<TableSettingsEntry> getAllSettings() {
        List<TableSettingsEntry> tableSettingsEntries = new ArrayList<>();

        Cursor cursor = database.query(table,
                columnHolder.getColumnNames(), null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TableSettingsEntry tableSettingsEntry = cursorToSetting(cursor);
            tableSettingsEntries.add(tableSettingsEntry);
            cursor.moveToNext();
        }

        cursor.close();

        return tableSettingsEntries;
    }

    private TableSettingsEntry cursorToSetting(Cursor cursor) {
        TableSettingsEntry tableSettingsEntry = new TableSettingsEntry();
        tableSettingsEntry.setKey(cursor.getString(0));
        tableSettingsEntry.setValue(cursor.getString(1));
        return tableSettingsEntry;
    }

    public void recreateOnSchemaChange() {
        try {
            database.query(table,
                    columnHolder.getColumnNames(), null, null, null, null, null);
        } catch (SQLException e) {
            dbLymboSQLiteOpenHelper.recreateTableSettings(database);
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
    public List<TableSettingsEntry> getEntries() {
        List<TableSettingsEntry> entries = new ArrayList<>();

        Cursor cursor = database.query(table,
                columnHolder.getColumnNames(), null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            entries.add(cursorToSetting(cursor));
            cursor.moveToNext();
        }

        cursor.close();

        return entries;
    }

    public List<TableSettingsEntry> getEntries(Column column, String value) {
        List<TableSettingsEntry> entries = new ArrayList<>();

        if (database != null) {
            Cursor cursor = database.query(table,
                    columnHolder.getColumnNames(), column.getName() + "='" + value + "'", null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                entries.add(cursorToSetting(cursor));
                cursor.moveToNext();
            }

            cursor.close();
        }

        return entries;
    }

    public TableSettingsEntry getEntryByKey(String key) {
        List<TableSettingsEntry> entries = getEntries(colKey, key);
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
     * @param key key to test existence
     * @return
     */
    public boolean containsKey(String key) {
        return contains(colKey.getName(), key);
    }

    // --------------------
    // Methods - Delete
    // --------------------

    /**
     * Deletes an entry identified by key
     *
     * @param key key of the setting to be deleted
     */
    public void deleteSettingEntry(String key) {
        database.delete(table, colKey.getName() + " = '" + key + "'", null);
    }

    // --------------------
    // Methods - Update
    // --------------------

    /**
     * Updates the value field of a setting identified by {@param key}
     *
     * @param key key of the setting entry
     */
    public void updateSetting(String key, String value) {
        if (containsKey(key)) {
            ContentValues values = new ContentValues();
            values.put(colValue.getName(), value);
            database.update(table, values, colKey.getName() + "='" + key + "'", null);
        } else {
            ContentValues values = new ContentValues();
            values.put(colKey.getName(), key);
            values.put(colValue.getName(), value);
            database.insert(table, null, values);
        }
    }


    // --------------------
    // Methods - Debug
    // --------------------

    public void printTable() {
        for (TableSettingsEntry entry : getEntries()) {
            if (entry != null) {
                Log.d(TAG, "Entry");
                Log.d(TAG, ".." + colKey.getName() + "\t" + entry.getKey());
                Log.d(TAG, ".." + colValue.getName() + "\t" + entry.getValue());
            }
        }
    }
}
