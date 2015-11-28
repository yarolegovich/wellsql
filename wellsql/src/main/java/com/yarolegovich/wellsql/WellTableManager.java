package com.yarolegovich.wellsql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.core.TableClass;
import com.yarolegovich.wellsql.core.annotation.PrimaryKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yarolegovich on 26.11.2015.
 */
public class WellTableManager {

    private SQLiteDatabase mDb;

    WellTableManager(SQLiteDatabase db) {
        mDb = db;
    }

    public void createTable(Class<? extends Identifiable> token) {
        TableClass table = WellSql.tableFor(token);
        mDb.execSQL(table.createStatement());
    }

    public void dropTable(Class<? extends Identifiable> token) {
        TableClass table = WellSql.tableFor(token);
        mDb.execSQL("DROP TABLE " + table.getTableName());
    }

}
