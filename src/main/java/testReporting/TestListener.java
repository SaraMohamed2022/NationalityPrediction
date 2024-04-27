package testReporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;


public class TestListener implements ITestListener {
//        private static ExtentReports extent = new ExtentReports();
//        private static ExtentTest test;
//        public static ExtentReports extentReports;
//        @Override
//        public void onStart(ITestContext context) {
//            String fileName = ExtentReportManager.getReportNameWithTimeStamp();
//            String fullReportPath = System.getProperty("user.dir") + "\\reports\\" + fileName;
//            extentReports = ExtentReportManager.createInstance(fullReportPath, "Test API Automation Report", "Test ExecutionReport");
//        }
//
//        @Override
//        public void onFinish(ITestContext context) {
//            if (extentReports != null)
//                extentReports.flush();
//        }
//
//        @Override
//        public void onTestStart(ITestResult result) {
//            test = extent.createTest(result.getMethod().getMethodName());
//        }
//
//        @Override
//        public void onTestSuccess(ITestResult result) {
//            test.log(Status.PASS, "Test Passed");
//        }
//
//        @Override
//        public void onTestFailure(ITestResult result) {
//            test.log(Status.FAIL, "Test Failed");
//            test.log(Status.FAIL, result.getThrowable());
//        }
//
//        @Override
//        public void onTestSkipped(ITestResult result) {
//            test.log(Status.SKIP, "Test Skipped");
//        }

    protected static ExtentReports reports;
    protected static ExtentTest test;
    private static String resultpath = getResultPath();

    private static String getResultPath() {
        resultpath = "test";
        if (!new File(resultpath).isDirectory()) {
            new File(resultpath);
        }
        return resultpath;
    }

    public void onTestStart(ITestResult result) {

        test = reports.createTest(result.getMethod().getMethodName());
        test.log(Status.INFO, result.getMethod().getMethodName());
    }

    public void onTestSuccess(ITestResult result) {
        test.log(Status.PASS, "Test is pass");

    }

    public void onTestFailure(ITestResult result) {
        test.log(Status.FAIL, "Test is fail");

    }

    public void onTestSkipped(ITestResult result) {
        test.log(Status.SKIP, "Test is skipped");

    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // TODO Auto-generated method stub

    }

    public void onStart(ITestContext context) {
        String fileName = ExtentReportManager.getReportNameWithTimeStamp();
        String fullReportPath = System.getProperty("user.dir") + "\\reports\\" + fileName;
        reports = ExtentReportManager.createInstance(fullReportPath, "GetNationalityProbability API Automation Report", "Test ExecutionReport");
    }

    public void onFinish(ITestContext context) {
        reports.flush();
    }
}
