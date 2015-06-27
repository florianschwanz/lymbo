package de.interoberlin.lymbo.model.persistence.sqlite.cards;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import de.interoberlin.lymbo.model.persistence.sqlite.LymboSQLiteOpenHelper;

/**
 * Data source for SQLite database table CARDSTATE
 */
public class CardStateDatasource {
    private SQLiteDatabase database;
    private LymboSQLiteOpenHelper dbLymboSQLiteOpenHelper;
    private String[] allColumns = {LymboSQLiteOpenHelper.COL_UUID, LymboSQLiteOpenHelper.COL_STASHED};

    // --------------------
    // Constructors
    // --------------------

    public CardStateDatasource(Context context) {
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
        Cursor cursor = database.query(LymboSQLiteOpenHelper.TABLE_CARDSTATE,
                allColumns, columnName + "='" + value + "'", null,
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    /**
     * Deletes all entries from table CARDSTATE
     */
    public void clear() {
        for (CardState cs : getAllCardStates()) {
            deleteCardState(cs);
        }
    }

    /**
     * Deletes an entry identified by {@parm cardState}
     *
     * @param cardState entry identified by this cardState will be deleted
     */
    public void deleteCardState(CardState cardState) {
        String uuid = cardState.getUuid();
        database.delete(LymboSQLiteOpenHelper.TABLE_CARDSTATE, LymboSQLiteOpenHelper.COL_UUID
                + " = '" + uuid + "'", null);
    }

    /**
     * Updates the field STASHED of a card identified by {@param uuid} to {@param stashed}
     *
     * @param uuid    entry identified by this uuid will be modified
     * @param stashed whether or nor the entry shall be marked as stashed
     */
    public void updateCardState(String uuid, boolean stashed) {
        String statement = "INSERT OR REPLACE INTO " + LymboSQLiteOpenHelper.TABLE_CARDSTATE + " (" + LymboSQLiteOpenHelper.COL_UUID + ", " + LymboSQLiteOpenHelper.COL_STASHED + ") VALUES ('" + uuid + "', '" + (stashed ? 1 : 0) + "');";
        database.execSQL(statement);
    }

    /**
     * Retrieves a list of all entries of table CARDSTATE
     *
     * @return list of CardState objects
     */
    public List<CardState> getAllCardStates() {
        List<CardState> cardStates = new ArrayList<>();

        Cursor cursor = database.query(LymboSQLiteOpenHelper.TABLE_CARDSTATE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CardState cardState = cursorToLocation(cursor);
            cardStates.add(cardState);
            cursor.moveToNext();
        }

        cursor.close();

        return cardStates;
    }

    public boolean containsUuid(String uuid) {
        for (CardState cs : getAllCardStates()) {
            if (cs.getUuid().equals(uuid)) {
                return true;
            }
        }

        return false;
    }

    public boolean getStashed(String uuid) {
        for (CardState cs : getAllCardStates()) {
            if (cs.getUuid().equals(uuid)) {
                return cs.getStashed() == 0 ? false : true;
            }
        }

        return false;
    }

    private CardState cursorToLocation(Cursor cursor) {
        CardState cardState = new CardState();
        cardState.setUuid(cursor.getString(0));
        cardState.setStashed(cursor.getInt(1));
        return cardState;
    }

    public void recreateOnSchemaChange() {
        try {
            database.query(LymboSQLiteOpenHelper.TABLE_CARDSTATE,
                    allColumns, null, null, null, null, null);
        } catch (SQLException e) {
            dbLymboSQLiteOpenHelper.recreateTableLocation(database);
        }
    }
}
