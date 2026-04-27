package ru.kabachok.abobus.support;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public abstract class BaseSystemTest extends AbstractTestNGSpringContextTests {

    @LocalServerPort
    protected int port;

    @Autowired
    private DataSource dataSource;

    protected WebDriver driver;

    @BeforeClass
    public void initSchema() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/drop.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/create.sql"));
        }
    }

    @BeforeMethod
    public void setUpBrowserAndData() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/cleanup.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/insert.sql"));
        }
        driver = new HtmlUnitDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected String url(String path) {
        return "http://localhost:" + port + path;
    }

    protected WebElement byId(String id) {
        return driver.findElement(By.id(id));
    }
}
