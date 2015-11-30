package com.yarolegovich.wellsample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wellsql.generated.GeneratedLookup;
import com.yarolegovich.wellsql.DefaultWellConfig;
import com.yarolegovich.wellsql.WellSql;
import com.yarolegovich.wellsql.WellTableManager;
import com.yarolegovich.wellsql.mapper.SQLiteMapper;

import java.util.Map;

/**
 * Created by yarolegovich on 26.11.2015.
 */
public class WellConfig extends DefaultWellConfig {

    public WellConfig(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db, WellTableManager helper) {
        helper.createTable(SuperHero.class);
        helper.createTable(Villain.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, WellTableManager helper, int newVersion, int oldVersion) {
        helper.dropTable(SuperHero.class);
        helper.dropTable(Villain.class);
        onCreate(db, helper);
    }

    /*
     * Here you can map classes to custom mappers. If you register here a mapper for class that already
     * has generated mapper - WellSql will use your mapper, but not generated.
     * No need to call super.registerMappers()
     */
    @Override
    protected Map<Class<?>, SQLiteMapper<?>> registerMappers() {
        return super.registerMappers();
    }

    @Override
    public int getDbVersion() {
        return 3;
    }

    @Override
    public String getDbName() {
        return "my_db";
    }
}
