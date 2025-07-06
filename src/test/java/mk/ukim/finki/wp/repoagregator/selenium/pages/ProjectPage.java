package mk.ukim.finki.wp.repoagregator.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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


    @FindBy(css = ".card.mb-4")
    private List<WebElement> projectCardElements;

    @FindBy(css = ".card.horizontal-card")
    private     List<WebElement> projectCards;



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
    public void filterByCourse(String courseId) throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(courseSelect));

        Select courseDropdown = new Select(courseSelect);
        courseDropdown.selectByValue(courseId);

        filterButton.click();
        Thread.sleep(1000);
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


    public void clickProjectDetails(int projectIndex) throws InterruptedException {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.horizontal-card")));

        if (projectIndex >= projectCards.size()) {
            throw new IndexOutOfBoundsException("Invalid project index: " + projectIndex);
        }

        WebElement projectCard = projectCards.get(projectIndex);
        WebElement detailsBtn = projectCard.findElement(By.cssSelector("a.btn.btn-outline-primary"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", detailsBtn);
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(detailsBtn));
        detailsBtn.click();
    }



}
