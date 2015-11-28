package com.yarolegovich.wellsql.mapper;

import android.database.Cursor;

/**
 * Created by yarolegovich on 26.11.2015.
 */
public interface SelectMapper<T> {
    T convert(Cursor cursor);
}
