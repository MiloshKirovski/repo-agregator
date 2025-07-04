package mk.ukim.finki.wp.repoagregator.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.List;

public class ProjectPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "h1.display-4")
    private WebElement pageTitle;

    @FindBy(css = "body > div > div > div > div.project-header > a")
    private WebElement addNewProjectButton;

    @FindBy(name = "search")
    private WebElement searchField;
    @FindBy(name = "course")
    private WebElement courseSelect;

    @FindBy(name = "year")
    private WebElement yearField;

    @FindBy(css = "button[type='submit']")
    private WebElement filterButton;

    @FindBy(css = ".project-card")
    private List<WebElement> projectCards;

    @FindBy(css = ".card.mb-4")
    private List<WebElement> projectCardElements;


    @FindBy(css = ".text-center h3")
    private WebElement noProjectsTitle;


    @FindBy(css = ".text-muted")
    private WebElement noProjectsMessage;

    @FindBy(css = ".btn.btn-success[href*='create']")
    private WebElement createNewProjectButton;

    public ProjectPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void clickAddNewProject() {
        wait.until(ExpectedConditions.elementToBeClickable(addNewProjectButton));
        addNewProjectButton.click();
    }

    public void searchProjects(String searchTerm) {
        wait.until(ExpectedConditions.visibilityOf(searchField));
        searchField.clear();
        searchField.sendKeys(searchTerm);
        filterButton.click();
    }
    public void filterByCourse(String courseId) {
        wait.until(ExpectedConditions.elementToBeClickable(courseSelect));

        Select courseDropdown = new Select(courseSelect);
        courseDropdown.selectByValue(courseId);

        filterButton.click();
    }


    public boolean isCourseOptionAvailable(String courseId) {
        Select courseDropdown = new Select(courseSelect);
        return courseDropdown.getOptions().stream()
                .anyMatch(option -> option.getAttribute("value").equals(courseId));
    }
    public String getSelectedCourseValue() {
        Select courseDropdown = new Select(courseSelect);
        return courseDropdown.getFirstSelectedOption().getAttribute("value");
    }

    public void filterByYear(String year) {
        wait.until(ExpectedConditions.visibilityOf(yearField));
        yearField.clear();
        yearField.sendKeys(year);
        filterButton.click();
    }


    public int getProjectCount() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.mb-4")));
            return projectCardElements.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isNoProjectsMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(noProjectsTitle))
                    .getText().contains("Не се пронајдени проекти");
        } catch (Exception e) {
            return false;
        }
    }

    public void clickProjectDetails(int projectIndex) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.mb-4")));
        WebElement projectCard = projectCardElements.get(projectIndex);
        WebElement detailsButton = projectCard.findElement(By.cssSelector(".btn.btn-info"));
        detailsButton.click();
    }

    public String getProjectTitle(int projectIndex) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.mb-4")));
        WebElement projectCard = projectCardElements.get(projectIndex);
        return projectCard.findElement(By.cssSelector(".card-title")).getText();
    }

    public String getProjectYear(int projectIndex) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.mb-4")));
        WebElement projectCard = projectCardElements.get(projectIndex);
        return projectCard.findElement(By.cssSelector(".badge.badge-secondary")).getText();
    }


}
