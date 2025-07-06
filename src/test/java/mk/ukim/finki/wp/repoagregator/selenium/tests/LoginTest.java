package mk.ukim.finki.wp.repoagregator.selenium.tests;

import mk.ukim.finki.wp.repoagregator.selenium.pages.LoginPage;
import mk.ukim.finki.wp.repoagregator.selenium.pages.MyProjectsPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseSeleniumTest{
    private LoginPage loginPage;

    @BeforeEach
    void setUpPages() {
        loginPage = new LoginPage(driver);

    }
    @ParameterizedTest
    @ValueSource(strings = {"221071", "riste.stojanov"})
    void testLogin(String searchTerm) {
        driver.get(getUrl("/login"));
        assertThat(loginPage.isPageLoaded()).isTrue();
    loginPage.login(searchTerm, "SystemPass");

      assertThat(loginPage.successfulLogin()).isTrue();


    }
    @Test
    void testInvalidLogin() {
        driver.get(getUrl("/login"));
        assertThat(loginPage.isPageLoaded()).isTrue();

        loginPage.login("wrong.user", "wrongpass");

        assertThat(loginPage.isErrorAlertDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Неточно корисничко име или лозинка");
    }


    void testLogoutSuccess() {
        driver.get(getUrl("/login"));
        assertThat(loginPage.isPageLoaded()).isTrue();

        loginPage.login("riste.stojanov", "SystemPass");
        assertThat(loginPage.successfulLogin()).isTrue();

        WebElement logoutBtn = driver.findElement(By.cssSelector("#offcanvasNavbar > div.offcanvas-body > ul > li:nth-child(4) > a"));
        logoutBtn.click();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/login"));

        WebElement successAlert = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.alert.alert-success")));

        assertThat(successAlert.getText()).contains("Успешно се одјавивте");
    }

}
