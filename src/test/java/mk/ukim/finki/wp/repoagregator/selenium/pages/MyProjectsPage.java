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

    @FindBy(css = "body > div > div > div > div.project-header.row > div.col-4.row.mb-4 > div > div > div.stats-number")
    private WebElement projectCountDisplay;

    @FindBy(xpath = "//h3[contains(text(), 'Немате проекти')]")
    private WebElement noProjectsTitle;
    @FindBy(css = ".card.horizontal-card .card-title")
    private List<WebElement> titles;

    @FindBy(css = ".card.horizontal-card")
    List<WebElement> cards;
    @FindBy(css = "body > div > div > div > div.project-header.row > div.col-4.row.mb-4 > div > div > a")
    WebElement editButton;
    @FindBy(css = "body > div > div > div > div.row.g-4 > div:nth-child(1) > div > div > div.col-md-6.ps-0 > div > div:nth-child(2) > a")
    WebElement detailsButton;

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
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.horizontal-card")));


        if (titles.isEmpty()) {
            titles = driver.findElements(By.cssSelector(".card.horizontal-card h5"));
        }

        if (titles.isEmpty()) {
            throw new RuntimeException("No project titles found");
        }

        if (projectIndex >= titles.size()) {
            throw new RuntimeException("Project index " + projectIndex + " out of bounds. Found " + titles.size() + " projects");
        }

        return titles.get(projectIndex).getText();
    }


    public String getCommentOfProjectByTitle(String title) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.horizontal-card")));


        for (int i = 0; i < cards.size(); i++) {
            WebElement card = cards.get(i);
            try {
                WebElement titleElement = card.findElement(By.cssSelector(".card-title"));
                if (titleElement.getText().equals(title)) {
                    WebElement commentTextarea = card.findElement(By.cssSelector("textarea[name='comment']"));
                    wait.until(ExpectedConditions.visibilityOf(commentTextarea));
                    return commentTextarea.getAttribute("value").trim();
                }
            } catch (Exception e) {
                continue;
            }
        }

        throw new RuntimeException("Project with title '" + title + "' not found");
    }

    public String findProjectByTitle(String title) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.horizontal-card")));


        for (WebElement projectTitle : titles) {
            if (projectTitle.getText().equals(title)) {
                return projectTitle.getText();
            }
        }
        return null;
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

    public void clickEditProject() {

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


    public void confirmDeletion() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (TimeoutException e) {
            e.getStackTrace();
        }
    }


    public String changeStatusOfProject(int projectIndex) throws InterruptedException {
        try {
            String currentStatus = getCurrentStatus(projectIndex);
            System.out.println("Current status for project " + projectIndex + ": " + currentStatus);

            String[] availableStatuses = {"APPROVED", "REJECTED", "PENDING"};

            String targetStatus = null;
            for (String status : availableStatuses) {
                if (!status.equals(currentStatus)) {
                    targetStatus = status;
                    break;
                }
            }

            if (targetStatus == null) {
                System.out.println("No alternative status available, returning current status");
                return currentStatus;
            }

            System.out.println("Changing status from " + currentStatus + " to " + targetStatus);

            List<WebElement> projectCards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".card.horizontal-card")
            ));

            if (projectIndex >= projectCards.size()) {
                throw new IndexOutOfBoundsException("Project index " + projectIndex + " is out of bounds. Total projects: " + projectCards.size());
            }

            WebElement targetCard = projectCards.get(projectIndex);

            WebElement statusDropdown = targetCard.findElement(By.cssSelector("select[name='status']"));


            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", statusDropdown);
            Thread.sleep(1000);

            Select statusSelect = new Select(statusDropdown);
            statusSelect.selectByValue(targetStatus);

            WebElement updateStatusButton = null;

            updateStatusButton = targetCard.findElement(By.cssSelector("button.btn-outline-success"));

            if (updateStatusButton == null) {
                throw new RuntimeException("Could not find update button for project at index " + projectIndex);
            }

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", updateStatusButton);
            Thread.sleep(500);

            try {
                updateStatusButton.click();
            } catch (ElementClickInterceptedException e) {
                System.out.println("Regular click failed, using JavaScript click...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", updateStatusButton);
            }

            Thread.sleep(2000);

            return currentStatus;

        } catch (Exception e) {
            System.err.println("Error in changeStatusOfProject: " + e.getMessage());
            throw e;
        }
    }


    public String getCurrentStatus(int projectIndex) {
        try {
            List<WebElement> projectCards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".card.horizontal-card")
            ));

            if (projectIndex >= projectCards.size()) {
                throw new IndexOutOfBoundsException("Project index " + projectIndex + " is out of bounds. Total projects: " + projectCards.size());
            }

            WebElement targetCard = projectCards.get(projectIndex);
            WebElement statusElement = targetCard.findElement(By.cssSelector("select[name='status']"));

            Select statusSelect = new Select(statusElement);
            return statusSelect.getFirstSelectedOption().getAttribute("value");
        } catch (Exception e) {
            System.err.println("Error getting current status for project " + projectIndex + ": " + e.getMessage());
            return "unknown";
        }
    }


    public String getStatusOfProjectByTitle(String title) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.horizontal-card")));

        List<WebElement> cards = driver.findElements(By.cssSelector(".card.horizontal-card"));

        for (int i = 0; i < cards.size(); i++) {
            WebElement card = cards.get(i);
            try {
                WebElement titleElement = card.findElement(By.cssSelector(".card-title"));
                if (titleElement.getText().equals(title)) {
                    WebElement statusDropdown = card.findElement(By.cssSelector("select[name='status']"));
                    Select statusSelect = new Select(statusDropdown);
                    return statusSelect.getFirstSelectedOption().getAttribute("value");
                }
            } catch (Exception e) {
                continue;
            }
        }

        throw new RuntimeException("Project with title '" + title + "' not found");
    }


    public int findProjectIndexByTitle(String title) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.horizontal-card")));

        List<WebElement> titles = driver.findElements(By.cssSelector(".card.horizontal-card .card-title"));

        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i).getText().equals(title)) {
                return i;
            }
        }
        return -1;
    }

    public void clickDeleteProject(int projectIndex) throws InterruptedException {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.horizontal-card")));

        List<WebElement> deleteButtons = driver.findElements(By.cssSelector("a.btn.btn-outline-primary-delete"));

        if (deleteButtons.isEmpty()) {
            throw new RuntimeException("No delete buttons found");
        }

        if (projectIndex >= deleteButtons.size()) {
            throw new RuntimeException("Project index " + projectIndex + " out of bounds. Found " + deleteButtons.size() + " projects");
        }

        WebElement element = deleteButtons.get(projectIndex);


        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        Thread.sleep(1000);

        wait.until(ExpectedConditions.elementToBeClickable(element));

        element.click();
    }


    public void updateProjectComment(int projectIndex, String comment) throws InterruptedException {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card.horizontal-card")));

        List<WebElement> cards = driver.findElements(By.cssSelector(".card.horizontal-card"));

        if (projectIndex >= cards.size()) {
            throw new RuntimeException("Project index " + projectIndex + " out of bounds. Found " + cards.size() + " projects");
        }

        WebElement card = cards.get(projectIndex);

        WebElement commentField = card.findElement(By.name("comment"));
        WebElement updateButton = card.findElement(By.cssSelector("button.btn-outline-success"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", commentField);
        Thread.sleep(500);

        wait.until(ExpectedConditions.visibilityOf(commentField));
        commentField.clear();
        commentField.sendKeys(comment);

        Thread.sleep(500);

        System.out.println("Attempting to click update comment button for project " + projectIndex);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", updateButton);
        Thread.sleep(1000);

        wait.until(ExpectedConditions.elementToBeClickable(updateButton));

        updateButton.click();

        Thread.sleep(1000);
    }


}
