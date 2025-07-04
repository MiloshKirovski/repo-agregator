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


    @FindBy(css = ".btn.btn-secondary[href*='projects']")
    private WebElement backToProjectsButton;

    @FindBy(css = "h1.display-4")
    private WebElement projectTitle;

    @FindBy(css = ".badge.badge-secondary")
    private WebElement yearBadge;

    @FindBy(css = ".btn.btn-outline-primary[href*='github']")
    private List<WebElement> repositoryLinks;

    @FindBy(css = "h3")
    private List<WebElement> sectionHeaders;

    @FindBy(xpath = "//h3[contains(text(), 'Опис на проект')]/following-sibling::*")
    private WebElement projectDescription;

    @FindBy(xpath = "//p[contains(text(), 'Нема достапен опис')]")
    private WebElement noDescriptionMessage;

    @FindBy(css = ".badge.badge-info")
    private List<WebElement> teamMemberBadges;

    @FindBy(xpath = "//p[contains(text(), 'Нема достапни членови')]")
    private WebElement noTeamMembersMessage;

    @FindBy(css = ".badge.badge-success")
    private List<WebElement> mentorBadges;

    @FindBy(xpath = "//p[contains(text(), 'Нема достапни ментори')]")
    private WebElement noMentorsMessage;

    @FindBy(css = ".badge.badge-primary")
    private List<WebElement> courseBadges;

    @FindBy(xpath = "//p[contains(text(), 'Нема достапни предмети')]")
    private WebElement noCoursesMessage;

    @FindBy(xpath = "//h3[contains(text(), 'Коментар')]/following-sibling::*")
    private WebElement projectComment;

    @FindBy(xpath = "//p[contains(text(), 'Нема коментар')]")
    private WebElement noCommentMessage;

    @FindBy(css = ".card-header h5")
    private WebElement readmeHeader;

    @FindBy(css = ".card-body")
    private WebElement readmeContent;

    @FindBy(xpath = "//h5[contains(text(), 'README не е достапен')]")
    private WebElement noReadmeMessage;

    public ProjectDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public boolean isPageLoaded() {
        return wait.until(ExpectedConditions.visibilityOf(projectTitle)).isDisplayed();
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

    public void clickRepositoryLink(int index) {
        if (index < repositoryLinks.size()) {
            wait.until(ExpectedConditions.elementToBeClickable(repositoryLinks.get(index)));
            repositoryLinks.get(index).click();
        }
    }

    public String getProjectDescription() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(projectDescription)).getText();
        } catch (Exception e) {
            return noDescriptionMessage.isDisplayed() ? noDescriptionMessage.getText() : "";
        }
    }

    public boolean hasDescription() {
        try {
            return projectDescription.isDisplayed() && !projectDescription.getText().trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getTeamMembers() {
        return teamMemberBadges.stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .toList();
    }

    public boolean hasTeamMembers() {
        return !teamMemberBadges.isEmpty() && teamMemberBadges.get(0).isDisplayed();
    }

    public List<String> getMentors() {
        return mentorBadges.stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .toList();
    }

    public boolean hasMentors() {
        return !mentorBadges.isEmpty() && mentorBadges.get(0).isDisplayed();
    }

    public List<String> getCourses() {
        return courseBadges.stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .toList();
    }

    public boolean hasCourses() {
        return !courseBadges.isEmpty() && courseBadges.get(0).isDisplayed();
    }

    public String getComment() {
        try {
            return projectComment.isDisplayed() ? projectComment.getText() : "";
        } catch (Exception e) {
            return "";
        }
    }

    public boolean hasComment() {
        try {
            return projectComment.isDisplayed() && !projectComment.getText().trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasReadme() {
        try {
            return readmeContent.isDisplayed() && !noReadmeMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getReadmeContent() {
        try {
            return readmeContent.isDisplayed() ? readmeContent.getText() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
