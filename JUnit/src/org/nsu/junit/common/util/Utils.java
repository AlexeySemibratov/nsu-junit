package org.nsu.junit.common.util;

import org.nsu.junit.common.annotations.After;
import org.nsu.junit.common.annotations.Before;
import org.nsu.junit.common.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<Method> getMethodsWithAnnotation(Class<?> source, Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<>();
        for (Method m : source.getDeclaredMethods()) {
            if (m.isAnnotationPresent(annotation)) {
                methods.add(m);
            }
        }
        return methods;
    }

    public static boolean methodHasIncompatibleAnnotations(Method m) {
        if (m.isAnnotationPresent(Before.class)) {
            if (m.isAnnotationPresent(Test.class)) return true;
            if (m.isAnnotationPresent(After.class)) return true;
        } else if (m.isAnnotationPresent(Test.class)) {
            if (m.isAnnotationPresent(After.class)) return true;
        }
        return false;
    }
}
