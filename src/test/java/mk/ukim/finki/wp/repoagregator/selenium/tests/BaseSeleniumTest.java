package mk.ukim.finki.wp.repoagregator.selenium.tests;

import mk.ukim.finki.wp.repoagregator.selenium.config.SeleniumConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SeleniumConfig.class)


public abstract class BaseSeleniumTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected WebDriver driver;

    protected String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
    }

    protected String getUrl(String path) {
        return baseUrl + path;
    }
}
