package com.appium.manager;

import com.annotation.values.Description;
import com.annotation.values.SkipIf;
import com.report.factory.ExtentManager;
import com.report.factory.ExtentTestManager;
import org.testng.*;

import java.io.IOException;

public final class AppiumParallelTestListener
    implements ITestListener, IClassListener, IInvokedMethodListener {

    private ReportManager reportManager;
    private DeviceAllocationManager deviceAllocationManager;
    private ConfigFileManager prop;
    public AppiumServerManager appiumServerManager;
    public String testDescription = "";
    private int retryCount = 0;
    private int maxRetryCount;
    private AppiumDriverManager appiumDriverManager;

    public AppiumParallelTestListener() throws Exception {
        try {
            reportManager = new ReportManager();

            // TODO: 29/06/17 Setting up the report manager
            ExtentTestManager.setReportManager(reportManager);

            appiumServerManager = new AppiumServerManager();
            prop = ConfigFileManager.getInstance();
            deviceAllocationManager = DeviceAllocationManager.getInstance();
            appiumDriverManager = new AppiumDriverManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBeforeClass(ITestClass testClass) {
        try {
            String device = testClass.getXmlClass().getAllParameters().get("device").toString();
            String className = testClass.getRealClass().getSimpleName();
            deviceAllocationManager.allocateDevice(device,
                deviceAllocationManager.getNextAvailableDeviceId());
            appiumServerManager.startAppiumServer(className);
            if (getClass().getAnnotation(Description.class) != null) {
                testDescription = getClass().getAnnotation(Description.class).value();
            }
            reportManager.createParentNodeExtent(className, testDescription);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        try {
            appiumServerManager.stopAppiumServer();
            ExtentManager.getExtent().flush();
            deviceAllocationManager.freeDevice();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        try {
            appiumDriverManager.startAppiumDriver();
            reportManager.startLogResults(method.getTestMethod().getMethodName(),
                    testResult.getTestClass().getRealClass().getSimpleName());

            // TODO: 29/06/17 added setAuthorName here to create the node earlier
            reportManager.setAuthorName(method);

            SkipIf skip =
                    method.getTestMethod()
                            .getConstructorOrMethod()
                            .getMethod().getAnnotation(SkipIf.class);
            if (skip != null) {
                String info = skip.platform();
                if (AppiumDriverManager.getDriver().toString()
                        .split("\\(")[0].trim().toString().contains(info)) {
                    System.out.println("skipping test");
                    throw new SkipException("Skipped because property was set to :::" + info);
                }
            }
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        try {
            if (testResult.getStatus() == ITestResult.SUCCESS
                    || testResult.getStatus() == ITestResult.FAILURE) {
                // TODO: 29/06/17 removed set Author Name bc it's creating node at the end
//                reportManager.setAuthorName(method);
                reportManager.endLogTestResults(testResult);
            }
            appiumDriverManager.stopAppiumDriver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {

    }

    @Override
    public void onTestSuccess(ITestResult result) {

    }

    @Override
    public void onTestFailure(ITestResult result) {

    }

    /*
        Document to make codacy happy
     */
    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {

    }
}
