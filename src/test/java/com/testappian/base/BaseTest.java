package com.testappian.base;

import com.appiancorp.ps.automatedtest.fixture.SitesFixture;
import com.testappian.driver.DriverFactory;
import com.testappian.pages.HomePage;
import com.testappian.pages.LoginPage;
import com.testappian.reporting.TestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

/**
 * Common setup/teardown for every test class: creates the WebDriver and the
 * Appian SitesFixture, wires up page objects, and tears both down afterwards.
 */
@Listeners(TestListener.class)
public abstract class BaseTest {

    protected WebDriver driver;
    protected SitesFixture fixture;
    protected LoginPage loginPage;
    protected HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.createDriver();
        fixture = DriverFactory.createFixture(driver);

        loginPage = new LoginPage(driver, fixture);
        homePage = new HomePage(driver, fixture);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
