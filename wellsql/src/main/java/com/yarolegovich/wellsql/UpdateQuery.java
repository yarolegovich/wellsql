package com.yarolegovich.wellsql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.mapper.InsertMapper;
import com.yarolegovich.wellsql.mapper.SQLiteMapper;

import java.util.Collections;
import java.util.List;

/**
 * Created by yarolegovich on 26.11.2015.
 */
@SuppressWarnings("unchecked")
public class UpdateQuery<T extends Identifiable> implements ConditionClauseConsumer {

    private final String WHERE_ID = "_id = ?";

    private SQLiteDatabase mDb;

    private ContentValues mContentValues;

    private String mTableName;
    private String mSelection;
    private String[] mSelectionArgs;

    private SQLiteMapper<T> mMapper;

    UpdateQuery(SQLiteDatabase db, Class<T> token) {
        mDb = db;
        mTableName = WellSql.tableFor(token).getTableName();
        mMapper = WellSql.mapperFor(token);
    }

    public ConditionClauseBuilder<UpdateQuery<T>> where() {
        return new ConditionClauseBuilder(this);
    }

    public UpdateQuery<T> whereId(int id) {
        mSelection = WHERE_ID;
        mSelectionArgs = new String[]{String.valueOf(id)};
        return this;
    }

    public int replaceWhereId(T item) {
        return replaceWhereId(Collections.singletonList(item));
    }

    public int replaceWhereId(List<T> items) {
        mSelection = WHERE_ID;
        int rowsAffected = 0;
        String[] args = new String[1];
        for (T item : items) {
            args[0] = String.valueOf(item.getId());
            ContentValues cv = mMapper.toCv(item);
            rowsAffected += mDb.update(mTableName, cv, mSelection, args);
        }
        return rowsAffected;
    }

    public UpdateQuery<T> put(T item) {
        mContentValues = mMapper.toCv(item);
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

    public int execute() {
        return mDb.update(mTableName, mContentValues, mSelection, mSelectionArgs);
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
