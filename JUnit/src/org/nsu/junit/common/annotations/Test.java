package org.nsu.junit.common.annotations;

import org.nsu.junit.assertions.AssertionException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    Class<?> expectedException() default AssertionException.class;
}

