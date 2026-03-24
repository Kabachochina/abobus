package ru.kabachok.abobus.dao;

import ru.kabachok.abobus.entity.OrderEntity;
import ru.kabachok.abobus.support.BaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

public class OrderDaoTest extends BaseIntegrationTest {

    @Autowired
    private OrderDao orderDao;

    @Test
    public void shouldReturnOrdersByClient() {
        List<OrderEntity> orders = orderDao.getOrdersByClient(2L);

        assertNotNull(orders);
        assertEquals(orders.size(), 3);
        assertTrue(orders.stream().allMatch(order -> order.getClient().getId().equals(2L)));
    }

    @Test
    public void shouldReturnOnlyPaidOrdersByClient() {
        List<OrderEntity> orders = orderDao.getPaidOrdersByClient(2L);

        assertNotNull(orders);
        assertEquals(orders.size(), 3);
        assertTrue(orders.stream().allMatch(order -> "paid".equals(order.getStatus())));
    }

    @Test
    public void shouldReturnEmptyOrdersForUnknownClient() {
        List<OrderEntity> orders = orderDao.getOrdersByClient(999L);

        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void shouldCancelExistingOrder() {
        Optional<OrderEntity> result = orderDao.cancelOrder(4L, "Пользователь отказался");

        assertTrue(result.isPresent());

        OrderEntity order = result.get();
        assertEquals(order.getStatus(), "canceled");
        assertEquals(order.getPaymentStatus(), "failed");
        assertEquals(order.getCanceledReason(), "Пользователь отказался");
        assertNotNull(order.getCanceledAt());
    }

    @Test
    public void shouldReturnEmptyWhenCancelUnknownOrder() {
        Optional<OrderEntity> result = orderDao.cancelOrder(999L, "Нет такого заказа");

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldKeepCanceledOrderUnchangedIfAlreadyCanceled() {
        Optional<OrderEntity> result = orderDao.cancelOrder(6L, "Повторная отмена");

        assertTrue(result.isPresent());

        OrderEntity order = result.get();
        assertEquals(order.getStatus(), "canceled");
        assertEquals(order.getPaymentStatus(), "failed");
        assertEquals(order.getCanceledReason(), "Недостаточно средств");
        assertNotNull(order.getCanceledAt());
    }
}