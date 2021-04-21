package org.nsu.junit.common;

public class TestResult {

    private String sourceClass;
    private String methodName;
    private String optionalMessage;
    private String executionThread;

    private long executeTime;

    public boolean isFailed = false;

    private static final String RED = "\u001b[31m";
    private static final String GREEN = "\u001b[32m";

    public TestResult(String sourceClass, String testName) {
        this.sourceClass = sourceClass;
        this.methodName = testName;
    }

    public TestResult(String sourceClass, String testName, String optionalMessage) {
        this(sourceClass, testName);
        this.optionalMessage = optionalMessage;
    }

    public TestResult(String sourceClass, String testName, String optionalMessage, String executionThread) {
        this(sourceClass, testName, optionalMessage);
        this.executionThread = executionThread;
    }

    public void setOptionalMessage(String optionalMessage) {
        this.optionalMessage = optionalMessage;
    }

    public void setExecutionThread(String executionThread) {
        this.executionThread = executionThread;
    }

    public void setExecuteTime(long time) {
        this.executeTime = time;
    }

    @Override
    public String toString() {
        String result = optionalMessage != null ? ("" + optionalMessage + " ") : "";

        if (isFailed) result += "Test <" + methodName + ">" + " in class <" + sourceClass + "> failed!";
        else {
            result += "Test <" + methodName + ">" + " in class <" + sourceClass + "> is successfull!";
            if (Config.SHOW_EXECUTION_TIME) result += " " + (executeTime / 1000000) + " ms.";
        }

        if (Config.SHOW_EXECUTION_THREAD) result += " Thread [" + executionThread + "]";

        if (Config.COLORED_OUTPUT) return isFailed ? RED + result : GREEN + result;
        else return result;
    }
}
