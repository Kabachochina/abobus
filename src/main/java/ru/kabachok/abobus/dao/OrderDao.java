package ru.kabachok.abobus.dao;

import ru.kabachok.abobus.entity.OrderEntity;

import java.util.List;
import java.util.Optional;

public interface OrderDao {

    List<OrderEntity> getOrdersByClient(Long clientId);

    List<OrderEntity> getPaidOrdersByClient(Long clientId);

    Optional<OrderEntity> cancelOrder(Long orderId, String reason);
}