package org.nsu.junit.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает, что метод должен быть выполнен после каждого тестом.
 * Можно помечать несколько методов в одном классе, однако порядок выполнения может быть любым.
 * Если метод выбросит исключение, то тесты в классе не будут выполняться
 * Нельзя помечать одновременно с аннотациями {@link After}, {@link Test}.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {
}
