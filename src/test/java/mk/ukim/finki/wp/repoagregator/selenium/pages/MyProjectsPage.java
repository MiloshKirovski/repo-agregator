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

public class MyProjectsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "h1.display-4")
    private WebElement pageTitle;

    @FindBy(css = ".btn.btn-outline-primary[href*='projects']")
    private WebElement allProjectsButton;

    @FindBy(css = ".display-1")
    private WebElement projectCountDisplay;

    @FindBy(css = ".lead")
    private WebElement projectCountLabel;

    @FindBy(xpath = "//h3[contains(text(), 'Немате проекти')]")
    private WebElement noProjectsTitle;

    @FindBy(xpath = "//p[contains(text(), 'Сè уште немате креирано')]")
    private WebElement noCreatedProjectsMessage;

    @FindBy(xpath = "//p[contains(text(), 'Сè уште не сте ментор')]")
    private WebElement noMentorProjectsMessage;

    @FindBy(css = ".btn.btn-success[href*='create']")
    private WebElement createNewProjectButton;

    @FindBy(css = ".card.mb-4")
    private List<WebElement> projectCards;

    @FindBy(css = ".card-title")
    private List<WebElement> projectTitles;

    @FindBy(css = ".badge.badge-secondary")
    private List<WebElement> yearBadges;

    @FindBy(css = ".btn.btn-info")
    private List<WebElement> detailsButtons;

    @FindBy(css = ".btn.btn-warning")
    private List<WebElement> editButtons;

    @FindBy(css = ".btn.btn-danger")
    private List<WebElement> deleteButtons;

    @FindBy(css = "select[name='status']")
    private List<WebElement> statusDropdowns;

    @FindBy(css = ".btn.btn-primary[onclick*='updateStatus']")
    private List<WebElement> updateStatusButtons;

    @FindBy(css = "textarea[name='comment']")
    private List<WebElement> commentTextareas;

    @FindBy(css = ".btn.btn-secondary[onclick*='updateComment']")
    private List<WebElement> updateCommentButtons;

    @FindBy(css = ".badge.badge-warning")
    private List<WebElement> roleBadges;

    public MyProjectsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public boolean isPageLoaded() {
        return wait.until(ExpectedConditions.visibilityOf(pageTitle))
                .getText().contains("Мои Проекти");
    }

    public void clickAllProjects() {
        wait.until(ExpectedConditions.elementToBeClickable(allProjectsButton));
        allProjectsButton.click();
    }

    public int getProjectCount() {
        try {
            String countText = wait.until(ExpectedConditions.visibilityOf(projectCountDisplay)).getText();
            return Integer.parseInt(countText);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean hasNoProjects() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(noProjectsTitle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickCreateNewProject() {
        wait.until(ExpectedConditions.elementToBeClickable(createNewProjectButton));
        createNewProjectButton.click();
    }

    public int getDisplayedProjectsCount() {
        return projectCards.size();
    }

    public String getProjectTitle(int projectIndex) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.mb-4")));
        return projectTitles.get(projectIndex).getText();
    }

    public String getProjectYear(int projectIndex) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.mb-4")));
        return yearBadges.get(projectIndex).getText();
    }

    public void clickProjectDetails(int projectIndex) {
        wait.until(ExpectedConditions.elementToBeClickable(detailsButtons.get(projectIndex)));
        detailsButtons.get(projectIndex).click();
    }

    public void clickEditProject(int projectIndex) {
        wait.until(ExpectedConditions.elementToBeClickable(editButtons.get(projectIndex)));
        editButtons.get(projectIndex).click();
    }

    public void clickDeleteProject(int projectIndex) {
        wait.until(ExpectedConditions.elementToBeClickable(deleteButtons.get(projectIndex)));
        deleteButtons.get(projectIndex).click();
    }

    public void updateProjectStatus(int projectIndex, String status) {
        wait.until(ExpectedConditions.visibilityOf(statusDropdowns.get(projectIndex)));
        Select statusSelect = new Select(statusDropdowns.get(projectIndex));
        statusSelect.selectByValue(status);

        wait.until(ExpectedConditions.elementToBeClickable(updateStatusButtons.get(projectIndex)));
        updateStatusButtons.get(projectIndex).click();
    }

    public void updateProjectComment(int projectIndex, String comment) {
        wait.until(ExpectedConditions.visibilityOf(commentTextareas.get(projectIndex)));
        commentTextareas.get(projectIndex).clear();
        commentTextareas.get(projectIndex).sendKeys(comment);

        wait.until(ExpectedConditions.elementToBeClickable(updateCommentButtons.get(projectIndex)));
        updateCommentButtons.get(projectIndex).click();
    }

    public String getUserRole(int projectIndex) {
        try {
            return roleBadges.get(projectIndex).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isUserMentor(int projectIndex) {
        return getUserRole(projectIndex).equals("Ментор");
    }

    public boolean canEditProject(int projectIndex) {
        return editButtons.size() > projectIndex && editButtons.get(projectIndex).isDisplayed();
    }

    public boolean canDeleteProject(int projectIndex) {
        return deleteButtons.size() > projectIndex && deleteButtons.get(projectIndex).isDisplayed();
    }
}
