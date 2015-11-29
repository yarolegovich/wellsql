package com.yarolegovich.processor;

import com.yarolegovich.wellsql.core.annotation.Check;
import com.yarolegovich.wellsql.core.annotation.Column;
import com.yarolegovich.wellsql.core.ColumnType;
import com.yarolegovich.wellsql.core.annotation.NotNull;
import com.yarolegovich.wellsql.core.annotation.PrimaryKey;
import com.yarolegovich.wellsql.core.annotation.Unique;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;


/**
 * Created by yarolegovich on 23.11.2015.
 */
public class ColumnAnnotatedField {

    private String columnName;
    private String columnType;
    private String columnConstraints;

    private String fieldName;
    private String fieldClass;

    private boolean isPrimaryKey;
    private boolean isAutoincrement;

    public ColumnAnnotatedField(Element element, Column annotation) throws TableCreationException{

        String userDefinedName = annotation.name();
        fieldName = element.getSimpleName().toString();
        fieldName = fieldName.replaceAll("^m[A-Z]",
                String.valueOf(Character.toLowerCase(fieldName.charAt(1))));

        columnName = userDefinedName.equals("") ?
                fieldName.toUpperCase() :
                userDefinedName;

        String type = element.asType().toString();
        fieldClass = type;
        String userDefinedType = annotation.type();
        columnType = userDefinedType.equals("") ?
                inferFieldType(type) :
                userDefinedType;

        StringBuilder cb = new StringBuilder();
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            cb.append(interpret(mirror));
        }
        if (cb.length() != 0) {
            columnConstraints = cb.toString();
        }
    }

    public String getName() {
        return columnName;
    }

    public String toColumnDeclaration() {
        return Utils.join(" ",
                columnName,
                columnType,
                (columnConstraints != null ?
                        columnConstraints.trim() :
                        null));
    }

    private String inferFieldType(String className) throws TableCreationException {
        String type = typeMapping.get(className);
        if (type == null) {
            throw new TableCreationException("Can't infer column type: " + className);
        } else {
            return type;
        }
    }

    private String interpret(AnnotationMirror mirror) throws TableCreationException {
        String simpleName = mirror.getAnnotationType().asElement().getSimpleName().toString();
        if (simpleName.equals(PrimaryKey.class.getSimpleName())) {
            isPrimaryKey = true;
            isAutoincrement = Utils.extractValue(mirror, "autoincrement", Boolean.class);
            columnName = "_" + columnName.toLowerCase();
            return " PRIMARY KEY" + (isAutoincrement ? " AUTOINCREMENT" : "");
        } else if (simpleName.equals(NotNull.class.getSimpleName())) {
            return " NOT NULL";
        } else if (simpleName.equals(Unique.class.getSimpleName())) {
            return " UNIQUE";
        } else if (simpleName.equals(Check.class.getSimpleName())) {
            String constraint = Utils.extractValue(mirror, "value", String.class);
            boolean hasConstraint = !constraint.equals("");
            return " CHECK" + (hasConstraint ? " (" + constraint + ")" : "");
        } else {
            return "";
        }
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isAutoincrement() {
        return isAutoincrement;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getClassName() {
        return primitiveWrappers.containsKey(fieldClass) ?
                primitiveWrappers.get(fieldClass) : fieldClass;
    }

    private static Map<String, String> primitiveWrappers;
    private static Map<String, String> typeMapping;
    static {
        primitiveWrappers = new HashMap<>();
        typeMapping = new HashMap<>();

        typeMapping.put(Integer.class.getCanonicalName(), ColumnType.INTEGER);
        typeMapping.put(Long.class.getCanonicalName(), ColumnType.INTEGER);
        typeMapping.put(Short.class.getCanonicalName(), ColumnType.INTEGER);
        typeMapping.put(Byte.class.getCanonicalName(), ColumnType.INTEGER);
        typeMapping.put(Boolean.class.getCanonicalName(), ColumnType.INTEGER);
        typeMapping.put(int.class.getCanonicalName(), ColumnType.INTEGER);
        typeMapping.put(long.class.getCanonicalName(), ColumnType.INTEGER);
        typeMapping.put(short.class.getCanonicalName(), ColumnType.INTEGER);
        typeMapping.put(byte.class.getCanonicalName(), ColumnType.INTEGER);
        typeMapping.put(boolean.class.getCanonicalName(), ColumnType.INTEGER);

        typeMapping.put(Double.class.getCanonicalName(), ColumnType.REAL);
        typeMapping.put(Float.class.getCanonicalName(), ColumnType.REAL);
        typeMapping.put(double.class.getCanonicalName(), ColumnType.REAL);
        typeMapping.put(float.class.getCanonicalName(), ColumnType.REAL);

        typeMapping.put(String.class.getCanonicalName(), ColumnType.TEXT);

        typeMapping.put(byte[].class.getCanonicalName(), ColumnType.BLOB);

        primitiveWrappers.put(int.class.getSimpleName(), Integer.class.getSimpleName());

    }
}
