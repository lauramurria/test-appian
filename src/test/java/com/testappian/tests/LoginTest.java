package com.testappian.tests;

import com.testappian.base.BaseTest;
import com.testappian.config.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(groups = "login", description = "A valid user can log in to the Appian site and reach the landing page")
    public void validUserCanLogIn() {
        homePage = loginPage.login(ConfigReader.siteUrl(), ConfigReader.username(), ConfigReader.password());

        Assert.assertTrue(homePage.isLoaded(), "Expected to land on the Appian site home page after login");
    }
}
