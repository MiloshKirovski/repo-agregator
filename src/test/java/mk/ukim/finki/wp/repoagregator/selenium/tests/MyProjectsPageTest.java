package mk.ukim.finki.wp.repoagregator.selenium.tests;

import mk.ukim.finki.wp.repoagregator.selenium.pages.EditProjectPage;
import mk.ukim.finki.wp.repoagregator.selenium.pages.MyProjectsPage;
import mk.ukim.finki.wp.repoagregator.selenium.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MyProjectsPageTest extends BaseSeleniumTest {

    private MyProjectsPage myProjectsPage;
    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(driver);
        myProjectsPage = new MyProjectsPage(driver);
        driver.get(getUrl("/login"));
        loginPage.login("riste.stojanov", "SystemPass");
        driver.get(getUrl("/user/riste.stojanov"));
    }

    @Test
    void testPageLoads() {
        assertThat(myProjectsPage.isPageLoaded()).isTrue();
    }

    @Test
    void testNoProjectsMessage() {
        if (myProjectsPage.hasNoProjects()) {
            assertThat(myProjectsPage.hasNoProjects()).isTrue();
            assertThat(myProjectsPage.getProjectCount()).isEqualTo(0);
        }
    }

    @Test
    void testProjectDetailsNavigation() {

            myProjectsPage.clickProjectDetails();
        assertThat(driver.getCurrentUrl()).matches(".*/projects/\\d+(?:\\?.*)?");
    }

    @Test
    void testEditProjectNavigation() {
        if (myProjectsPage.getProjectCount() > 0 ) {
            myProjectsPage.clickEditProject();
            assertThat(driver.getCurrentUrl()).matches(".*/projects/edit/\\d+");        }
    }

    @Test
    void testEditProjectWithInvalidName() throws InterruptedException {
        if (myProjectsPage.getProjectCount() > 0) {
            myProjectsPage.clickEditProject();
            String currentUrl = driver.getCurrentUrl();

            EditProjectPage editPage = new EditProjectPage(driver);
            editPage.updateProjectName("");

            String newDesc = "Invalid name test " + System.currentTimeMillis();
            editPage.updateDescription(newDesc);

            String newRepo = "https://github.com/test/invalid-name-check";
            editPage.updateRepoLink(newRepo);

            editPage.submit();

            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.urlToBe(currentUrl));

            assertThat(driver.getCurrentUrl()).isEqualTo(currentUrl);
        }
    }
    @Test
    void testCancelEditProjectNavigatesToProjects() throws InterruptedException {
        if (myProjectsPage.getProjectCount() > 0) {
            myProjectsPage.clickEditProject();

            EditProjectPage editPage = new EditProjectPage(driver);
            editPage.cancelEdit();

            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.urlContains("/projects"));

            assertThat(driver.getCurrentUrl()).contains("/projects");
        }
    }




    @Test
    void testDeleteProject() throws InterruptedException {
            int initialUiCount = myProjectsPage.getProjectCount();

            myProjectsPage.clickDeleteProject(0);

            int updatedUiCount = myProjectsPage.getProjectCount();
            assertThat(updatedUiCount).isEqualTo(initialUiCount - 1);



    }

    @Test
    void testEditAndSaveProject() throws InterruptedException {
        if (myProjectsPage.getProjectCount() > 0) {
            String originalTitle = myProjectsPage.getProjectTitle(0);
            System.out.println("Original title: " + originalTitle);

            myProjectsPage.clickEditProject();

            EditProjectPage editPage = new EditProjectPage(driver);

            String newName = "Updated Project " + System.currentTimeMillis();
            editPage.updateProjectName(newName);

            String newDesc = "Updated description at " + System.currentTimeMillis();
            editPage.updateDescription(newDesc);

            String newRepo = "https://github.com/test/project-" + System.currentTimeMillis();
            editPage.updateRepoLink(newRepo);

            editPage.submit();

            Thread.sleep(2000);

            String foundTitle = myProjectsPage.findProjectByTitle(newName);
            assertThat(foundTitle).isNotNull();
            assertThat(foundTitle).isEqualTo(newName);
        }
    }

    @Test
    void testProjectStatusUpdate() throws InterruptedException {

        if (myProjectsPage.getProjectCount() > 0) {
            String originalTitle = myProjectsPage.getProjectTitle(0);
            System.out.println("Testing project: " + originalTitle);

            String initialStatus = myProjectsPage.getStatusOfProjectByTitle(originalTitle);
            System.out.println("Initial status: " + initialStatus);

            String previousStatus = myProjectsPage.changeStatusOfProject(0);
            System.out.println("Previous status was: " + previousStatus);

            Thread.sleep(3000);

            driver.navigate().refresh();
            Thread.sleep(2000);

            String updatedStatus = myProjectsPage.getStatusOfProjectByTitle(originalTitle);
            System.out.println("Updated status: " + updatedStatus);

            assertThat(updatedStatus).isNotEqualTo(previousStatus);
        }
    }

    @ParameterizedTest
    @MethodSource("commentTestData")
    void testProjectCommentUpdate(String testData) throws InterruptedException {
        if (myProjectsPage.getProjectCount() > 0) {
            String originalTitle = myProjectsPage.getProjectTitle(0);
            System.out.println("Original title: " + originalTitle);


            String timestampedComment = testData;
            myProjectsPage.updateProjectComment(0, timestampedComment);

            String actual = myProjectsPage.getCommentOfProjectByTitle(originalTitle);
            if(Objects.equals(testData, " ")) {
                assertThat(actual).isEqualTo("");

            }else {
                assertThat(actual).isEqualTo(timestampedComment);
            }
        }
    }

    static Stream<String> commentTestData() {
        return Stream.of(
                new String("Simple comment "+ System.currentTimeMillis()),
                new String("This is a very long comment that tests the system's ability to handle extended text input and storage"+System.currentTimeMillis()),
                new String(" " )
        );
    }


    @Test
    void testProjectCountDisplay() {
        int displayedCount = myProjectsPage.getProjectCount();
        if (displayedCount > 0) {
            assertThat(myProjectsPage.getProjectCount()).isEqualTo(displayedCount);
        }
    }





}