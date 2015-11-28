package com.yarolegovich.wellsql.core;

/**
 * Created by yarolegovich on 23.11.2015.
 */
public interface TableClass {
    String createStatement();
    String getTableName();
    Class<?> getModelClass();
}
