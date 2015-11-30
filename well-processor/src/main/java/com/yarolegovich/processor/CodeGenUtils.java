package com.yarolegovich.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import com.yarolegovich.wellsql.core.Binder;
import com.yarolegovich.wellsql.core.ColumnType;

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

    private static String toGetter(String fieldName) {
        return (!fieldName.startsWith("is") ? "get" +
                Character.toUpperCase(fieldName.charAt(0)) +
                fieldName.substring(1) : fieldName);
    }

    private static String toSetter(String fieldName) {
        return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    public static String toCvStatement(ColumnAnnotatedField column) {
        return "cv.put($S, " + extractValue(column) + ")";
    }

    public static String toConvertStatement(ColumnAnnotatedField column) {
        String setter = "item." + toSetter(column.getFieldName()) + "(" + casts.get(column.getClassName()) + ")";
        return !column.isDate() ? setter : "try { " + setter + "; } catch (Exception e) { /* NOP */ }";
    }

    public static String extractValue(ColumnAnnotatedField column) {
        String getter = "item." + toGetter(column.getFieldName()) + "()";
        return !column.isDate() ? getter : FORMATTER + ".format(" + getter + ")";
    }

    private static final Map<String, String> casts;
    static {
        casts = new HashMap<>();

        casts.put(boolean.class.getCanonicalName(), "((Integer) cv.get($S)) == 1");
        casts.put(short.class.getCanonicalName(), "((Integer) cv.get($S)).shortValue()");
        casts.put(long.class.getCanonicalName(), "((Integer) cv.get($S)).longValue()");
        casts.put(byte.class.getCanonicalName(), "((Integer) cv.get($S)).byteValue()");

        casts.put(Boolean.class.getCanonicalName(), casts.get(boolean.class.getCanonicalName()));
        casts.put(Short.class.getCanonicalName(), casts.get(short.class.getCanonicalName()));
        casts.put(Long.class.getCanonicalName(), casts.get(long.class.getCanonicalName()));
        casts.put(Byte.class.getCanonicalName(), casts.get(byte.class.getCanonicalName()));

        casts.put(double.class.getCanonicalName(), "((Float) cv.get($S)).doubleValue()");

        casts.put(Double.class.getCanonicalName(), casts.get(double.class.getCanonicalName()));

        casts.put(int.class.getCanonicalName(), "(Integer) cv.get($S)");
        casts.put(float.class.getCanonicalName(), "(Float) cv.get($S)");

        casts.put(Integer.class.getCanonicalName(), casts.get(int.class.getCanonicalName()));
        casts.put(Float.class.getCanonicalName(), casts.get(float.class.getCanonicalName()));

        casts.put(String.class.getCanonicalName(), "(String) cv.get($S)");
        casts.put(Date.class.getCanonicalName(), FORMATTER + ".parse((String) cv.get($S))");
    }
}
