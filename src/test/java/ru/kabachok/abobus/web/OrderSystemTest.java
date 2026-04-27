package ru.kabachok.abobus.web;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;
import ru.kabachok.abobus.support.BaseSystemTest;

import static org.testng.Assert.*;

public class OrderSystemTest extends BaseSystemTest {

    @Test
    public void shouldCreatePayAndOpenOrderDetails() {
        driver.get(url("/orders/new?tripId=2"));

        new Select(byId("clientId")).selectByValue("1");
        new Select(byId("fromRouteStopId")).selectByValue("1");
        new Select(byId("toRouteStopId")).selectByValue("4");
        byId("create-order").click();

        assertTrue(driver.getCurrentUrl().contains("/payment"));
        assertEquals(driver.findElement(By.id("order-price")).getText(), "599.00");

        byId("cardNumber").clear();
        byId("cardNumber").sendKeys("4111111111111111");
        byId("pay-order").click();

        assertTrue(driver.findElement(By.id("success")).getText().contains("Оплата прошла"));
        byId("order-details").click();

        assertEquals(driver.findElement(By.id("order-status")).getText(), "paid");
        assertEquals(driver.findElement(By.id("payment-status")).getText(), "paid");
    }

    @Test
    public void shouldShowFailedPaymentPage() {
        driver.get(url("/orders/new?tripId=2"));

        new Select(byId("clientId")).selectByValue("2");
        new Select(byId("fromRouteStopId")).selectByValue("1");
        new Select(byId("toRouteStopId")).selectByValue("3");
        byId("create-order").click();

        byId("cardNumber").clear();
        byId("cardNumber").sendKeys("4000000000000000");
        byId("pay-order").click();

        assertTrue(driver.findElement(By.id("failed")).getText().contains("Оплата не прошла"));
        assertTrue(driver.findElement(By.id("retry-payment")).getAttribute("href").contains("/payment"));
    }

    @Test
    public void shouldShowErrorForInvalidStopPair() {
        driver.get(url("/orders/new?tripId=2"));

        new Select(byId("fromRouteStopId")).selectByValue("4");
        new Select(byId("toRouteStopId")).selectByValue("1");
        byId("create-order").click();

        assertTrue(driver.findElement(By.id("error")).getText().contains("Пункт отправления"));
    }

    @Test
    public void shouldCancelOrderThroughConfirmationPage() {
        driver.get(url("/orders/4"));

        byId("cancel-order").click();
        byId("reason").clear();
        byId("reason").sendKeys("Системная отмена");
        byId("confirm-cancel").click();

        assertEquals(driver.findElement(By.id("order-status")).getText(), "canceled");
        assertEquals(driver.findElement(By.id("payment-status")).getText(), "failed");
    }
}
