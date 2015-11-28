package com.yarolegovich.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;

/**
 * Created by yarolegovich on 24.11.2015.
 */
public class Utils {

    private static Elements elements;

    public static void init(ProcessingEnvironment env) {
        elements = env.getElementUtils();
    }


    @SuppressWarnings("unchecked")
    public static <T>T extractValue(AnnotationMirror mirror, String fieldName, Class<T> clazz) throws TableCreationException {
        Map<ExecutableElement, AnnotationValue> values =
                (Map<ExecutableElement, AnnotationValue>) elements.getElementValuesWithDefaults(mirror);
        List<String> list = new ArrayList<>();
        for (Map.Entry<ExecutableElement, AnnotationValue> entry : values.entrySet()) {
            if (entry.getKey().getSimpleName().contentEquals(fieldName)) {
                return clazz.cast(entry.getValue().getValue());
            }
            list.add(entry.getKey().getSimpleName().toString());
        }
        throw new TableCreationException(fieldName + " can't be set to null" + Utils.join(";", list));
    }

    public static String join(String delimiter, Object... objects) {
        return join(delimiter, Arrays.asList(objects));
    }

    public static String join(String delimiter, Collection<?> objects) {
        StringBuilder sb = new StringBuilder();
        Iterator<?> iterator = filterNulls(objects).iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            sb.append(String.valueOf(next));
            if (iterator.hasNext()) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static List<?> filterNulls(Collection<?> objects) {
        List<Object> notNulls = new ArrayList<>();
        for (Object o : objects) {
            if (o != null) {
                notNulls.add(o);
            }
        }
        return notNulls;
    }
}
