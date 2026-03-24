package ru.kabachok.abobus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kabachok.abobus.entity.Route;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByCompanyIdAndIsActiveTrue(Long companyId);

    Optional<Route> findByCompanyIdAndRouteNumber(Long companyId, String routeNumber);

    List<Route> findByIsActiveTrueAndRouteNumberContainingIgnoreCaseAndNameContainingIgnoreCase(
            String routeNumberPart,
            String namePart
    );
}