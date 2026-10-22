package com.testappian.driver;

import com.appiancorp.ps.automatedtest.fixture.SitesFixture;
import com.testappian.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Builds a Selenium WebDriver and wraps it in Appian's SitesFixture, which
 * adds Appian-aware waits (e.g. for SAIL's async progress bar) and login
 * helpers on top of raw Selenium.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = ConfigReader.browser().toLowerCase();
        switch (browser) {
            case "chrome":
                return createChromeDriver();
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser
                        + ". Add a case in DriverFactory.createDriver() to support it.");
        }
    }

    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (ConfigReader.headless()) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }
        return new ChromeDriver(options);
    }

    public static SitesFixture createFixture(WebDriver driver) {
        SitesFixture fixture = new SitesFixture();
        fixture.setWebDriver(driver);
        fixture.setTimeoutSecondsTo(ConfigReader.timeoutSeconds());
        fixture.setAppianVersionTo(ConfigReader.appianVersion());
        fixture.setAppianLocaleTo(ConfigReader.appianLocale());
        return fixture;
    }
}
