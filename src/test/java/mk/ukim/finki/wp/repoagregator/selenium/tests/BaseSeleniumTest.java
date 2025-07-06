package mk.ukim.finki.wp.repoagregator.selenium.tests;

import mk.ukim.finki.wp.repoagregator.selenium.config.SeleniumConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SeleniumConfig.class)
public abstract class BaseSeleniumTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected WebDriver driver;


    @BeforeEach
    void setUp() throws InterruptedException {
        driver.manage().window().fullscreen();
    }



    protected String getUrl(String path) {
        return "http://localhost:" + port + path;
    }
}