package mk.ukim.finki.wp.repoagregator.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import java.time.Duration;
import java.util.List;

public class CreateProjectPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "h1.display-4")
    private WebElement pageTitle;

    @FindBy(css = ".lead")
    private WebElement pageSubtitle;

    @FindBy(name = "name")
    private WebElement projectNameField;

    @FindBy(name = "year")
    private WebElement yearField;

    @FindBy(name = "description")
    private WebElement descriptionTextarea;

    @FindBy(name = "courses")
    private WebElement coursesSelect;

    @FindBy(name = "mentors")
    private WebElement mentorsSelect;

    @FindBy(name = "teamMembers")
    private WebElement teamMembersSelect;

    @FindBy(name = "repositoryUrl")
    private WebElement repositoryUrlField;

    @FindBy(css = "button[type='submit'].btn-success")
    private WebElement createProjectButton;

    @FindBy(css = ".btn.btn-secondary[href*='projects']")
    private WebElement cancelButton;

    @FindBy(css = ".form-text.text-muted")
    private List<WebElement> helpTexts;

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
        wait.until(ExpectedConditions.visibilityOf(coursesSelect));
        Select select = new Select(coursesSelect);
        select.selectByVisibleText(course);
    }

    public void selectMentor(String mentor) {
        wait.until(ExpectedConditions.visibilityOf(mentorsSelect));
        Select select = new Select(mentorsSelect);
        select.selectByVisibleText(mentor);
    }

    public void selectTeamMember(String teamMember) {
        wait.until(ExpectedConditions.visibilityOf(teamMembersSelect));
        Select select = new Select(teamMembersSelect);
        select.selectByVisibleText(teamMember);
    }

    public void enterRepositoryUrl(String repositoryUrl) {
        wait.until(ExpectedConditions.visibilityOf(repositoryUrlField));
        repositoryUrlField.clear();
        repositoryUrlField.sendKeys(repositoryUrl);
    }

    public void clickCreateProject() {
        wait.until(ExpectedConditions.elementToBeClickable(createProjectButton));
        createProjectButton.click();
    }

    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton));
        cancelButton.click();
    }

    public void createProject(String name, String year, String description,
                              String course, String mentor, String teamMember, String repoUrl) {
        enterProjectName(name);
        enterYear(year);
        enterDescription(description);
        selectCourse(course);
        selectMentor(mentor);
        selectTeamMember(teamMember);
        enterRepositoryUrl(repoUrl);
        clickCreateProject();
    }

    public boolean hasValidationErrors() {
        return !validationMessages.isEmpty() &&
                validationMessages.stream().anyMatch(WebElement::isDisplayed);
    }

    public String getValidationMessage(int index) {
        if (index < validationMessages.size() && validationMessages.get(index).isDisplayed()) {
            return validationMessages.get(index).getText();
        }
        return "";
    }
}
