package de.interoberlin.lymbo.model.persistence.sqlite.stack;


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
public class TableStackDatasource {
    private SQLiteDatabase database;
    private LymboSQLiteOpenHelper dbLymboSQLiteOpenHelper;

    public static final String table = "stack";
    public static final ColumnHolder columnHolder = new ColumnHolder();
    public static final Column colUuid = new Column("uuid", Type.TEXT_PRIMARY_KEY);
    public static final Column colFile = new Column("file", Type.TEXT);
    public static final Column colPath = new Column("path", Type.TEXT);
    public static final Column colState = new Column("state", Type.INTEGER);
    public static final Column colFormat = new Column("format", Type.INTEGER);

    public static final int STATE_NORMAL = 0;
    public static final int STATE_STASHED = 3;

    public static final int FORMAT_LYMBO = 0;
    public static final int FORMAT_LYMBOX = 1;

    static {
        columnHolder.add(colUuid);
        columnHolder.add(colFile);
        columnHolder.add(colPath);
        columnHolder.add(colState);
        columnHolder.add(colFormat);
    }

    // --------------------
    // Constructors
    // --------------------

    public TableStackDatasource(Context context) {
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
        createStatement += colFile.getName() + " " + colFile.getType().getName() + ", ";
        createStatement += colPath.getName() + " " + colPath.getType().getName() + ", ";
        createStatement += colState.getName() + " " + colState.getType().getName() + ", ";
        createStatement += colFormat.getName() + " " + colFormat.getType().getName();

        createStatement += ");";

        return createStatement;
    }

    public static String getDropStatement() {
        return "drop table " + table + ";";
    }

    // --------------------
    // Methods - Common
    // --------------------

    private TableStackEntry cursorToStack(Cursor cursor) {
        TableStackEntry tableStackEntry = new TableStackEntry();
        tableStackEntry.setUuid(cursor.getString(0));
        tableStackEntry.setFile(cursor.getString(1));
        tableStackEntry.setPath(cursor.getString(2));
        tableStackEntry.setState(cursor.getInt(3));
        tableStackEntry.setFormat(cursor.getInt(4));
        return tableStackEntry;
    }

    public void recreate() {
        dbLymboSQLiteOpenHelper.recreateTableStack(database);
    }

    public void recreateOnSchemaChange() {
        try {
            database.query(table,
                    columnHolder.getColumnNames(), null, null, null, null, null);
        } catch (SQLException e) {
            dbLymboSQLiteOpenHelper.recreateTableStack(database);
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
    public List<TableStackEntry> getEntries() {
        List<TableStackEntry> entries = new ArrayList<>();

        if (database != null) {
            Cursor cursor = database.query(table,
                    columnHolder.getColumnNames(), null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                entries.add(cursorToStack(cursor));
                cursor.moveToNext();
            }

            cursor.close();
        }

        return entries;
    }

    public List<TableStackEntry> getEntries(Column column, String value) {
        List<TableStackEntry> entries = new ArrayList<>();

        Cursor cursor = database.query(table,
                columnHolder.getColumnNames(), column.getName() + "='" + value + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            entries.add(cursorToStack(cursor));
            cursor.moveToNext();
        }

        cursor.close();

        return entries;
    }

    public List<TableStackEntry> getEntries(Column column1, String value1, Column column2, int value2) {
        List<TableStackEntry> entries = new ArrayList<>();

        Cursor cursor = database.query(table,
                columnHolder.getColumnNames(), column1.getName() + "='" + value1 + "' and " + column2.getName() + "='" + value2 + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            entries.add(cursorToStack(cursor));
            cursor.moveToNext();
        }

        cursor.close();

        return entries;
    }

    public TableStackEntry getEntryByUuid(String uuid) {
        List<TableStackEntry> entries = getEntries(colUuid, uuid);
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
        return !getEntries(colUuid, uuid, colState, STATE_NORMAL).isEmpty();
    }

    /**
     * Determines whether the the of an entry with a given uuid is STASHED
     *
     * @param uuid uuid
     * @return
     */
    public boolean isStashed(String uuid) {
        return !getEntries(colUuid, uuid, colState, STATE_STASHED).isEmpty();
    }

    public boolean isCompressed(String uuid) {
        return !getEntries(colUuid, uuid, colFormat, FORMAT_LYMBOX).isEmpty();
    }

    // --------------------
    // Methods - Delete
    // --------------------

    /**
     * Deletes an entry identified by {@parm uuid}
     *
     * @param uuid uuid of the card to be deleted
     */
    public void deleteStackEntry(String uuid) {
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
    public void updateStackStateNormal(String uuid) {
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
     * Updates the field state of a card identified by {@param uuid} to STASHED
     *
     * @param uuid state of entry identified by this uuid will be set to STASHED
     */
    public void updateStackStateStashed(String uuid) {
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
     * Updates the field path of a stack identified by {@param uuid} to {@param path}
     *
     * @param uuid   path of entry identified by this uuid will be set
     * @param file   file to be set
     * @param path   path to be set
     * @param format format to be set
     */
    public void updateStackLocation(String uuid, String file, String path, int format) {
        if (containsUuid(uuid)) {
            ContentValues values = new ContentValues();
            values.put(colFile.getName(), file);
            values.put(colPath.getName(), path);
            values.put(colState.getName(), format);
            database.update(table, values, colUuid.getName() + "='" + uuid + "'", null);
        } else {
            ContentValues values = new ContentValues();
            values.put(colUuid.getName(), uuid);
            values.put(colFile.getName(), file);
            values.put(colPath.getName(), path);
            values.put(colState.getName(), 0);
            values.put(colFormat.getName(), format);
            database.insert(table, null, values);
        }
    }

    // --------------------
    // Methods - Debug
    // --------------------

    public void printTable() {
        for (TableStackEntry entry : getEntries()) {
            if (entry != null) {
                System.out.println("Entry");
                System.out.println(".. " + colUuid.getName() + " \t" + entry.getUuid());
                System.out.println(".. " + colState.getName() + " \t" + entry.getState());
                System.out.println(".. " + colFile.getName() + " \t" + entry.getFile());
                System.out.println(".. " + colPath.getName() + " \t" + entry.getPath());
                System.out.println(".. " + colFormat.getName() + " \t" + entry.getFormat());

            }
        }
    }
}
