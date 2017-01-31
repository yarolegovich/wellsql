package com.yarolegovich.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import com.yarolegovich.wellsql.core.Binder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * Created by yarolegovich on 27.11.2015.
 */
public class CodeGenUtils {

    public static final String PACKAGE = Binder.PACKAGE;
    public static final String LOOKUP_CLASS = Binder.LOOKUP_CLASS;

    public static final String FORMATTER = "formatter";

    public static MethodSpec.Builder interfaceMethod(String name) {
        return MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);
    }

    public static TypeName wildcard(Class<?> clazz) {
        return ParameterizedTypeName.get(ClassName.get(clazz), TypeVariableName.get("?"));
    }

    public static TypeName mapOfParametrized(Class<? extends Map> map, Class<?> first, Class<?> second) {
        return ParameterizedTypeName.get(ClassName.get(map), wildcard(first), wildcard(second));
    }

    public static String putForToken(String type) {
        return "$N.put($T.class, new " + type + "())";
    }

    public static String toGetter(String fieldName) {
        return (!fieldName.startsWith("is") ? "get" +
                Character.toUpperCase(fieldName.charAt(0)) +
                fieldName.substring(1) : fieldName);
    }

    public static String toSetter(String fieldName) {
        return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    public static String toCvStatement(ColumnAnnotatedField column) {
        return "cv.put($S, " + extractValue(column) + ")";
    }

    public static String toConvertStatement(ColumnAnnotatedField column) {
        String setter = "item." + toSetter(column.getFieldName()) + "(" + casts.get(column.getClassName()) + ")";
        return !column.isDate() ? setter :
                "try { " + setter + "; } catch (Exception e) { throw new RuntimeException(\"Only format 'yyyy-MM-dd' is supported, conversion failed\"); }";
    }

    public static String extractValue(ColumnAnnotatedField column, boolean formatIfDate) {
        String getter = "item." + toGetter(column.getFieldName()) + "()";
        return !(column.isDate() && formatIfDate) ? getter : FORMATTER + ".format(" + getter + ")";
    }

    public static String extractValue(ColumnAnnotatedField column) {
        return extractValue(column, true);
    }

    public static String cvGetNotNull() {
        return "if (cv.get($S) != null)";
    }

    private static final Map<String, String> casts;
    static {
        casts = new HashMap<>();

        casts.put(boolean.class.getCanonicalName(), "((Long) cv.get($S)) == 1");
        casts.put(Boolean.class.getCanonicalName(), casts.get(boolean.class.getCanonicalName()));

        casts.put(short.class.getCanonicalName(), "((Long) cv.get($S)).shortValue()");
        casts.put(Short.class.getCanonicalName(), casts.get(short.class.getCanonicalName()));

        casts.put(long.class.getCanonicalName(), "((Long) cv.get($S)).longValue()");
        casts.put(Long.class.getCanonicalName(), casts.get(long.class.getCanonicalName()));

        casts.put(byte.class.getCanonicalName(), "((Long) cv.get($S)).byteValue()");
        casts.put(Byte.class.getCanonicalName(), casts.get(byte.class.getCanonicalName()));

        casts.put(int.class.getCanonicalName(), "((Long) cv.get($S)).intValue()");
        casts.put(Integer.class.getCanonicalName(), casts.get(int.class.getCanonicalName()));

        casts.put(double.class.getCanonicalName(), "((Double) cv.get($S)).doubleValue()");
        casts.put(Double.class.getCanonicalName(), casts.get(double.class.getCanonicalName()));

        casts.put(float.class.getCanonicalName(), "((Double) cv.get($S)).floatValue()");
        casts.put(Float.class.getCanonicalName(), casts.get(float.class.getCanonicalName()));

        casts.put(String.class.getCanonicalName(), "(String) cv.get($S)");
        casts.put(Date.class.getCanonicalName(), FORMATTER + ".parse((String) cv.get($S))");
    }
}
