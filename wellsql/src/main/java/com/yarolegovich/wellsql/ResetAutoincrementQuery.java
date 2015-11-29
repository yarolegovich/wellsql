package com.yarolegovich.wellsql;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by yarolegovich on 29.11.2015.
 */
public class ResetAutoincrementQuery {

    private final String TABLE_NAME = "sqlite_sequence";
    private final String SELECTION = "name = ?";

    private SQLiteDatabase mDb;
    private String mName;

    ResetAutoincrementQuery(SQLiteDatabase db, String tableName) {
        mDb = db;
        mName = tableName;
    }

    public void reset() {
        try {
            mDb.delete(TABLE_NAME, SELECTION, new String[] {mName});
        } finally {
            mDb.close();
        }
    }
}
