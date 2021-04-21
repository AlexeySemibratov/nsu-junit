package org.nsu.junit.common;

import org.nsu.junit.common.annotations.After;
import org.nsu.junit.common.annotations.Before;
import org.nsu.junit.common.annotations.Test;
import org.nsu.junit.common.exceptions.TestException;
import org.nsu.junit.common.util.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class TestRunner {

    private final List<TestResult> results = new ArrayList<>();
    private final TestsQueue testsQueue;
    private final Thread[] threads;

    public TestRunner(int threadCount, String[] testNames) {
        testsQueue = new TestsQueue();
        for (String e : testNames) {
            testsQueue.putTest(e);
        }

        threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(testTask());
            threads[i].setName("testThread_" + i);
        }
    }

    private Runnable testTask() {
        return () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Class<?> testClass = testsQueue.getTest();
                    if (testClass != null) runTest(testClass);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
    }

    public void start() {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    public void joinAllTestThreads() throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }

    private void runTest(Class<?> testClass) {
        if (testClass == null) return;

        List<Method> beforeMethods = Utils.getMethodsWithAnnotation(testClass, Before.class);
        List<Method> testMethods = Utils.getMethodsWithAnnotation(testClass, Test.class);
        List<Method> afterMethods = Utils.getMethodsWithAnnotation(testClass, After.class);

        runTestList(testClass, testMethods, beforeMethods, afterMethods);
    }

    private void runTestList(
            Class<?> source,
            List<Method> methods,
            List<Method> beforeMethods,
            List<Method> afterMethods
    ) {
        if (methods == null) return;

        for (Method m : methods) {
            if (Utils.methodHasIncompatibleAnnotations(m)) {
                System.out.println("Test <" + m.getName() + "> can't be started because it has incompatible annotations: " +
                        Arrays.toString(m.getDeclaredAnnotations()));
                continue;
            }
            TestResult result = new TestResult(source.getCanonicalName(), m.getName());
            result.setExecutionThread(Thread.currentThread().getName());

            long startTime = System.nanoTime();
            try {
                Object instance = source.getDeclaredConstructor().newInstance();

                if (!runMethodsOnInstance(beforeMethods, instance)) {
                    throw new TestException("Exception in @Before method in " + source.getName());
                }

                m.invoke(instance);

                if (!runMethodsOnInstance(afterMethods, instance)) {
                    throw new TestException("Exception in @After method in " + source.getName());
                }
            } catch (InvocationTargetException e) {
                result.isFailed = true;

                Test ann = m.getAnnotation(Test.class);
                if (!(ann.expectedException() == e.getCause().getClass())) {
                    result.setOptionalMessage(
                            "Catch <" + e.getCause().getClass().getName() +
                                    "> but expected <" +
                                    ann.expectedException().getName() + ">. \n"
                                    + e.getCause().getMessage()
                    );
                } else {
                    result.setOptionalMessage(e.getCause().getMessage());
                }

            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
            } catch (TestException e) {
                result.isFailed = true;
                result.setOptionalMessage(e.getMessage());
            }
            result.setExecuteTime(System.nanoTime() - startTime);
            results.add(result);
        }
    }

    private boolean runMethodsOnInstance(List<Method> methods, Object instance) {
        if (methods == null) return true;

        for (Method m : methods) {
            try {
                m.invoke(instance);
            } catch (InvocationTargetException | IllegalAccessException e) {
                return false;
            }
        }
        return true;
    }

    public List<TestResult> getResultsList() {
        return results;
    }

}
