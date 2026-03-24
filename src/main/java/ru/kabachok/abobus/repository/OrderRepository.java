package ru.kabachok.abobus.repository;

import ru.kabachok.abobus.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByClientIdOrderByCreatedAtDesc(Long clientId);

    List<OrderEntity> findByClientIdAndStatusOrderByCreatedAtDesc(Long clientId, String status);

    long countByTripIdAndStatusIn(Long tripId, Collection<String> statuses);
}