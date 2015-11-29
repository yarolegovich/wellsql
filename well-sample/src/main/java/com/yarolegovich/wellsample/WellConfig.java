package com.yarolegovich.wellsample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wellsql.generated.GeneratedLookup;
import com.yarolegovich.wellsql.DefaultWellConfig;
import com.yarolegovich.wellsql.WellSql;
import com.yarolegovich.wellsql.WellTableManager;

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
        onCreate(db, helper);
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
