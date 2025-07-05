package mk.ukim.finki.wp.repoagregator.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

import static java.lang.Thread.sleep;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;


    @FindBy(name = "username")
    private WebElement usernameField;

    @FindBy(name = "password")
    private WebElement passwordField;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(css = ".alert.alert-error")
    private WebElement errorAlert;

    @FindBy(css = ".login-header")
    private WebElement pageTitle;

    @FindBy(css = ".card-subtitle")
    private WebElement pageSubtitle;

    @FindBy(css = ".logo img")
    private WebElement finkiLogo;

    @FindBy(css = "body > div > div > div > div.project-header > h1")
    private WebElement successlogin;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameField));
        usernameField.clear();
        usernameField.sendKeys(username);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordField));
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
    }

    public void login(String username, String password) {

        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isErrorAlertDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(errorAlert)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOf(errorAlert)).getText();
    }

    public String getPageTitle() {
        return wait.until(ExpectedConditions.visibilityOf(pageTitle)).getText();
    }

    public boolean isPageLoaded() {
        return wait.until(ExpectedConditions.visibilityOf(pageTitle)).getText().contains("FINKI Repository");
    }
    public boolean successfulLogin() {
        return wait.until(ExpectedConditions.visibilityOf(successlogin)).getText().contains("Студентски Проекти");
    }
}
