package mk.ukim.finki.wp.repoagregator.selenium.tests;

import mk.ukim.finki.wp.repoagregator.selenium.pages.ProjectPage;
import mk.ukim.finki.wp.repoagregator.selenium.pages.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectManagementTest extends BaseSeleniumTest {

    private ProjectPage projectsPage;
    private LoginPage loginPage;

    @BeforeEach
    void setUpTest() {
        projectsPage = new ProjectPage(driver);
        loginPage = new LoginPage(driver);

        driver.get(getUrl("/login"));
        loginPage.login("221071", "SystemPass");
    }

    @Test
    void testProjectsPageLoads() {
        driver.get(getUrl("/projects"));

        assertThat(driver.getTitle()).contains("RepoAggregator - Projects");
        assertThat(driver.getCurrentUrl()).contains("/projects");
    }

    @Test
    void testSearchFunctionality() {
        driver.get(getUrl("/projects"));

        projectsPage.searchProjects("Project 1");

        assertThat(driver.getCurrentUrl()).contains("search=Project+1");
    }

    @Test
    void testFilterByCourse() {
        driver.get(getUrl("/projects"));


        projectsPage.filterByCourse("F23L2W096");

        assertThat(driver.getCurrentUrl()).contains("course=F23L2W096");

        assertEquals("F23L2W096", projectsPage.getSelectedCourseValue());
    }

    @Test
    void testFilterByYear() {
        driver.get(getUrl("/projects"));

        projectsPage.filterByYear("2024");

        assertThat(driver.getCurrentUrl()).contains("year=2024");
    }

    @Test
    void testAddNewProjectNavigation() {
        driver.get(getUrl("/projects"));

        projectsPage.clickAddNewProject();

        assertThat(driver.getCurrentUrl()).contains("/projects/create");
    }

    @Test
    void testProjectDetailsNavigation() {
        driver.get(getUrl("/projects"));

        if (projectsPage.getProjectCount() > 0) {
            projectsPage.clickProjectDetails(0);
            assertThat(driver.getCurrentUrl()).contains("/projects/");
        }
    }
}
