package com.yarolegovich.processor;

import com.yarolegovich.wellsql.core.annotation.Column;
import com.yarolegovich.wellsql.core.annotation.RawConstraints;
import com.yarolegovich.wellsql.core.annotation.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;

/**
 * Created by yarolegovich on 23.11.2015.
 */
public class TableAnnotatedClass {

    private String tableName;
    private String tableConstraints = "";

    private List<ColumnAnnotatedField> columns;

    public TableAnnotatedClass(Element typeElement) throws TableCreationException {

        Table tableAnnotation = typeElement.getAnnotation(Table.class);
        String userDefinedName = tableAnnotation.name();
        tableName = userDefinedName.equals("") ?
                typeElement.getSimpleName().toString() :
                userDefinedName;

        columns = new ArrayList<>();
        for (Element column : typeElement.getEnclosedElements()) {
            Column columnAnnotation = column.getAnnotation(Column.class);
            if (columnAnnotation == null) {
                continue;
            }
            columns.add(new ColumnAnnotatedField(column, columnAnnotation));
        }

        RawConstraints rawConstraints = typeElement.getAnnotation(RawConstraints.class);
        if (rawConstraints != null) {
            String[] values = rawConstraints.value();
            List<String> tableConstraintList = new ArrayList<>();
            Collections.addAll(tableConstraintList, values);
            tableConstraints = Utils.join(",", tableConstraintList);
        }
    }

    public boolean isAutoincrement() {
        for (ColumnAnnotatedField column : columns) {
            if (column.isPrimaryKey()) {
                return column.isAutoincrement();
            }
        }
        return false;
    }

    public boolean hasDate() {
        for (ColumnAnnotatedField column : columns) {
            if (column.isDate()) {
                return true;
            }
        }
        return false;
    }

    public String getTableName() {
        return tableName;
    }

    public List<ColumnAnnotatedField> columns() {
        return Collections.unmodifiableList(columns);
    }

    public String toTableDeclaration() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName).append(" (");
        List<String> columnDefinitions = new ArrayList<>();
        for (ColumnAnnotatedField column : columns) {
            columnDefinitions.add(column.toColumnDeclaration());
        }
        sb.append(Utils.join(",", columnDefinitions));
        if (!tableConstraints.equals("")) {
            sb.append(",").append(tableConstraints);
        }
        sb.append(")");
        return sb.toString();
    }
}
