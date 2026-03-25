package ru.kabachok.abobus.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import ru.kabachok.abobus.entity.Client;
import ru.kabachok.abobus.entity.OrderEntity;
import ru.kabachok.abobus.entity.RouteStop;
import ru.kabachok.abobus.entity.Trip;
import ru.kabachok.abobus.support.BaseIntegrationTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

@Transactional
public class OrderDaoTest extends BaseIntegrationTest {

    @Autowired
    private OrderDao orderDao;

    @Test
    public void shouldReturnOrdersByClient() {
        List<OrderEntity> orders = orderDao.getOrdersByClient(2L);

        assertNotNull(orders);
        assertEquals(orders.size(), 3);

        for (OrderEntity order : orders) {
            assertNotNull(order.getId());
            assertNotNull(order.getClient());
            assertEquals(order.getClient().getId(), Long.valueOf(2L));
            assertNotNull(order.getTrip());
            assertNotNull(order.getFromRouteStop());
            assertNotNull(order.getToRouteStop());
            assertNotNull(order.getPrice());
            assertNotNull(order.getStatus());
            assertNotNull(order.getPaymentStatus());
            assertNotNull(order.getCreatedAt());
        }

        assertTrue(
                orders.get(0).getCreatedAt().isAfter(orders.get(1).getCreatedAt())
                        || orders.get(0).getCreatedAt().isEqual(orders.get(1).getCreatedAt())
        );
        assertTrue(
                orders.get(1).getCreatedAt().isAfter(orders.get(2).getCreatedAt())
                        || orders.get(1).getCreatedAt().isEqual(orders.get(2).getCreatedAt())
        );
    }

    @Test
    public void shouldReturnEmptyOrdersWhenClientHasNoOrders() {
        List<OrderEntity> orders = orderDao.getOrdersByClient(999L);

        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void shouldReturnOnlyPaidOrdersByClient() {
        List<OrderEntity> orders = orderDao.getPaidOrdersByClient(2L);

        assertNotNull(orders);
        assertEquals(orders.size(), 3);

        for (OrderEntity order : orders) {
            assertNotNull(order.getId());
            assertNotNull(order.getClient());
            assertEquals(order.getClient().getId(), Long.valueOf(2L));
            assertEquals(order.getStatus(), "paid");
            assertNotNull(order.getTrip());
            assertNotNull(order.getPrice());
            assertNotNull(order.getCreatedAt());
        }
    }

    @Test
    public void shouldReturnEmptyPaidOrdersWhenClientHasNoPaidOrders() {
        List<OrderEntity> orders = orderDao.getPaidOrdersByClient(999L);

        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void shouldReturnOrderByIdWhenExists() {
        Optional<OrderEntity> orderOpt = orderDao.getOrderById(1L);

        assertTrue(orderOpt.isPresent());

        OrderEntity order = orderOpt.get();
        assertEquals(order.getId(), Long.valueOf(1L));
        assertNotNull(order.getClient());
        assertEquals(order.getClient().getId(), Long.valueOf(1L));
        assertNotNull(order.getTrip());
        assertNotNull(order.getFromRouteStop());
        assertNotNull(order.getToRouteStop());
        assertNotNull(order.getPrice());
        assertNotNull(order.getStatus());
        assertNotNull(order.getPaymentStatus());
        assertNotNull(order.getCreatedAt());
    }

    @Test
    public void shouldReturnEmptyWhenOrderByIdNotFound() {
        Optional<OrderEntity> orderOpt = orderDao.getOrderById(999L);

        assertTrue(orderOpt.isEmpty());
    }

    @Test
    public void shouldCreateOrder() {
        OrderEntity template = orderDao.getOrderById(1L).orElseThrow();

        OrderEntity order = new OrderEntity();
        order.setClient(template.getClient());
        order.setTrip(template.getTrip());
        order.setFromRouteStop(template.getFromRouteStop());
        order.setToRouteStop(template.getToRouteStop());
        order.setPrice(new BigDecimal("150.00"));

        OrderEntity saved = orderDao.createOrder(order);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertNotNull(saved.getClient());
        assertEquals(saved.getClient().getId(), Long.valueOf(1L));
        assertNotNull(saved.getTrip());
        assertEquals(saved.getTrip().getId(), template.getTrip().getId());
        assertNotNull(saved.getFromRouteStop());
        assertEquals(saved.getFromRouteStop().getId(), template.getFromRouteStop().getId());
        assertNotNull(saved.getToRouteStop());
        assertEquals(saved.getToRouteStop().getId(), template.getToRouteStop().getId());
        assertEquals(saved.getPrice(), new BigDecimal("150.00"));
        assertEquals(saved.getStatus(), "created");
        assertEquals(saved.getPaymentStatus(), "pending");
        assertNotNull(saved.getCreatedAt());
        assertNull(saved.getPaidAt());
        assertNull(saved.getCanceledAt());
        assertNull(saved.getCanceledReason());
    }

    @Test
    public void shouldMarkOrderAsPaid() {
        OrderEntity template = orderDao.getOrderById(1L).orElseThrow();

        OrderEntity order = new OrderEntity();
        order.setClient(template.getClient());
        order.setTrip(template.getTrip());
        order.setFromRouteStop(template.getFromRouteStop());
        order.setToRouteStop(template.getToRouteStop());
        order.setPrice(new BigDecimal("170.00"));

        OrderEntity created = orderDao.createOrder(order);

        Optional<OrderEntity> paidOpt = orderDao.markOrderAsPaid(created.getId());

        assertTrue(paidOpt.isPresent());

        OrderEntity paid = paidOpt.get();
        assertEquals(paid.getId(), created.getId());
        assertEquals(paid.getStatus(), "paid");
        assertEquals(paid.getPaymentStatus(), "paid");
        assertNotNull(paid.getPaidAt());
        assertNull(paid.getCanceledAt());
        assertNull(paid.getCanceledReason());
    }

    @Test
    public void shouldReturnEmptyWhenMarkOrderAsPaidForMissingOrder() {
        Optional<OrderEntity> paidOpt = orderDao.markOrderAsPaid(999L);

        assertTrue(paidOpt.isEmpty());
    }

    @Test
    public void shouldMarkOrderPaymentFailed() {
        OrderEntity template = orderDao.getOrderById(1L).orElseThrow();

        OrderEntity order = new OrderEntity();
        order.setClient(template.getClient());
        order.setTrip(template.getTrip());
        order.setFromRouteStop(template.getFromRouteStop());
        order.setToRouteStop(template.getToRouteStop());
        order.setPrice(new BigDecimal("180.00"));

        OrderEntity created = orderDao.createOrder(order);

        Optional<OrderEntity> failedOpt = orderDao.markOrderPaymentFailed(created.getId());

        assertTrue(failedOpt.isPresent());

        OrderEntity failed = failedOpt.get();
        assertEquals(failed.getId(), created.getId());
        assertEquals(failed.getStatus(), "created");
        assertEquals(failed.getPaymentStatus(), "failed");
        assertNull(failed.getPaidAt());
        assertNull(failed.getCanceledAt());
        assertNull(failed.getCanceledReason());
    }

    @Test
    public void shouldReturnEmptyWhenMarkOrderPaymentFailedForMissingOrder() {
        Optional<OrderEntity> failedOpt = orderDao.markOrderPaymentFailed(999L);

        assertTrue(failedOpt.isEmpty());
    }

    @Test
    public void shouldCancelOrderWhenExists() {
        Optional<OrderEntity> canceledOpt = orderDao.cancelOrder(1L, "client changed plans");

        assertTrue(canceledOpt.isPresent());

        OrderEntity canceled = canceledOpt.get();
        assertEquals(canceled.getId(), Long.valueOf(1L));
        assertEquals(canceled.getStatus(), "canceled");
        assertEquals(canceled.getPaymentStatus(), "failed");
        assertNotNull(canceled.getCanceledAt());
        assertEquals(canceled.getCanceledReason(), "client changed plans");
    }

    @Test
    public void shouldReturnEmptyWhenCancelMissingOrder() {
        Optional<OrderEntity> canceledOpt = orderDao.cancelOrder(999L, "no such order");

        assertTrue(canceledOpt.isEmpty());
    }

    @Test
    public void shouldReturnSameCanceledOrderWhenCancelCalledTwice() {
        Optional<OrderEntity> firstCancelOpt = orderDao.cancelOrder(1L, "first reason");
        assertTrue(firstCancelOpt.isPresent());

        Optional<OrderEntity> secondCancelOpt = orderDao.cancelOrder(1L, "second reason");
        assertTrue(secondCancelOpt.isPresent());

        OrderEntity secondCancel = secondCancelOpt.get();
        assertEquals(secondCancel.getId(), Long.valueOf(1L));
        assertEquals(secondCancel.getStatus(), "canceled");
        assertEquals(secondCancel.getPaymentStatus(), "failed");
        assertEquals(secondCancel.getCanceledReason(), "first reason");
        assertNotNull(secondCancel.getCanceledAt());
    }

    @Test
    public void shouldNotChangeCanceledOrderWhenMarkOrderAsPaid() {
        Optional<OrderEntity> firstCancelOpt = orderDao.cancelOrder(1L, "already canceled");
        assertTrue(firstCancelOpt.isPresent());

        Optional<OrderEntity> paidOpt = orderDao.markOrderAsPaid(1L);
        assertTrue(paidOpt.isPresent());

        OrderEntity order = paidOpt.get();
        assertEquals(order.getId(), Long.valueOf(1L));
        assertEquals(order.getStatus(), "canceled");
        assertEquals(order.getPaymentStatus(), "failed");
        assertEquals(order.getCanceledReason(), "already canceled");
        assertNotNull(order.getCanceledAt());
    }

    @Test
    public void shouldNotChangeCanceledOrderWhenMarkOrderPaymentFailed() {
        Optional<OrderEntity> firstCancelOpt = orderDao.cancelOrder(1L, "already canceled");
        assertTrue(firstCancelOpt.isPresent());

        Optional<OrderEntity> failedOpt = orderDao.markOrderPaymentFailed(1L);
        assertTrue(failedOpt.isPresent());

        OrderEntity order = failedOpt.get();
        assertEquals(order.getId(), Long.valueOf(1L));
        assertEquals(order.getStatus(), "canceled");
        assertEquals(order.getPaymentStatus(), "failed");
        assertEquals(order.getCanceledReason(), "already canceled");
        assertNotNull(order.getCanceledAt());
    }

    @Test
    public void shouldKeepExplicitFieldsWhenCreateOrder() {
        OrderEntity template = orderDao.getOrderById(1L).orElseThrow();

        OrderEntity order = new OrderEntity();
        order.setClient(template.getClient());
        order.setTrip(template.getTrip());
        order.setFromRouteStop(template.getFromRouteStop());
        order.setToRouteStop(template.getToRouteStop());
        order.setPrice(new BigDecimal("199.00"));

        java.time.OffsetDateTime createdAt =
                java.time.OffsetDateTime.parse("2026-03-01T12:00:00+03:00");
        order.setCreatedAt(createdAt);
        order.setStatus("paid");
        order.setPaymentStatus("paid");

        OrderEntity saved = orderDao.createOrder(order);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(saved.getPrice(), new BigDecimal("199.00"));
        assertEquals(saved.getCreatedAt(), createdAt);
        assertEquals(saved.getStatus(), "paid");
        assertEquals(saved.getPaymentStatus(), "paid");

        assertNull(saved.getPaidAt());
        assertNull(saved.getCanceledAt());
        assertNull(saved.getCanceledReason());
    }
}