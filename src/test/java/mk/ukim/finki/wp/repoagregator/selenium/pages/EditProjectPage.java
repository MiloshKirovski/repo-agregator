package mk.ukim.finki.wp.repoagregator.selenium.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EditProjectPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public EditProjectPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "name")
    private WebElement nameInput;

    @FindBy(id = "year")
    private WebElement yearInput;

    @FindBy(id = "description")
    private WebElement descriptionTextarea;

    @FindBy(id = "repoLink")
    private WebElement repoLinkInput;

    @FindBy(css = "body > div > div > div > div.form-container > form > div.form-actions > button > span")
    private WebElement submitButton;

    public void updateProjectName(String newName) {
        wait.until(ExpectedConditions.visibilityOf(nameInput));
        nameInput.clear();
        nameInput.sendKeys(newName);
    }

    public void updateProjectYear(String newYear) {
        wait.until(ExpectedConditions.visibilityOf(yearInput));
        yearInput.clear();
        yearInput.sendKeys(newYear);
    }

    public void updateDescription(String newDescription) {
        wait.until(ExpectedConditions.visibilityOf(descriptionTextarea));
        descriptionTextarea.clear();
        descriptionTextarea.sendKeys(newDescription);
    }

    public void updateRepoLink(String newLink) {
        wait.until(ExpectedConditions.visibilityOf(repoLinkInput));
        repoLinkInput.clear();
        repoLinkInput.sendKeys(newLink);
    }

    public void submit() throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", submitButton);
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
    }
}
