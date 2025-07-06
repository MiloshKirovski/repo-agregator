package mk.ukim.finki.wp.repoagregator.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

public class ProjectDetailsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;


    @FindBy(css = "body > div > div > a")
    private WebElement backToProjectsButton;

    @FindBy(css = "body > div > div > div.project-header > div > h1")
    private WebElement projectTitle;

    @FindBy(css = "body > div > div > div.project-header > div > div > span")
    private WebElement yearBadge;

    @FindBy(css = "body > div > div > div.project-header > div > div > a")
    private List<WebElement> repositoryLinks;

    public ProjectDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public String isPageLoaded() {
        return wait.until(ExpectedConditions.visibilityOf(projectTitle)).getText();

    }

    public void clickBackToProjects() {
        wait.until(ExpectedConditions.elementToBeClickable(backToProjectsButton));
        backToProjectsButton.click();
    }

    public String getProjectTitle() {
        return wait.until(ExpectedConditions.visibilityOf(projectTitle)).getText();
    }

    public String getProjectYear() {
        return wait.until(ExpectedConditions.visibilityOf(yearBadge)).getText();
    }

    public boolean hasRepositoryLinks() {
        return !repositoryLinks.isEmpty() && repositoryLinks.get(0).isDisplayed();
    }



}
