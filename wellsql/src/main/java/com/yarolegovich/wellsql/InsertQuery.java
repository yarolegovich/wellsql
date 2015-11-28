package com.yarolegovich.wellsql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.core.TableClass;
import com.yarolegovich.wellsql.mapper.InsertMapper;
import com.yarolegovich.wellsql.mapper.SQLiteMapper;

import java.util.List;

/**
 * Created by yarolegovich on 26.11.2015.
 */
@SuppressWarnings("unchecked")
public class InsertQuery<T extends Identifiable> {

    private TableClass mTable;
    private InsertMapper<T> mMapper;

    private SQLiteDatabase mDb;

    private List<T> mToInsert;
    private boolean mAsTransaction;

    InsertQuery(SQLiteDatabase db, List<T> data) {
        mToInsert = data;
        mDb = db;
        if (!data.isEmpty()) {
            T item = data.get(0);
            mTable = WellSql.tableFor(item.getClass());
            mMapper = (InsertMapper<T>) WellSql.mapperFor(item.getClass());
        }
    }

    public InsertQuery<T> withMapper(InsertMapper<T> mapper) {
        mMapper = mapper;
        return this;
    }

    public InsertQuery<T> asSingleTransaction(boolean enabled) {
        mAsTransaction = enabled;
        return this;
    }

    public void execute() {
        try {
            if (mToInsert.isEmpty()) {
                mAsTransaction = false;
                return;
            }
            if (mAsTransaction) {
                mDb.beginTransaction();
            }
            for (T item : mToInsert) {
                ContentValues cv = mMapper.toCv(item);
                //We do this not to violate UNIQUE constraint of @PrimaryKey, when using generated mapper
                if (cv.containsKey("_id")) {
                    cv.remove("_id");
                }
                int index = (int) mDb.insert(mTable.getTableName(), null, cv);
                item.setId(index);
            }
            if (mAsTransaction) {
                mDb.setTransactionSuccessful();
            }
        } finally {
            if (mAsTransaction) {
                mDb.endTransaction();
            }
            mDb.close();
        }
    }

}
