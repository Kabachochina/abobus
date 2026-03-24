package ru.kabachok.abobus.dao;

import ru.kabachok.abobus.entity.OrderEntity;
import ru.kabachok.abobus.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class OrderDaoImpl implements OrderDao {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> getOrdersByClient(Long clientId) {
        return orderRepository.findByClientIdOrderByCreatedAtDesc(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> getPaidOrdersByClient(Long clientId) {
        return orderRepository.findByClientIdAndStatusOrderByCreatedAtDesc(clientId, "paid");
    }

    @Override
    public Optional<OrderEntity> cancelOrder(Long orderId, String reason) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            return Optional.empty();
        }

        OrderEntity order = optionalOrder.get();

        if ("canceled".equals(order.getStatus())) {
            return Optional.of(order);
        }

        order.setStatus("canceled");
        order.setPaymentStatus("failed");
        order.setCanceledAt(OffsetDateTime.now());
        order.setCanceledReason(reason);

        return Optional.of(orderRepository.save(order));
    }
}