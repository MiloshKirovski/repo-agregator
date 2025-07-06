package mk.ukim.finki.wp.repoagregator.selenium.tests;

import mk.ukim.finki.wp.repoagregator.selenium.pages.ProjectDetailsPage;
import mk.ukim.finki.wp.repoagregator.selenium.pages.LoginPage;
import mk.ukim.finki.wp.repoagregator.selenium.pages.ProjectPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectDetailsPageTest extends BaseSeleniumTest {

    private ProjectDetailsPage projectDetailsPage;
    private ProjectPage projectsPage;
    private LoginPage loginPage;

    @BeforeEach
    void setUp() throws InterruptedException {
        loginPage = new LoginPage(driver);
        projectsPage = new ProjectPage(driver);
        projectDetailsPage = new ProjectDetailsPage(driver);
        driver.get(getUrl("/login"));
        loginPage.login("221071", "SystemPass");
        driver.get(getUrl("/projects"));
         projectsPage.clickProjectDetails(0);
         Thread.sleep(1000);

    }

    @Test
    void testPageLoads() {
        assertThat(projectDetailsPage.isPageLoaded()).isNotEmpty();

    }

    @Test
    void testProjectTitleAndYearDisplayed() {
        assertThat(projectDetailsPage.getProjectTitle()).isNotEmpty();
        assertThat(projectDetailsPage.getProjectYear()).isNotEmpty();
    }

    @Test
    void testRepositoryLinks() {
        assertThat(projectDetailsPage.hasRepositoryLinks()).isTrue();
    }
    @Test
    void testBackToProjectsNavigation() {

        projectDetailsPage.clickBackToProjects();
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).endsWith("/projects");
    }

}

