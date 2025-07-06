package mk.ukim.finki.wp.repoagregator.selenium.tests;

import mk.ukim.finki.wp.repoagregator.selenium.pages.CreateProjectPage;
import mk.ukim.finki.wp.repoagregator.selenium.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateProjectPageTest extends BaseSeleniumTest {

    private CreateProjectPage createProjectPage;
    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(driver);
        createProjectPage = new CreateProjectPage(driver);
        driver.get(getUrl("/login"));
        loginPage.login("221071", "SystemPass");
        driver.get(getUrl("/projects/create"));
    }

    @Test
    void testPageLoads() {
        assertThat(createProjectPage.isPageLoaded()).isTrue();
    }

    @Test
    void testCreateProjectWithValidData() throws InterruptedException {

        createProjectPage.createProject(
                "Selenium Test Project 3",
                "2025",
                "This is a test project created by Selenium.",
                "Оперативни системи",
                "Ристе Стојанов",
                "Hristijan",
                "https://github.com/MiloshKirovski/repo-agregator"
        );

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThat(createProjectPage.successfulCreation()).isTrue();
    }
    @Test
    void testCreateProjectWithnotValidData() throws InterruptedException {

        createProjectPage.createProject(
                "",
                "2025",
                "This is a test project created by Selenium.",
                "Оперативни системи",
                "Ристе Стојанов",
                "Hristijan",
                "https://github.com/MiloshKirovski/repo-agregator"
        );

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThat(createProjectPage.successfulCreation()).isFalse();
    }

}