package org.nsu.junit.common.annotations;

import org.nsu.junit.assertions.AssertionException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Используется для пометки тестируемых методов.
 * Нельзя помечать одновременно с аннотациями {@link After}, {@link Before}.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {

    /**
     * Класс ожидаемого исключение.
     * Если в результате выполнения теста будет выброшено другое исключение, то тест считается не пройденным.
     */
    Class<? extends Throwable> expectedException() default DefaultException.class;

    class DefaultException extends Throwable {}
}

