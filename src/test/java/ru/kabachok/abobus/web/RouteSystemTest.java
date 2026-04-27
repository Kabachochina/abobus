package ru.kabachok.abobus.web;

import org.openqa.selenium.By;
import org.testng.annotations.Test;
import ru.kabachok.abobus.support.BaseSystemTest;

import static org.testng.Assert.*;

public class RouteSystemTest extends BaseSystemTest {

    @Test
    public void shouldFilterOpenEditAndDeleteRouteThroughPages() {
        driver.get(url("/routes"));

        byId("number").sendKeys("BLN");
        byId("filter-routes").click();
        assertTrue(driver.findElement(By.id("routes-table")).getText().contains("BLN-101"));

        driver.findElement(By.cssSelector(".route-details")).click();
        assertEquals(driver.findElement(By.id("route-number")).getText(), "BLN-101");
        assertTrue(driver.findElement(By.id("stops-table")).getText().contains("Блиноград"));

        byId("edit-route").click();
        byId("name").clear();
        byId("name").sendKeys("Новое направление");
        byId("save-route").click();

        assertEquals(driver.findElement(By.id("route-name")).getText(), "Новое направление");

        byId("edit-route").click();
        byId("delete-route").click();

        assertTrue(driver.getCurrentUrl().endsWith("/routes"));
        assertFalse(driver.findElement(By.id("routes-table")).getText().contains("BLN-101"));
    }

    @Test
    public void shouldCreateRouteAndValidateRequiredNumber() {
        driver.get(url("/routes/new"));
        byId("save-route").click();
        assertTrue(driver.findElement(By.id("error")).getText().contains("Компания и номер рейса обязательны"));

        byId("routeNumber").sendKeys("SYS-777");
        byId("name").sendKeys("Системный тестовый рейс");
        byId("save-route").click();

        assertEquals(driver.findElement(By.id("route-number")).getText(), "SYS-777");
        assertTrue(driver.findElement(By.id("route-name")).getText().contains("Системный тестовый рейс"));
    }
}
