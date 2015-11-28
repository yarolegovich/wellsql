package com.yarolegovich.wellsql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.core.TableClass;
import com.yarolegovich.wellsql.mapper.SQLiteMapper;

/**
 * Created by yarolegovich on 25.11.2015.
 */
public interface WellConfig {

    int getDbVersion();
    String getDbName();

    OnUpgradeListener getOnUpgradeListener();
    OnCreateListener getOnCreateListener();
    OnDowngradeListener getOnDowngradeListener();
    Context getContext();

    <T>SQLiteMapper<T> getMapper(Class<T> token);
    TableClass getTable(Class<? extends Identifiable> token);

    interface OnCreateListener {
        void onCreate(SQLiteDatabase db, WellTableManager helper);
    }

    interface OnUpgradeListener {
        void onUpgrade(SQLiteDatabase db, WellTableManager helper, int oldVersion, int newVersion);
    }

    interface OnDowngradeListener {
        void onDowngrade(SQLiteDatabase db, WellTableManager helper, int oldVersion, int newVersion);
    }
}
