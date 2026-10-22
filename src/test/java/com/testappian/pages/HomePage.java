package com.testappian.pages;

import com.appiancorp.ps.automatedtest.fixture.SitesFixture;
import org.openqa.selenium.WebDriver;

/**
 * Represents the landing page/site shown immediately after login.
 * TODO: replace the placeholder check in isLoaded() with something specific
 * to your Appian site once you know what the post-login page looks like
 * (e.g. a known record list, a page title, or a specific SAIL component).
 */
public class HomePage {

    private final WebDriver driver;
    private final SitesFixture fixture;

    public HomePage(WebDriver driver, SitesFixture fixture) {
        this.driver = driver;
        this.fixture = fixture;
    }

    /** Waits for Appian's async SAIL rendering to settle, then does a basic sanity check. */
    public boolean isLoaded() {
        fixture.waitForProgressBar();
        return driver.getCurrentUrl().contains("/sites/");
    }
}
