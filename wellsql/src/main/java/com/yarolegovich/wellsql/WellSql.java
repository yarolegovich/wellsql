package com.yarolegovich.wellsql;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.core.TableClass;
import com.yarolegovich.wellsql.mapper.SQLiteMapper;

import java.util.Collections;
import java.util.List;

/**
 * Created by yarolegovich on 19.11.2015.
 */
public class WellSql extends SQLiteOpenHelper {

    private static WellSql sInstance;

    static WellConfig mDbConfig;

    public static void init(WellConfig config) {
        mDbConfig = config;
        sInstance = new WellSql(config);
    }

    @SuppressWarnings("unchecked")
    public WellSql(WellConfig config) {
        super(config.getContext(), config.getDbName(), null, config.getDbVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        WellConfig.OnCreateListener l = mDbConfig.getOnCreateListener();
        if (l != null) {
            l.onCreate(db, new WellTableManager(db));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        WellConfig.OnUpgradeListener l = mDbConfig.getOnUpgradeListener();
        if (l != null) {
            l.onUpgrade(db, new WellTableManager(db), oldVersion, newVersion);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        WellConfig.OnDowngradeListener l = mDbConfig.getOnDowngradeListener();
        if (l != null) {
            l.onDowngrade(db, new WellTableManager(db), oldVersion, newVersion);
        }
    }

    public static <T extends Identifiable> SelectQuery<T> selectUnique(Class<T> token) {
        return select(token).uniqueOnly();
    }

    public static <T extends Identifiable> SelectQuery<T> select(Class<T> token) {
        return new SelectQuery<>(sInstance.getReadableDatabase(), token);
    }

    public static <T extends Identifiable> InsertQuery<T> insert(T item) {
        return insert(Collections.singletonList(item));
    }

    public static <T extends Identifiable> InsertQuery<T> insert(List<T> items) {
        return new InsertQuery<>(sInstance.getWritableDatabase(), items);
    }

    public static <T extends Identifiable> DeleteQuery<T> delete(Class<T> token) {
        return new DeleteQuery<>(sInstance.getWritableDatabase(), token);
    }

    public static <T extends Identifiable> UpdateQuery<T> update(Class<T> token) {
        return new UpdateQuery<>(sInstance.getWritableDatabase(), token);
    }

    public static <T extends Identifiable>ResetAutoincrementQuery autoincrementFor(Class<T> token) {
        return new ResetAutoincrementQuery(sInstance.getWritableDatabase(), tableFor(token).getTableName());
    }

    public static SQLiteDatabase giveMeReadableDb() {
        return sInstance.getReadableDatabase();
    }

    public static SQLiteDatabase giveMeWritableDb() {
        return sInstance.getWritableDatabase();
    }

    public static <T> SQLiteMapper<T> mapperFor(Class<T> token) {
        SQLiteMapper<T> mapper = mDbConfig.getMapper(token);
        if (mapper == null) {
            throw new RuntimeException(mDbConfig.getContext()
                    .getString(R.string.mapper_not_found, token.getSimpleName()));
        }
        return mapper;
    }

    static <T extends Identifiable> TableClass tableFor(Class<T> token) {
        TableClass tableClass = mDbConfig.getTable(token);
        if (tableClass == null) {
            throw new WellException(mDbConfig.getContext()
                    .getString(R.string.table_not_found, token.getSimpleName()));
        }
        return tableClass;
    }
}
