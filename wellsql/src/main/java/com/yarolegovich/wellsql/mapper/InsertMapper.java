package com.yarolegovich.wellsql.mapper;

import android.content.ContentValues;

/**
 * Created by yarolegovich on 26.11.2015.
 */
public interface InsertMapper<T> {
    ContentValues toCv(T item);
}
