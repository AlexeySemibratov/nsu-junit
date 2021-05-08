package org.nsu.junit.common;

public class TestResult {

    private String sourceClass;
    private String methodName;
    private String optionalMessage;
    private String executionThread;

    private long executionTime;

    public boolean isFailed = false;

    public TestResult(String sourceClass, String testName) {
        this.sourceClass = sourceClass;
        this.methodName = testName;
    }

    public void setOptionalMessage(String optionalMessage) {
        this.optionalMessage = optionalMessage;
    }

    public void setExecutionThread(String executionThread) {
        this.executionThread = executionThread;
    }

    public void setExecutionTime(long time) {
        this.executionTime = time;
    }

    public String formatToString() {
        StringBuilder result = new StringBuilder("-----------------------------------------\n");
        result.append("Test class: ")
                .append(sourceClass)
                .append("\n")
                .append("Test method: ")
                .append(methodName)
                .append("\n")
                .append(optionalMessage != null ? ("" + optionalMessage + "\n") : "")
                .append("STATUS: ");
        if (isFailed) {
            result.append("FAIL");
        } else {
            result.append("SUCCESS");
        }
        result.append("\n");
        if (Config.SHOW_EXECUTION_THREAD) {
            result.append("Execution thread: ")
                    .append(executionThread)
                    .append("\n");
        }
        if (Config.SHOW_EXECUTION_TIME) {
            result.append("Execution time: ")
                    .append((executionTime / 1000000))
                    .append(" ms. \n");
        }
        result.append("-----------------------------------------");
        return result.toString();
    }
}
