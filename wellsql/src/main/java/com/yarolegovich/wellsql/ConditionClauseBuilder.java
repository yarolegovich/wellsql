package com.yarolegovich.wellsql;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yarolegovich on 20.11.2015.
 */
public class ConditionClauseBuilder<T extends ConditionClauseConsumer> {

    private StringBuilder mSelectionBuilder;
    private List<Object> mSelectionArgs;

    private T mParent;

    public ConditionClauseBuilder(T sb) {
        mSelectionArgs = new ArrayList<>();
        mSelectionBuilder = new StringBuilder();

        mParent = sb;
    }

    public ConditionClauseBuilder<T> contains(String column, Object value) {
        return matching(column, " LIKE ?", "%" + value + "%");
    }

    public ConditionClauseBuilder<T> notContains(String column, Object value) {
        return matching(column, " NOT LIKE ?", "%" + value + "%");
    }

    public ConditionClauseBuilder<T> startsWith(String column, Object value) {
        return matching(column, " NOT LIKE ?", value + "%");
    }

    public ConditionClauseBuilder<T> notStartsWith(String column, Object value) {
        return matching(column, " NOT LIKE ?", value + "%");
    }

    public ConditionClauseBuilder<T> endsWith(String column, Object value) {
        return matching(column, " NOT LIKE ?", "%" + value);
    }

    public ConditionClauseBuilder<T> notEndsWith(String column, Object value) {
        return matching(column, " NOT LIKE ?", "%" + value);
    }

    private ConditionClauseBuilder<T> matching(String column, String selection, String pattern) {
        check();
        mSelectionBuilder.append(column).append(selection);
        mSelectionArgs.add(pattern);
        return this;
    }

    public ConditionClauseBuilder<T> isIn(String column, Iterable<?> values) {
        check();
        mSelectionBuilder.append(column).append(" IN (");
        append(values);
        mSelectionBuilder.deleteCharAt(mSelectionBuilder.length() - 1);
        mSelectionBuilder.append(")");
        return this;
    }

    public ConditionClauseBuilder<T> isNotIn(String column, Iterable<?> values) {
        check();
        mSelectionBuilder.append(column).append(" NOT IN (");
        append(values);
        mSelectionBuilder.deleteCharAt(mSelectionBuilder.length() - 1);
        mSelectionBuilder.append(")");
        return this;
    }

    public ConditionClauseBuilder<T> isBetween(String column, Object lower, Object upper) {
        check();
        mSelectionBuilder.append(column).append(" BETWEEN ? AND ?");
        mSelectionArgs.add(lower);
        mSelectionArgs.add(upper);
        return this;
    }

    public ConditionClauseBuilder<T> isNotBetween(String column, Object lower, Object upper) {
        check();
        mSelectionBuilder.append(column).append(" NOT BETWEEN ? AND ?");
        mSelectionArgs.add(lower);
        mSelectionArgs.add(upper);
        return this;
    }

    public ConditionClauseBuilder<T> greaterThen(String column, Object value) {
        check();
        mSelectionBuilder.append(column).append(" > ?");
        mSelectionArgs.add(value);
        return this;
    }

    public ConditionClauseBuilder<T> greaterThenOrEqual(String column, Object value) {
        check();
        mSelectionBuilder.append(column).append(" >= ?");
        mSelectionArgs.add(value);
        return this;
    }

    public ConditionClauseBuilder<T> lessThen(String column, Object value) {
        check();
        mSelectionBuilder.append(column).append(" < ?");
        mSelectionArgs.add(value);
        return this;
    }

    public ConditionClauseBuilder<T> lessThenOrEqual(String column, Object value) {
        check();
        mSelectionBuilder.append(column).append(" <= ?");
        mSelectionArgs.add(value);
        return this;
    }

    public ConditionClauseBuilder<T> not() {
        check();
        mSelectionBuilder.append(" NOT ");
        return this;
    }

    public ConditionClauseBuilder<T> equals(String column, Object value) {
        check();
        mSelectionBuilder.append(column).append(" = ?");
        mSelectionArgs.add(value);
        return this;
    }

    public ConditionClauseBuilder<T> equals(String column, boolean value) {
        return equals(column, value ? 1 : 0);
    }

    public ConditionClauseBuilder<T> or() {
        mSelectionBuilder.append(" OR ");
        return this;
    }

    public ConditionClauseBuilder<T> beginGroup() {
        check();
        mSelectionBuilder.append('(');
        return this;
    }

    public ConditionClauseBuilder<T> endGroup() {
        mSelectionBuilder.append(')');
        return this;
    }

    public T endWhere() {
        mParent.acceptClause(mSelectionBuilder.toString().trim());
        if (!mSelectionArgs.isEmpty()) {
            String[] args = new String[mSelectionArgs.size()];
            for (int i = 0; i < mSelectionArgs.size(); i++) {
                args[i] = String.valueOf(mSelectionArgs.get(i));
            }
            mParent.acceptArgs(args);
        }
        return mParent;
    }

    private void append(Iterable<?> values) {
        for (Object val : values) {
            mSelectionBuilder.append("?,");
            mSelectionArgs.add(val);
        }
    }

    private void check() {
        if (mSelectionBuilder.length() == 0) {
            return;
        }
        char lastChar = mSelectionBuilder.charAt(mSelectionBuilder.length() - 1);
        if (lastChar == '(' || lastChar == ' ') {
            return;
        }
        mSelectionBuilder.append(" AND ");
    }

}
