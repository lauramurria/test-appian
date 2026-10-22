package com.testappian.pages;

import com.appiancorp.ps.automatedtest.fixture.SitesFixture;
import org.openqa.selenium.WebDriver;

/**
 * Login is handled through Appian's own SitesFixture#loginIntoWithUsernameAndPassword,
 * rather than hand-rolled Selenium locators against the login form. Appian
 * maintains that helper against its own login page across versions, which is
 * more resilient than pinning to raw element IDs ourselves.
 *
 * If your Appian instance uses SSO instead of native username/password login,
 * replace performLogin() with whatever SSO flow your IdP requires (this is the
 * one part of the framework that is genuinely environment-specific).
 */
public class LoginPage {

    private final WebDriver driver;
    private final SitesFixture fixture;

    public LoginPage(WebDriver driver, SitesFixture fixture) {
        this.driver = driver;
        this.fixture = fixture;
    }

    public HomePage login(String siteUrl, String username, String password) {
        fixture.loginIntoWithUsernameAndPassword(siteUrl, username, password);
        return new HomePage(driver, fixture);
    }
}
