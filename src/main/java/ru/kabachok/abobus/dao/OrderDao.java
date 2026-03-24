package ru.kabachok.abobus.dao;

import ru.kabachok.abobus.entity.OrderEntity;

import java.util.List;
import java.util.Optional;

public interface OrderDao {

    List<OrderEntity> getOrdersByClient(Long clientId);

    List<OrderEntity> getPaidOrdersByClient(Long clientId);

    Optional<OrderEntity> getOrderById(Long orderId);

    OrderEntity createOrder(OrderEntity order);

    Optional<OrderEntity> markOrderAsPaid(Long orderId);

    Optional<OrderEntity> markOrderPaymentFailed(Long orderId);

    Optional<OrderEntity> cancelOrder(Long orderId, String reason);
}