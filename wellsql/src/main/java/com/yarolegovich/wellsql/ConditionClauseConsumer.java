package com.yarolegovich.wellsql;

/**
 * Created by yarolegovich on 26.11.2015.
 */
interface ConditionClauseConsumer {
    void acceptClause(String where);
    void acceptArgs(String[] whereArgs);
}
