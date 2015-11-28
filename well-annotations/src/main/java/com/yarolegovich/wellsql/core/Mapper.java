package com.yarolegovich.wellsql.core;


import java.util.Map;

/**
 * Created by yarolegovich on 24.11.2015.
 */
public interface Mapper<T> {
    Map<String, Object> toContentValues(T item);
    T convert(Map<String, Object> cursor);
}
