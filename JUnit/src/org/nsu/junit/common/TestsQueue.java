package org.nsu.junit.common;

import java.util.ArrayDeque;

public class TestsQueue {

    private final ArrayDeque<String> testQueue;

    public TestsQueue() {
        testQueue = new ArrayDeque<>();
    }

    synchronized public void putTest(String testName) {
        testQueue.add(testName);
        notifyAll();
    }

    synchronized public Class<?> getTest() throws InterruptedException {
        if (testQueue.isEmpty()) {
            wait(250);
            if (testQueue.isEmpty()) {
                throw new InterruptedException();
            }
        }
        Class<?> testClass = null;
        String className = testQueue.pollFirst();
        try {
            testClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.out.println("Class: " + className + " not found.");
            e.printStackTrace();
        }
        return testClass;
    }
}
