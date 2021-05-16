package org.nsu.junit.common;

import java.util.ArrayDeque;

public class TestsQueue {

    private final ArrayDeque<String> testQueue;
    private final ThreadStopCondition stopCondition;

    public TestsQueue(ThreadStopCondition threadStopCondition) {
        this.testQueue = new ArrayDeque<>();
        this.stopCondition = threadStopCondition;
    }

    synchronized public void putTest(String testName) {
        testQueue.add(testName);
        notifyAll();
    }

    synchronized public Class<?> getTest() throws InterruptedException {
        if (testQueue.isEmpty() && stopCondition.toStop()) {
            throw new InterruptedException();
        }
        Class<?> testClass = null;
        String className = testQueue.pollFirst();

        if (className == null) return null;

        try {
            testClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return testClass;
    }
}
