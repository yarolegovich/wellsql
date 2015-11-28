package com.yarolegovich.wellsql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.mapper.InsertMapper;
import com.yarolegovich.wellsql.mapper.SQLiteMapper;

/**
 * Created by yarolegovich on 26.11.2015.
 */
@SuppressWarnings("unchecked")
public class UpdateQuery<T extends Identifiable> implements ConditionClauseConsumer {

    private SQLiteDatabase mDb;

    private ContentValues mContentValues;

    private String mTableName;
    private String mSelection;
    private String[] mSelectionArgs;

    UpdateQuery(SQLiteDatabase db, Class<T> token) {
        mDb = db;
        mTableName = WellSql.tableFor(token).getTableName();
    }

    public ConditionClauseBuilder<UpdateQuery<T>> where() {
        return new ConditionClauseBuilder(this);
    }

    public UpdateQuery<T> whereId(int id) {
        mSelection = "_id = ?";
        mSelectionArgs = new String[]{String.valueOf(id)};
        return this;
    }

    public UpdateQuery<T> put(T item) {
        SQLiteMapper<T> mapper = (SQLiteMapper<T>) WellSql.mapperFor(item.getClass());
        mContentValues = mapper.toCv(item);
        return this;
    }

    public UpdateQuery<T> put(T item, InsertMapper<T> mapper) {
        mContentValues = mapper.toCv(item);
        return this;
    }

    public <U> UpdateQuery<T> put(U item, InsertMapper<U> mapper) {
        mContentValues = mapper.toCv(item);
        return this;
    }

    public void execute() {
        try {
            mDb.update(mTableName, mContentValues, mSelection, mSelectionArgs);
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
        mSelectionArgs = whereArgs;
    }
}
