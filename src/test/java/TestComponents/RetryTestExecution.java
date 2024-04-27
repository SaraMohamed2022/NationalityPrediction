package TestComponents;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryTestExecution implements IRetryAnalyzer {

    //To be called only with the flaky testCases
    // by adding @Test (retryAnalyzer = RetryTestExecution.class)
    int runCount=0,MaxRunCount=1;

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (runCount<MaxRunCount)
        {
            runCount++;
            return true;
        }
        return false;
    }
}
