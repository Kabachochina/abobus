package ru.kabachok.abobus.repository;

import ru.kabachok.abobus.entity.RouteFare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouteFareRepository extends JpaRepository<RouteFare, Long> {

    Optional<RouteFare> findByRouteIdAndFromRouteStopIdAndToRouteStopId(
            Long routeId,
            Long fromRouteStopId,
            Long toRouteStopId
    );
}