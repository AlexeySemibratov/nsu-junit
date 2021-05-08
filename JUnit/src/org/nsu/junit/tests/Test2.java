package org.nsu.junit.tests;

import org.nsu.junit.assertions.Assertions;
import org.nsu.junit.common.annotations.Test;

public class Test2 {

    @Test
    public void run() {
        Assertions.assertEquals(0, 1);
    }
}
