package de.interoberlin.lymbo.model.persistence.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.interoberlin.lymbo.model.persistence.sqlite.cards.TableCardDatasource;
import de.interoberlin.lymbo.model.persistence.sqlite.stack.TableStackDatasource;

public class LymboSQLiteOpenHelper extends SQLiteOpenHelper {
    private static Context context;

    // Database
    private static final String DATABASE_NAME = "lymbo";
    private static final int DATABASE_VERSION = 3;

    public LymboSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
        db.execSQL(TableCardDatasource.getCreateStatement());
        db.execSQL(TableStackDatasource.getCreateStatement());
    }

    public void recreateTableCard(SQLiteDatabase db) {
        db.execSQL(TableCardDatasource.getCreateStatement());
        db.execSQL(TableCardDatasource.getDropStatement());
    }

    public void recreateTableStack(SQLiteDatabase db) {
        db.execSQL(TableStackDatasource.getCreateStatement());
        db.execSQL(TableStackDatasource.getDropStatement());
    }
}
