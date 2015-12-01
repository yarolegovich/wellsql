package com.yarolegovich.wellsql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.core.TableClass;
import com.yarolegovich.wellsql.mapper.SQLiteMapper;
import com.yarolegovich.wellsql.mapper.SelectMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by yarolegovich on 19.11.2015.
 */
@SuppressWarnings("unchecked")
public class SelectQuery<T extends Identifiable> implements ConditionClauseConsumer {

    private static Executor sExecutor = Executors.newSingleThreadExecutor();

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private Class<T> mModel;

    private SQLiteDatabase mDb;

    private String[] mProjection;
    private String mGroupBy;
    private String mHaving;
    private String mSortOrder;
    private String mLimit;

    private String mSelection;
    private String[] mSelectionArgs;

    private SQLiteQueryBuilder mSQLiteQueryBuilder = new SQLiteQueryBuilder();

    SelectQuery(SQLiteDatabase db, Class<T> tableClass) {
        TableClass table = WellSql.tableFor(tableClass);

        mDb = db;
        mSQLiteQueryBuilder.setTables(table.getTableName());
        mModel = (Class<T>) table.getModelClass();
    }

    public SelectQuery<T> columns(String ...columns) {
        if (columns.length != 0) {
            mProjection = columns;
        }
        return this;
    }

    public SelectQuery<T> groupBy(String ...columns) {
        if (columns.length != 0) {
            mGroupBy = TextUtils.join(",", columns);
        }
        return this;
    }

    public SelectQuery<T> whereRaw(String where, Object[] args) {
        whereRaw(where, Arrays.asList(args));
        return this;
    }

    public SelectQuery<T> whereRaw(String where, List<?> args) {
        mSelection = where;
        mSelectionArgs = new String[args.size()];
        for (int i = 0; i < args.size(); i++) {
            mSelectionArgs[i] = String.valueOf(args.get(i));
        }
        return this;
    }

    public SelectQuery<T> orderBy(@Order int order, String ...columns) {
        mSortOrder = TextUtils.join(", ", columns).concat(order >= 0 ? " ASC" : " DESC");
        return this;
    }

    public SelectQuery<T> uniqueOnly() {
        mSQLiteQueryBuilder.setDistinct(true);
        return this;
    }

    public ConditionClauseBuilder<SelectQuery<T>> where() {
        return new ConditionClauseBuilder<>(this);
    }

    public SelectQuery<T> havingRaw(String having) {
        mHaving = having;
        return this;
    }

    public SelectQuery<T> limit(int num) {
        mLimit = String.valueOf(num);
        return this;
    }

    public SelectQuery<T> limit(int num, int offset) {
        mLimit = num + ", " + offset;
        return this;
    }

    public Map<String, Object> getAsMap() {
        Cursor cursor = execute();
        try {
            Map<String, Object> result = new HashMap<>();
            Bundle bundle = cursor.getExtras();
            for (String column : bundle.keySet()) {
                result.put(column, bundle.get(column));
            }
            return result;
        } finally {
            cursor.close();
            mDb.close();
        }
    }

    public void getAsMapAsync(final Callback<Map<String, Object>> callback) {
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMainHandler.post(new DeliveryMan<>(getAsMap(), callback));
            }
        });
    }

    public List<T> getAsModel() {
        return getAsModel(WellSql.mapperFor(mModel));
    }

    public List<T> getAsModel(SelectMapper<T> mapper) {
        Cursor cursor = execute();
        try {
            List<T> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                result.add(mapper.convert(cursor));
            }
            return result;
        } finally {
            cursor.close();
            mDb.close();
        }
    }

    public WellCursor<T> getAsCursor() {
        return getAsCursor(WellSql.mapperFor(mModel));
    }

    public WellCursor<T> getAsCursor(SelectMapper<T> mapper) {
        return new WellCursor<>(mDb, mapper, execute());
    }

    public void getAsModelAsync(final Callback<List<T>> callback) {
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMainHandler.post(new DeliveryMan<>(getAsModel(), callback));
            }
        });
    }

    public void getAsModelAsync(final SelectMapper<T> mapper, final Callback<List<T>> callback) {
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMainHandler.post(new DeliveryMan<>(getAsModel(mapper), callback));
            }
        });
    }

    private Cursor execute() {
        return mSQLiteQueryBuilder.query(mDb,
                mProjection,
                mSelection,
                mSelectionArgs,
                mGroupBy, mHaving,
                mSortOrder,
                mLimit);
    }

    @Override
    public void acceptClause(String where) {
        mSelection = where;
    }

    @Override
    public void acceptArgs(String[] whereArgs) {
        mSelectionArgs = whereArgs;
    }

    public interface Callback<T> {
        void onDataReady(T data);
    }

    private static class DeliveryMan<T> implements Runnable {

        private T mData;
        private Callback<T> mRecipient;

        public DeliveryMan(T data, Callback<T> recipient) {
            mData = data;
            mRecipient = recipient;
        }

        @Override
        public void run() {
            mRecipient.onDataReady(mData);
        }
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ORDER_ASCENDING, ORDER_DESCENDING})
    public @interface Order {}
    public static final int ORDER_ASCENDING = 1;
    public static final int ORDER_DESCENDING = -1;

}
