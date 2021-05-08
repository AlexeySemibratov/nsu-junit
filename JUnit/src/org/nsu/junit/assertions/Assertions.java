package org.nsu.junit.assertions;

public class Assertions {

    public static void assertEquals(int expected, int actual) {
        if (expected == actual) return;
        failEquals(expected, actual);
    }

    public static void assertEquals(double expected, double actual) {
        if (expected == actual) return;
        failEquals(expected, actual);
    }

    public static void assertEquals(String expected, String actual) {
        if (expected.equals(actual)) return;
        failEquals(expected, actual);
    }

    public static void assertEquals(Object expected, Object actual) {
        if (expected.equals(actual)) return;
        failEquals(expected, actual);
    }

    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            failMessage(message + " Condition is not true!");
        }
    }

    public static void assertTrue(boolean condition) {
        assertTrue("Condition is not true!", condition);
    }

    public static void assertFalse(boolean condition) {
        assertFalse("Condition is not false!", condition);
    }

    public static void assertFalse(String message, boolean condition) {
        if (condition) {
            failMessage(message + " Condition is not false!");
        }
    }

    public static void assertNotNull(Object e) {
        if (e == null) {
            failMessage("Instanse is null");
        }
    }

    private static void failMessage(String message) {
        throw new AssertionException(message);
    }

    public static void failEquals(Object expected, Object actual) {
        failMessage(format("Not equals:", expected, actual));
    }

    private static String format(String str, Object expected, Object actual) {
        String form = "";
        if (str != null && str.length() > 0) {
            form = str + " ";
        }
        return form + "expected:<" + expected.toString() + "> but was:<" + actual.toString() + ">";
    }
}
