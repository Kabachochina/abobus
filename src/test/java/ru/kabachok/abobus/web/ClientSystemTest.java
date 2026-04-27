package ru.kabachok.abobus.web;

import org.openqa.selenium.By;
import org.testng.annotations.Test;
import ru.kabachok.abobus.support.BaseSystemTest;

import static org.testng.Assert.*;

public class ClientSystemTest extends BaseSystemTest {

    @Test
    public void shouldCreateEditAndDeleteClientThroughPages() {
        driver.get(url("/clients"));
        byId("add-client").click();

        byId("fullName").sendKeys("Системный Клиент");
        byId("email").sendKeys("system@example.com");
        byId("phone").sendKeys("+7-900-555-44-33");
        byId("address").sendKeys("Системный адрес");
        byId("save-client").click();

        assertTrue(driver.findElement(By.id("client-name")).getText().contains("Системный Клиент"));
        assertEquals(driver.findElement(By.id("client-email")).getText(), "system@example.com");

        byId("edit-client").click();
        byId("fullName").clear();
        byId("fullName").sendKeys("Обновленный Клиент");
        byId("save-client").click();

        assertEquals(driver.findElement(By.id("client-name")).getText(), "Обновленный Клиент");

        byId("edit-client").click();
        byId("delete-client").click();

        assertTrue(driver.getCurrentUrl().endsWith("/clients"));
        assertFalse(driver.findElement(By.id("clients-table")).getText().contains("Обновленный Клиент"));
    }

    @Test
    public void shouldShowValidationErrorForClientWithoutName() {
        driver.get(url("/clients/new"));

        byId("email").sendKeys("bad@example.com");
        byId("save-client").click();

        assertTrue(driver.findElement(By.id("error")).getText().contains("ФИО обязательно"));
        assertTrue(driver.getCurrentUrl().endsWith("/clients"));
    }

    @Test
    public void shouldFilterClientsAndOpenOrderHistory() {
        driver.get(url("/clients"));

        byId("name").sendKeys("Оливье");
        byId("filter-clients").click();

        String table = driver.findElement(By.id("clients-table")).getText();
        assertTrue(table.contains("Салат Оливье Петрович"));
        assertFalse(table.contains("Суп Лапшичный Иванович"));

        driver.findElement(By.cssSelector(".client-details")).click();
        byId("client-orders").click();

        assertTrue(driver.findElement(By.id("orders-table")).getText().contains("BLN-101"));
        assertTrue(driver.findElements(By.cssSelector(".order-details")).size() >= 1);
    }
}
