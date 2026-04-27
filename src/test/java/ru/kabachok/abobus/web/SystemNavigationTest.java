package ru.kabachok.abobus.web;

import org.openqa.selenium.By;
import org.testng.annotations.Test;
import ru.kabachok.abobus.support.BaseSystemTest;

import static org.testng.Assert.*;

public class SystemNavigationTest extends BaseSystemTest {

    @Test
    public void shouldOpenHomeAndNavigateToMainSections() {
        driver.get(url("/home"));

        assertTrue(driver.getTitle().contains("Abobus"));
        assertTrue(driver.getPageSource().contains("Система информации"));

        byId("nav-routes").click();
        assertTrue(driver.getCurrentUrl().endsWith("/routes"));
        assertTrue(driver.findElement(By.id("routes-table")).getText().contains("BLN-101"));

        byId("nav-clients").click();
        assertTrue(driver.getCurrentUrl().endsWith("/clients"));
        assertTrue(driver.findElement(By.id("clients-table")).getText().contains("Салат Оливье Петрович"));
    }

    @Test
    public void shouldSearchRoutesFromHomeForm() {
        driver.get(url("/home"));

        byId("date").sendKeys("03-10-2026");
        byId("name").sendKeys("Блиноград");
        byId("quick-search").click();

        assertTrue(driver.getCurrentUrl().contains("/routes"));
        assertTrue(driver.findElement(By.id("routes-table")).getText().contains("BLN-101"));
        assertFalse(driver.findElement(By.id("routes-table")).getText().contains("PLM-202"));
    }
}
