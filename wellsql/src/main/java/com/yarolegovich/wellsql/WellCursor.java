package com.yarolegovich.wellsql;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.yarolegovich.wellsql.mapper.SelectMapper;

/**
 * Created by yarolegovich on 01.12.2015.
 */
public class WellCursor<T> extends CursorWrapper {

    private SQLiteDatabase mDb;
    private SelectMapper<T> mMapper;

    WellCursor(SQLiteDatabase db, SelectMapper<T> mapper, Cursor cursor) {
        super(cursor);

        mDb = db;
        mMapper = mapper;
    }

    @Override
    public void close() {
        super.close();
    }

    @Nullable
    public T next() {
        Cursor cursor = getWrappedCursor();
        return cursor.moveToNext() ? mMapper.convert(cursor) : null;
    }
}
