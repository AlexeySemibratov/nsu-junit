package org.nsu.junit;

import org.nsu.junit.common.Config;
import org.nsu.junit.common.TestResult;
import org.nsu.junit.common.TestRunner;

import java.util.List;
import java.util.function.Predicate;

public class Main {

    public static void main(String[] args) {
		if(args == null || args.length == 0) {
			printHelp();
			System.exit(-1);
		}

		int n = 0;
		try {
			n = Integer.parseInt(args[0]);
		} catch (NumberFormatException e){
			System.out.println("The argument <" + args[0] + "> must be an integer.");
			System.exit(-1);
		}
		if(n<=0) {
			System.out.println("[" + n + "] - Thread count must be positive integer.");
			System.exit(-1);
		}

		String[] tests = new String[args.length-1];
        System.arraycopy(args, 1, tests, 0, tests.length);

		int finalN = n;

        TestRunner runner = new TestRunner(2, tests);
        long startTime = System.nanoTime();
        runner.start();
        try {
            runner.joinAllTestThreads();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        printResults(runner.getResultsList(), (System.nanoTime() - startTime) / 1000000);
    }

    public static void printResults(List<TestResult> results, long totalTime) {
        results.stream()
                .filter(Predicate.not(x -> (!x.isFailed && !Config.SHOW_SUCCESSFUl_TESTS)))
                .forEach(System.out::println);

        System.out.println("Total tests: " + results.size() +
                ". Failed: " + results.stream().filter(x -> x.isFailed).count() +
                "." + " Total time: " + totalTime + " ms.");
    }

    public static void printHelp() {
        String help1 = "Invalid arguments. Use:";
        String help2 = "n -- Thread count";
        String help3 = "[className...] -- Class names";
        System.out.println(help1);
        System.out.println(help2);
        System.out.println(help3);
    }

}
