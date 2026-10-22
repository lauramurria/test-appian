package com.testappian.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.testappian.config.ConfigReader;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * TestNG listener that logs each test to Extent Reports and captures a
 * screenshot on failure. Register via testng.xml's &lt;listeners&gt; or
 * @Listeners on a base test class.
 */
public class TestListener implements ITestListener {

    private final ExtentReports extent = ExtentManager.getInstance();
    private final Map<String, ExtentTest> tests = new HashMap<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(result.getMethod().getMethodName());
        tests.put(result.getMethod().getMethodName(), test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        tests.get(result.getMethod().getMethodName()).pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = tests.get(result.getMethod().getMethodName());
        test.fail(result.getThrowable());

        WebDriver driver = findDriver(result.getInstance());
        if (driver != null) {
            String path = captureScreenshot(driver, result.getMethod().getMethodName());
            if (path != null) {
                test.addScreenCaptureFromPath(path);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        tests.get(result.getMethod().getMethodName()).skip("Test skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    /** Reflectively pulls the `driver` field off the test instance (set by BaseTest). */
    private WebDriver findDriver(Object testInstance) {
        try {
            Field field = testInstance.getClass().getSuperclass().getDeclaredField("driver");
            field.setAccessible(true);
            return (WebDriver) field.get(testInstance);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private String captureScreenshot(WebDriver driver, String testName) {
        try {
            File screenshotDir = new File(ConfigReader.get("screenshots.dir"));
            screenshotDir.mkdirs();
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = Paths.get(screenshotDir.getPath(), testName + ".png").toFile();
            Files.copy(src.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return dest.getPath();
        } catch (IOException | ClassCastException e) {
            return null;
        }
    }
}
