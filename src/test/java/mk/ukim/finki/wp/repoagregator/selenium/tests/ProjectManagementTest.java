package mk.ukim.finki.wp.repoagregator.selenium.tests;

import mk.ukim.finki.wp.repoagregator.selenium.pages.ProjectPage;
import mk.ukim.finki.wp.repoagregator.selenium.pages.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
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

    @ParameterizedTest
    @ValueSource(strings = {"LONGPROJETNAMELONGPROJETNAMELONGPROJETNAMELONGPROJETNAMELONGPROJETNAMELONGPROJETNAME", "ShortName", " "})
    void testSearchFunctionality(String searchTerm) throws InterruptedException {
        driver.get(getUrl("/projects"));
        projectsPage.searchProjects(searchTerm);
        Thread.sleep(1000);
        if(searchTerm.equals(" ")){
            assertThat(driver.getCurrentUrl()).contains("search=");

        }else {
            assertThat(driver.getCurrentUrl()).contains("search=" + searchTerm);
        }
        }
   @Test
    void testFilterByCourse() throws InterruptedException {
        driver.get(getUrl("/projects"));


        projectsPage.filterByCourse("F23L2W096");

        assertThat(driver.getCurrentUrl()).contains("course=F23L2W096");
        Thread.sleep(1000);


        assertEquals("F23L2W096", projectsPage.getSelectedCourseValue());
    }
    @ParameterizedTest
    @ValueSource(strings = {"2024", "202520252025", " "})
    void testFilterByYear(String searchTerm) throws InterruptedException {

        driver.get(getUrl("/projects"));

        projectsPage.filterByYear(searchTerm);
        Thread.sleep(1000);

        if(searchTerm.equals(" ")){
            assertThat(driver.getCurrentUrl()).contains("year=");

        }else {
            assertThat(driver.getCurrentUrl()).contains("year=" + searchTerm);
        }
    }
    @Test
    void testCombinedFilteringFunctionality() throws InterruptedException {
        driver.get(getUrl("/projects"));

        projectsPage.searchProjects("Project");
        Thread.sleep(1000);

        projectsPage.filterByCourse("F23L2W096");
        Thread.sleep(1000);

        projectsPage.filterByYear("2024");
        Thread.sleep(1000);

        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("search=Project");
        assertThat(currentUrl).contains("course=F23L2W096");
        assertThat(currentUrl).contains("year=2024");

        assertEquals("F23L2W096", projectsPage.getSelectedCourseValue());
    }

    @Test
    void testAddNewProjectNavigation() {
        driver.get(getUrl("/projects"));

        projectsPage.clickAddNewProject();

        assertThat(driver.getCurrentUrl()).contains("/projects/create");
    }

    @Test
    void testProjectDetailsNavigation() throws InterruptedException {
        driver.get(getUrl("/projects"));

        if (projectsPage.getProjectCount() > 0) {
            projectsPage.clickProjectDetails(0);
            assertThat(driver.getCurrentUrl()).contains("/projects/");
        }
    }
}
