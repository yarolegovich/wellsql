package com.yarolegovich.wellsql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.yarolegovich.wellsql.core.Binder;
import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.core.TableClass;
import com.yarolegovich.wellsql.core.TableLookup;
import com.yarolegovich.wellsql.mapper.MapperAdapter;
import com.yarolegovich.wellsql.mapper.SQLiteMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yarolegovich on 26.11.2015.
 */
@SuppressWarnings("unchecked")
public abstract class DefaultWellConfig implements WellConfig,
        WellConfig.OnUpgradeListener, WellConfig.OnCreateListener, WellConfig.OnDowngradeListener {

    private Context mContext;
    private TableLookup mGeneratedLookup;
    private Map<Class<?>, SQLiteMapper<?>> mMappers;

    public DefaultWellConfig(Context context) {
        mContext = context.getApplicationContext();
        mMappers = new HashMap<>();
        try {
            Class<? extends TableLookup> clazz = (Class<? extends TableLookup>)
                    Class.forName(Binder.PACKAGE + "." + Binder.LOOKUP_CLASS);
            mGeneratedLookup = clazz.newInstance();
            for (Class<?> token : mGeneratedLookup.getMapperTokens()) {
                mMappers.put(token, new MapperAdapter<>(mGeneratedLookup.getMapper(token)));
            }
        } catch (ClassNotFoundException e) {
            throw new WellException(mContext.getString(R.string.classes_not_found));
        } catch (Exception e) {
            /* This can't be thrown, because Binder.LOOKUP_CLASS always will be instantiated successfully */
        }
        mMappers.putAll(registerMappers());
    }

    protected Map<Class<?>, SQLiteMapper<?>> registerMappers() {
        return Collections.emptyMap();
    }

    @Override
    public OnUpgradeListener getOnUpgradeListener() {
        return this;
    }

    @Override
    public OnCreateListener getOnCreateListener() {
        return this;
    }

    @Override
    public OnDowngradeListener getOnDowngradeListener() {
        return this;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public <T> SQLiteMapper<T> getMapper(Class<T> token) {
        return (SQLiteMapper<T>) mMappers.get(token);
    }

    @Override
    public TableClass getTable(Class<? extends Identifiable> token) {
        return mGeneratedLookup.getTable(token);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, WellTableManager helper, int oldVersion, int newVersion) {
        throw new SQLiteException(mContext.getString(R.string.downgrade, oldVersion, newVersion));
    }
}
