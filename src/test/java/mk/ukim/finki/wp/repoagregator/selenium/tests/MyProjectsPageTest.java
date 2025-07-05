package mk.ukim.finki.wp.repoagregator.selenium.tests;

import mk.ukim.finki.wp.repoagregator.selenium.pages.MyProjectsPage;
import mk.ukim.finki.wp.repoagregator.selenium.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


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
            myProjectsPage.clickEditProject(0);
            assertThat(driver.getCurrentUrl()).matches(".*/projects/edit/\\d+");        }
    }

    @Test
    void testDeleteProject() {
            int initialUiCount = myProjectsPage.getProjectCount();

            myProjectsPage.clickDeleteProject(0);

            int updatedUiCount = myProjectsPage.getProjectCount();
            assertThat(updatedUiCount).isEqualTo(initialUiCount - 1);



    }
    @Test
    void testProjectStatusUpdate() throws InterruptedException {
        if (myProjectsPage.getProjectCount() > 0) {
            String initialStatus = myProjectsPage.changeStatusOfFirstProject();
            String updatedStatus = myProjectsPage.getStatusOfLastProject();

            assertThat(updatedStatus).isNotEqualTo(initialStatus);
        }
    }



    @Test
    void testProjectCommentUpdate() throws InterruptedException {
        if (myProjectsPage.getProjectCount() > 0) {
            String comment = "Test comment " + System.currentTimeMillis();
            myProjectsPage.updateFirstProjectComment(comment);

            String actual = myProjectsPage.getCommentOfLastProject();
            assertThat(actual).isEqualTo(comment);
        }
    }



    @Test
    void testProjectCountDisplay() {
        int displayedCount = myProjectsPage.getProjectCount();
        if (displayedCount > 0) {
            assertThat(myProjectsPage.getProjectCount()).isEqualTo(displayedCount);
        }
    }




}