package mk.ukim.finki.wp.repoagregator.selenium.tests;

import mk.ukim.finki.wp.repoagregator.selenium.pages.LoginPage;
import mk.ukim.finki.wp.repoagregator.selenium.pages.MyProjectsPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseSeleniumTest{
    private LoginPage loginPage;
    private MyProjectsPage myProjectsPage;

    @BeforeEach
    void setUpPages() {
        loginPage = new LoginPage(driver);

    }
    @Test
    void testLogin() {
        driver.get(getUrl("/login"));
        assertThat(loginPage.isPageLoaded()).isTrue();
    loginPage.login("riste.stojanov", "SystemPass");

      assertThat(loginPage.successfulLogin()).isTrue();


    }
}
