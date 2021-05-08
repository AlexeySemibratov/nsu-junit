package org.nsu.junit;

import org.nsu.junit.common.Config;
import org.nsu.junit.common.TestRunner;
import org.nsu.junit.tests.Test1;
import org.nsu.junit.tests.Test2;
import org.nsu.junit.tests.Test3;


public class Main {

    private static int threadCount = 0;
    private static String[] testNames;

    public static void main(String[] args) {
        if (!parseArguments(args)) {
            printHelp();
            return;
        }

        TestRunner runner = new TestRunner(threadCount);
        runner.start();
        for (String test: testNames) {
            runner.addTestClass(test);
        }
        long startTime = System.nanoTime();
        try {
            runner.joinAllTestThreads();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Total tests: " + runner.totalTest);
        System.out.println("Failed tests: " + runner.failedTest);
        System.out.println("Total time: " + (System.nanoTime() - startTime) / 1000000 + " ms.");
    }

    private static boolean parseArguments(String[] args) {
        if (args == null || args.length == 0) {
            return false;
        }

        try {
            threadCount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("The argument <" + args[0] + "> must be an integer.");
            return false;
        }

        int i = 1;
        String command;
        while ((i < args.length) && (command = args[i]).startsWith("-")) {
            i++;
            if ("pcf".equals(command)) {
                Config.SHOW_SUCCESSFUL_TESTS = false;
            } else if ("-et".equals(command)) {
                Config.SHOW_EXECUTION_TIME = false;
            } else if ("-eth".equals(command)) {
                Config.SHOW_EXECUTION_THREAD = false;
            } else {
                System.out.println("Invalid command [" + command + "]");
                return false;
            }
        }
        testNames = new String[args.length-i];
        System.arraycopy(args, i, testNames, 0, testNames.length);
        return true;
    }


    private static void printHelp() {
        System.out.println("n -- Thread count");
        System.out.println("-pcf -- If you don't want to print successful tests");
        System.out.println("-et -- If you don't want to print tests execution time");
        System.out.println("-eth -- If you don't want to print tests execution thread");
        System.out.println("[className...] -- Class names");
    }

}
