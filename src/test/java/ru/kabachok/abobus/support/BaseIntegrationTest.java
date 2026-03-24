package ru.kabachok.abobus.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public abstract class BaseIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    protected DataSource dataSource;

    @BeforeClass
    public void initSchema() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/drop.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/create.sql"));
        }
    }

    @BeforeMethod
    public void reloadData() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/cleanup.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/insert.sql"));
        }
    }
}