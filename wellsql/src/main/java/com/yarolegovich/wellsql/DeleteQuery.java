package com.yarolegovich.wellsql;

import android.database.sqlite.SQLiteDatabase;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.core.TableClass;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by yarolegovich on 26.11.2015.
 */
public class DeleteQuery<T extends Identifiable> implements ConditionClauseConsumer{

    private final String WHERE_ID = "id = ?";

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

    public void whereId(List<T> items) {
        try {
            String[] arg = new String[1];
            for (T item : items) {
                arg[0] = String.valueOf(item.getId());
                mDb.delete(mTableName, WHERE_ID, arg);
            }
        } finally {
            mDb.close();
        }
    }

    public void whereId(T item) {
        try {
            mDb.delete(mTableName, WHERE_ID, new String[] { String.valueOf(item.getId()) });
        } finally {
            mDb.close();
        }
    }

    public void execute() {
        try {
            mDb.delete(mTableName, mSelection, mArgs);
        } finally {
            mDb.close();
        }
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
