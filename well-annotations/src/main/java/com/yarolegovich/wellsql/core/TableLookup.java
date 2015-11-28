package com.yarolegovich.wellsql.core;

import java.util.Set;

/**
 * Created by yarolegovich on 25.11.2015.
 */
public interface TableLookup {
    <T>Mapper<T> getMapper(Class<T> token);
    TableClass getTable(Class<?> token);

    Set<Class<?>> getMapperTokens();
    Set<Class<?>> getTableTokens();
}
