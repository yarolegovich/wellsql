package com.yarolegovich.wellsql.mapper;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by yarolegovich on 25.11.2015.
 */
public interface SQLiteMapper<T> extends InsertMapper<T>, SelectMapper<T> {}
