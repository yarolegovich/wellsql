package com.yarolegovich.wellsql;

import android.database.sqlite.SQLiteDatabase;

import com.yarolegovich.wellsql.core.Identifiable;

import java.util.List;

/**
 * Created by yarolegovich on 26.11.2015.
 */
public class DeleteQuery<T extends Identifiable> implements ConditionClauseConsumer{

    private final String WHERE_ID = "_id = ?";

    private String mTableName;
    private String mSelection;
    private String[] mArgs;

    private SQLiteDatabase mDb;

    DeleteQuery(SQLiteDatabase db, Class<T> token) {
        mDb = db;
        mTableName = WellSql.tableFor(token).getTableName();
    }

    public ConditionClauseBuilder<DeleteQuery<T>> where() {
        return new ConditionClauseBuilder<>(this);
    }

    public int whereId(List<T> items) {
        int rowsAffected = 0;
        String[] arg = new String[1];
        for (T item : items) {
            arg[0] = String.valueOf(item.getId());
            rowsAffected += mDb.delete(mTableName, WHERE_ID, arg);
        }
        return rowsAffected;
    }

    public int whereId(int id) {
        return mDb.delete(mTableName, WHERE_ID, new String[]{String.valueOf(id)});
    }

    public int whereId(T item) {
        return mDb.delete(mTableName, WHERE_ID, new String[] { String.valueOf(item.getId()) });
    }

    public int execute() {
        return mDb.delete(mTableName, mSelection, mArgs);
    }

    @Override
    public void acceptClause(String where) {
        mSelection = where;
    }

    @Override
    public void acceptArgs(String[] whereArgs) {
        mArgs = whereArgs;
    }
}
