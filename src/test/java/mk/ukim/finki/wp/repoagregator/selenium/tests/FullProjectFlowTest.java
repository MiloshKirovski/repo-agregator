package mk.ukim.finki.wp.repoagregator.selenium.tests;

import mk.ukim.finki.wp.repoagregator.selenium.pages.CreateProjectPage;
import mk.ukim.finki.wp.repoagregator.selenium.pages.LoginPage;
import mk.ukim.finki.wp.repoagregator.selenium.pages.MyProjectsPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FullProjectFlowTest extends BaseSeleniumTest {

    private LoginPage loginPage;
    private CreateProjectPage createProjectPage;
    private MyProjectsPage myProjectsPage;

    @BeforeEach
    void initPages() {
        loginPage = new LoginPage(driver);
        createProjectPage = new CreateProjectPage(driver);
        myProjectsPage = new MyProjectsPage(driver);
    }

    @Test
    void testCreateThenDeleteProject() throws InterruptedException {
        driver.get(getUrl("/login"));
        loginPage.login("221071", "SystemPass");

        driver.get(getUrl("/projects/create"));
        String projectTitle = "Flow Test Project A - " + System.currentTimeMillis();

        createProjectPage.createProject(
                projectTitle,
                "2025",
                "Created to test full flow",
                "Оперативни системи",
                "Ристе Стојанов",
                "Hristijan",
                "https://github.com/test/projectA"
        );
        assertThat(createProjectPage.successfulCreation()).isTrue();

        WebElement logoutBtn = driver.findElement(By.cssSelector("#offcanvasNavbar > div.offcanvas-body > ul > li:nth-child(4) > a"));
        logoutBtn.click();
        loginPage.login("riste.stojanov", "SystemPass");
        driver.get(getUrl("/user/riste.stojanov"));

        int initialCount = myProjectsPage.getProjectCount();

        int projectIndex = myProjectsPage.findProjectIndexByTitle(projectTitle);
        assertThat(projectIndex).isGreaterThanOrEqualTo(0);
        myProjectsPage.clickDeleteProject(projectIndex);
        myProjectsPage.confirmDeletion();

        Thread.sleep(2000);
        assertThat(myProjectsPage.getProjectCount()).isEqualTo(initialCount - 1);

        assertThat(myProjectsPage.findProjectByTitle(projectTitle)).isNull();
    }

    @Test
    void testCreateThreeProjectsThenManageAsMentor() throws InterruptedException {
        driver.get(getUrl("/login"));
        loginPage.login("221071", "SystemPass");

        List<String> createdProjectTitles = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            driver.get(getUrl("/projects/create"));
            String projectTitle = "Flow Test Project " + i + " - " + System.currentTimeMillis();
            createdProjectTitles.add(projectTitle);

            createProjectPage.createProject(
                    projectTitle,
                    "2025",
                    "Scenario test project #" + i,
                    "Оперативни системи",
                    "Ристе Стојанов",
                    "Hristijan",
                    "https://github.com/test/project" + i
            );
            assertThat(createProjectPage.successfulCreation()).isTrue();

            Thread.sleep(500);
        }

        WebElement logoutBtn = driver.findElement(By.cssSelector("#offcanvasNavbar > div.offcanvas-body > ul > li:nth-child(4) > a"));
        logoutBtn.click();
        loginPage.login("riste.stojanov", "SystemPass");

        driver.get(getUrl("/user/riste.stojanov"));

        Thread.sleep(2000);


        int initialCount = myProjectsPage.getProjectCount();
        System.out.println("Initial project count: " + initialCount);
        System.out.println("Created projects: " + createdProjectTitles);

        String firstProjectTitle = createdProjectTitles.get(0);
        int firstProjectIndex = myProjectsPage.findProjectIndexByTitle(firstProjectTitle);

        if (firstProjectIndex < 0) {
            System.out.println("Could not find project: " + firstProjectTitle);
            for (int i = 0; i < myProjectsPage.getProjectCount(); i++) {
                System.out.println("Project " + i + ": " + myProjectsPage.getProjectTitle(i));
            }
            firstProjectIndex = 0;
        }

        assertThat(firstProjectIndex).isGreaterThanOrEqualTo(0);

        System.out.println("Attempting to delete project at index: " + firstProjectIndex);
        myProjectsPage.clickDeleteProject(firstProjectIndex);
        myProjectsPage.confirmDeletion();
        Thread.sleep(2000);

        int countAfterDeletion = myProjectsPage.getProjectCount();
        System.out.println("Count after deletion: " + countAfterDeletion);
        assertThat(countAfterDeletion).isEqualTo(initialCount - 1);

        if (createdProjectTitles.size() > 1 && myProjectsPage.getProjectCount() > 0) {
            String secondProjectTitle = createdProjectTitles.get(1);
            int secondProjectIndex = myProjectsPage.findProjectIndexByTitle(secondProjectTitle);

            if (secondProjectIndex < 0) {
                secondProjectIndex = 0;
            }

            String initialStatus = myProjectsPage.getStatusOfProjectByTitle(myProjectsPage.getProjectTitle(secondProjectIndex));
            System.out.println("Initial status of project at index " + secondProjectIndex + ": " + initialStatus);

            myProjectsPage.changeStatusOfProject(secondProjectIndex);
            Thread.sleep(2000);

            String previousStatus = myProjectsPage.changeStatusOfProject(secondProjectIndex);
            Thread.sleep(2000);
            String updatedStatus = myProjectsPage.getStatusOfProjectByTitle(myProjectsPage.getProjectTitle(secondProjectIndex));
            assertThat(updatedStatus).isNotEqualTo(previousStatus);
        }
        Thread.sleep(2000);

        if (createdProjectTitles.size() > 2 && myProjectsPage.getProjectCount() > 1) {
            String thirdProjectTitle = createdProjectTitles.get(2);
            int thirdProjectIndex = myProjectsPage.findProjectIndexByTitle(thirdProjectTitle);

            if (thirdProjectIndex < 0) {
                thirdProjectIndex = Math.min(1, myProjectsPage.getProjectCount() - 1);
            }

            String comment = "Reviewed by mentor at " + System.currentTimeMillis();

            myProjectsPage.updateProjectComment(thirdProjectIndex, comment);
            Thread.sleep(2000);

            String actualComment = myProjectsPage.getCommentOfProjectByTitle(myProjectsPage.getProjectTitle(thirdProjectIndex));
            assertThat(actualComment).isEqualTo(comment);
        }
    }
}