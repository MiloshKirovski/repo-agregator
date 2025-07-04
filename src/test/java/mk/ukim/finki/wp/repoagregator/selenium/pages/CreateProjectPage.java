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
import org.openqa.selenium.Keys;
import org.openqa.selenium.ElementClickInterceptedException;
import java.time.Duration;
import java.util.List;

public class CreateProjectPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "h1.display-4")
    private WebElement pageTitle;

    @FindBy(css = ".intro")
    private WebElement pageSubtitle;

    @FindBy(name = "name")
    private WebElement projectNameField;

    @FindBy(name = "year")
    private WebElement yearField;

    @FindBy(name = "description")
    private WebElement descriptionTextarea;

    @FindBy(name = "courseIds")
    private WebElement coursesSelect;

    @FindBy(name = "mentorIds")
    private WebElement mentorsSelect;

    @FindBy(name = "teamMemberIds")
    private WebElement teamMembersSelect;

    @FindBy(name = "repoLink")
    private WebElement repositoryUrlField;

    @FindBy(css = "button[type='submit'].btn-primary")
    private WebElement createProjectButton;

    @FindBy(css = ".btn.btn-secondary[href*='projects']")
    private WebElement cancelButton;

    @FindBy(css = ".form-text")
    private List<WebElement> helpTexts;
    @FindBy(css = "body > div > div > div > div.project-header > h1")
    private WebElement succefulSubmission;

    @FindBy(css = ".invalid-feedback")
    private List<WebElement> validationMessages;

    public CreateProjectPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public boolean isPageLoaded() {
        return wait.until(ExpectedConditions.visibilityOf(pageTitle))
                .getText().contains("Креирај Нов Проект");
    }

    public void enterProjectName(String projectName) {
        wait.until(ExpectedConditions.visibilityOf(projectNameField));
        projectNameField.clear();
        projectNameField.sendKeys(projectName);
    }

    public void enterYear(String year) {
        wait.until(ExpectedConditions.visibilityOf(yearField));
        yearField.clear();
        yearField.sendKeys(year);
    }

    public void enterDescription(String description) {
        wait.until(ExpectedConditions.visibilityOf(descriptionTextarea));
        descriptionTextarea.clear();
        descriptionTextarea.sendKeys(description);
    }

    public void selectCourse(String course) {
        selectFromSelect2("courseIds", course);
    }

    public void selectMentor(String mentor) {
        selectFromSelect2("mentorIds", mentor);
    }

    public void selectTeamMember(String teamMember) {
        selectFromSelect2("teamMemberIds", teamMember);
    }

    private void selectFromSelect2(String selectId, String optionText) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name(selectId)));

            WebElement select2Container = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//select[@name='" + selectId + "']/following-sibling::span[contains(@class, 'select2-container')]")));

            WebElement select2Selection = select2Container.findElement(By.cssSelector(".select2-selection"));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    select2Selection
            );            Thread.sleep(300);

            wait.until(ExpectedConditions.elementToBeClickable(select2Selection));
            select2Selection.click();

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".select2-dropdown .select2-results")));

            WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".select2-search__field")));
            searchBox.clear();
            searchBox.sendKeys(optionText);

            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[contains(@class, 'select2-results__option') and contains(text(), '" + optionText + "')]")));
            option.click();

            driver.findElement(By.tagName("body")).click();

        } catch (Exception e) {
            System.out.println("Select2 selection failed for " + selectId + " with text '" + optionText + "'");
            e.printStackTrace();
        }
    }


    public void enterRepositoryUrl(String repositoryUrl) {
        wait.until(ExpectedConditions.visibilityOf(repositoryUrlField));
        repositoryUrlField.clear();
        repositoryUrlField.sendKeys(repositoryUrl);
    }

    public void clickCreateProject() {
        try {
            driver.findElement(By.tagName("body")).click();
            Thread.sleep(200);

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", createProjectButton);
            Thread.sleep(300);

            wait.until(ExpectedConditions.elementToBeClickable(createProjectButton));

            createProjectButton.click();

        } catch (ElementClickInterceptedException e) {
            System.out.println("Normal click intercepted, trying JavaScript click");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", createProjectButton);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton));
        cancelButton.click();
    }

    public void createProject(String name, String year, String description,
                              String course, String mentor, String teamMember, String repoUrl) throws InterruptedException {
        enterProjectName(name);
        enterYear(year);
        enterDescription(description);
        selectCourse(course);
        selectMentor(mentor);
        selectTeamMember(teamMember);
        enterRepositoryUrl(repoUrl);
        clickCreateProject();
    }

    public boolean successfulCreation() {
        return wait.until(ExpectedConditions.visibilityOf(succefulSubmission))
                .getText()
                .trim()
                .equals("Студентски Проекти");
    }


}