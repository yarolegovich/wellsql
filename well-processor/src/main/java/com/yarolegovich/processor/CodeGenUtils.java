package com.yarolegovich.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import com.yarolegovich.wellsql.core.Binder;

import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * Created by yarolegovich on 27.11.2015.
 */
public class CodeGenUtils {

    public static final String PACKAGE = Binder.PACKAGE;
    public static final String LOOKUP_CLASS = Binder.LOOKUP_CLASS;

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
}
