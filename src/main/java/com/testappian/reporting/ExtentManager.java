package com.testappian.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.testappian.config.ConfigReader;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Lazily creates a single ExtentReports instance for the whole test run. */
public final class ExtentManager {

    private static ExtentReports extent;

    private ExtentManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File reportDir = new File(ConfigReader.get("reports.dir"));
            reportDir.mkdirs();
            File reportFile = Paths.get(reportDir.getPath(), "report_" + timestamp + ".html").toFile();

            ExtentSparkReporter spark = new ExtentSparkReporter(reportFile.getPath());
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
        return extent;
    }
}
