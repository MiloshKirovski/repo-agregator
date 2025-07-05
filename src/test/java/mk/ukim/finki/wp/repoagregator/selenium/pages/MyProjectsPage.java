package mk.ukim.finki.wp.repoagregator.selenium.pages;

import org.openqa.selenium.*;
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

    @FindBy(css = "body > div > div > div > div.project-header.row > div.col-4.row.mb-4 > div > div > div.stats-number")
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

    @FindBy(css = "body > div > div > div > div.row.g-4 > div:nth-child(1) > div > div > div.col-md-6.ps-0 > div > div:nth-child(2) > a")
    private WebElement detailsButtons;

    @FindBy(css = ".btn.btn-warning")
    private List<WebElement> editButtons;

    @FindBy(css = "a.btn.btn-outline-primary-delete")
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




    public String getProjectTitle(int projectIndex) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.mb-4")));
        return projectTitles.get(projectIndex).getText();
    }

    public String getProjectYear(int projectIndex) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.mb-4")));
        return yearBadges.get(projectIndex).getText();
    }


    public void clickProjectDetails() {
        String selector = "body > div > div > div > div.row.g-4 > div:nth-child(1) > div > div > div.col-md-6.ps-0 > div > div:nth-child(2) > a";

        WebElement detailsButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", detailsButton);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        wait.until(ExpectedConditions.elementToBeClickable(detailsButton));
        detailsButton.click();
    }

    public void clickEditProject(int projectIndex) {

        WebElement editButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("body > div > div > div > div.row.g-4 > div:nth-child(1) > div > div > div.col-md-6.ps-0 > div > div:nth-child(2) > div:nth-child(2) > a.btn.btn-outline-primary.mt-1")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", editButton);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        wait.until(ExpectedConditions.elementToBeClickable(editButton));
        editButton.click();
    }

    public String changeStatusOfFirstProject() throws InterruptedException {
        WebElement statusDropdown = driver.findElement(By.id("statusSelect"));
        Select statusSelect = new Select(statusDropdown);

        String initialValue = statusSelect.getFirstSelectedOption().getAttribute("value");

        WebElement secondOption = driver.findElement(By.cssSelector("#statusSelect > option:nth-child(2)"));
        String optionValue = secondOption.getAttribute("value");

        statusSelect.selectByValue(optionValue);
        Thread.sleep(1000);

        WebElement updateButton = driver.findElement(By.cssSelector("button.btn-outline-success"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", updateButton);
        updateButton.click();

        Thread.sleep(2000);

        return initialValue;
    }

    public String getStatusOfLastProject() {
        List<WebElement> dropdowns = driver.findElements(By.cssSelector("select[name='status']"));
        if (dropdowns.isEmpty()) {
            throw new RuntimeException("No status dropdowns found");
        }

        Select statusSelect = new Select(dropdowns.get(dropdowns.size() - 1));
        return statusSelect.getFirstSelectedOption().getAttribute("value");
    }



    public void clickDeleteProject(int projectIndex) {

        WebElement deleteButtons = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("body > div > div > div > div.row.g-4 > div:nth-child(1) > div > div > div.col-md-6.ps-0 > div > div:nth-child(2) > div:nth-child(2) > a.btn.btn-outline-primary-delete.mt-1")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", deleteButtons);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        wait.until(ExpectedConditions.elementToBeClickable(deleteButtons));
        deleteButtons.click();
    }
    public void confirmDeletion() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (TimeoutException e) {
            e.getStackTrace();
        }
    }


    public String updateFirstProjectComment(String comment) throws InterruptedException {
        List<WebElement> projectCards = driver.findElements(By.cssSelector(".card.horizontal-card"));

        if (projectCards.isEmpty()) {
            throw new RuntimeException("No project cards found");
        }

        WebElement firstCard = projectCards.get(0);

        WebElement commentField = firstCard.findElement(By.name("comment"));

        WebElement updateButton = firstCard.findElement(By.cssSelector("button.btn-outline-success"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", commentField);
        wait.until(ExpectedConditions.visibilityOf(commentField));
        Thread.sleep(1000);

        commentField.clear();
        commentField.sendKeys(comment);

        wait.until(ExpectedConditions.elementToBeClickable(updateButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", updateButton);
        updateButton.click();

        Thread.sleep(2000);

        return comment;
    }
    public String getLastProjectTitle() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card-title")));
        return projectTitles.get(projectTitles.size() - 1).getText();
    }

    public String getCommentOfLastProject() {
        List<WebElement> textareas = driver.findElements(By.name("comment"));
        if (textareas.isEmpty()) {
            throw new RuntimeException("No comment fields found");
        }

        WebElement last = textareas.get(textareas.size() - 1);
        wait.until(ExpectedConditions.visibilityOf(last));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", last);
        return last.getAttribute("value").trim();
    }






}
