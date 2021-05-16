package org.nsu.junit.common;

import org.nsu.junit.common.annotations.After;
import org.nsu.junit.common.annotations.Before;
import org.nsu.junit.common.annotations.Test;
import org.nsu.junit.common.util.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TestRunner {
    private final TestsQueue testsQueue;
    private final Thread[] threads;

    public AtomicInteger totalTest = new AtomicInteger(0);
    public AtomicInteger failedTest = new AtomicInteger(0);
    public AtomicInteger totalClassTest = new AtomicInteger(0);

    public TestRunner(int threadCount, int testsCount) throws IllegalArgumentException {
        if (threadCount <= 0) {
            throw new IllegalArgumentException("The number of threads must be a positive integer");
        }

        testsQueue = new TestsQueue(() -> totalClassTest.get() == testsCount);

        threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new TestTask());
            threads[i].setName("testThread_" + i);
        }
    }

    public void addTestClass(String className) {
        testsQueue.putTest(className);
        totalClassTest.incrementAndGet();
    }

    public void start() {
        for (Thread thread : threads) {
            if (!thread.isAlive()) {
                thread.start();
            }
        }
    }

    public void joinAllTestThreads() throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }

    private void runTest(Class<?> testClass) {
        List<Method> beforeMethods = Utils.getMethodsWithAnnotation(testClass, Before.class);
        List<Method> testMethods = Utils.getMethodsWithAnnotation(testClass, Test.class);
        List<Method> afterMethods = Utils.getMethodsWithAnnotation(testClass, After.class);

        runTestList(testClass, testMethods, beforeMethods, afterMethods);
    }

    private void runTestList(
            Class<?> source,
            List<Method> testMethods,
            List<Method> beforeMethods,
            List<Method> afterMethods
    ) {
        if (testMethods == null || testMethods.isEmpty()) return;
        for (Method m : testMethods) {
            if (Utils.methodHasIncompatibleAnnotations(m)) {
                System.out.println("Test <" + m.getName() + "> can't be started because it has incompatible annotations: " +
                        Arrays.toString(m.getDeclaredAnnotations()));
                continue;
            }
            TestResult result = new TestResult(source.getCanonicalName(), m.getName());
            result.setExecutionThread(Thread.currentThread().getName());

            Object instance = null;
            try {
                instance = source.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            long startTime = System.nanoTime();

            try {
                if (!runMethodsOnInstance(beforeMethods, instance, "Exception in @Before method:")) {
                    return;
                } else {
                    m.invoke(instance);
                }
            } catch (InvocationTargetException e) {
                Test ann = m.getAnnotation(Test.class);
                Class<?> expectedException = ann.expectedException();

                if (expectedException == Test.DefaultException.class) {
                    failTest(result, e.getCause().getMessage());
                } else if (expectedException != e.getCause().getClass()) {
                    String message = "Catch <" +
                            e.getCause().getClass().getName() +
                            "> but expected <" +
                            expectedException.getName() +
                            ">. \n" +
                            e.getCause().getMessage();

                    failTest(result, message);
                } else {
                    result.setOptionalMessage(e.getCause().getMessage());
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }

            totalTest.incrementAndGet();
            if (result.isFailed) {
                failedTest.incrementAndGet();
            }
            result.setExecutionTime(System.nanoTime() - startTime);

            printResult(result);

            if (!runMethodsOnInstance(afterMethods, instance, "Exception in @After method")) {
                return;
            }
        }
    }

    private boolean runMethodsOnInstance(List<Method> methods, Object instance, String message) {
        if (methods == null || methods.isEmpty()) return true;

        for (Method m : methods) {
            try {
                m.invoke(instance);
            } catch (InvocationTargetException | IllegalAccessException e) {
                printErrorMessage(m, message, e);
                return false;
            }
        }
        return true;
    }

    private void failTest(TestResult result, String message) {
        result.isFailed = true;
        result.setOptionalMessage(message);
    }

    private void printErrorMessage(Method method, String message, Throwable throwable) {
        synchronized (System.out) {
            System.out.println(message);
            System.out.println("Error while try to execute <" + method + ">");
            if (throwable.getCause() != null) {
                throwable.getCause().printStackTrace(System.out);
            }
        }
    }

    private void printResult(TestResult result) {
        synchronized (System.out) {
            if (result.isFailed || Config.SHOW_SUCCESSFUL_TESTS) {
                System.out.println(result.formatToString());
            }
        }
    }

    private class TestTask implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Class<?> testClass = testsQueue.getTest();
                    if (testClass != null) {
                        runTest(testClass);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
