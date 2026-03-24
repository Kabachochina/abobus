package ru.kabachok.abobus.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kabachok.abobus.entity.Route;
import ru.kabachok.abobus.entity.RouteFare;
import ru.kabachok.abobus.entity.RouteStop;
import ru.kabachok.abobus.entity.Trip;
import ru.kabachok.abobus.repository.OrderRepository;
import ru.kabachok.abobus.repository.RouteFareRepository;
import ru.kabachok.abobus.repository.RouteRepository;
import ru.kabachok.abobus.repository.RouteStopRepository;
import ru.kabachok.abobus.repository.TripRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class TransportDaoImpl implements TransportDao {

    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final RouteFareRepository routeFareRepository;
    private final TripRepository tripRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Route> getActiveRoutesByCompany(Long companyId) {
        return routeRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> searchActiveRoutes(String routeNumberPart, String routeNamePart) {
        String routeNumber = routeNumberPart == null ? "" : routeNumberPart;
        String routeName = routeNamePart == null ? "" : routeNamePart;
        return routeRepository.findByIsActiveTrueAndRouteNumberContainingIgnoreCaseAndNameContainingIgnoreCase(
                routeNumber,
                routeName
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Route> getRouteById(Long routeId) {
        return routeRepository.findById(routeId);
    }

    @Override
    public Route createRoute(Route route) {
        route.setId(null);

        if (route.getCreatedAt() == null) {
            route.setCreatedAt(OffsetDateTime.now());
        }
        if (route.getIsActive() == null) {
            route.setIsActive(true);
        }

        return routeRepository.save(route);
    }

    @Override
    public Optional<Route> updateRoute(Long routeId, Route updatedRoute) {
        Optional<Route> optionalRoute = routeRepository.findById(routeId);
        if (optionalRoute.isEmpty()) {
            return Optional.empty();
        }

        Route route = optionalRoute.get();

        if (updatedRoute.getRouteNumber() != null) {
            route.setRouteNumber(updatedRoute.getRouteNumber());
        }
        if (updatedRoute.getName() != null) {
            route.setName(updatedRoute.getName());
        }
        if (updatedRoute.getCompany() != null) {
            route.setCompany(updatedRoute.getCompany());
        }
        if (updatedRoute.getIsActive() != null) {
            route.setIsActive(updatedRoute.getIsActive());
        }

        return Optional.of(routeRepository.save(route));
    }

    @Override
    public boolean deactivateRoute(Long routeId) {
        Optional<Route> optionalRoute = routeRepository.findById(routeId);
        if (optionalRoute.isEmpty()) {
            return false;
        }

        Route route = optionalRoute.get();
        route.setIsActive(false);
        routeRepository.save(route);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteStop> getOrderedStopsForRoute(Long routeId) {
        return routeStopRepository.findByRouteIdOrderBySeqAsc(routeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BigDecimal> getFare(Long routeId, Long fromRouteStopId, Long toRouteStopId) {
        return routeFareRepository
                .findByRouteIdAndFromRouteStopIdAndToRouteStopId(routeId, fromRouteStopId, toRouteStopId)
                .map(RouteFare::getPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trip> getTripsForRouteOnDate(Long routeId, LocalDate date) {
        OffsetDateTime from = date.atStartOfDay().atOffset(ZoneOffset.ofHours(3));
        OffsetDateTime to = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.ofHours(3));
        return tripRepository.findTripsByRouteAndDay(routeId, from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public long getOccupiedSeats(Long tripId) {
        return orderRepository.countByTripIdAndStatusIn(tripId, List.of("paid", "created"));
    }

    @Override
    @Transactional(readOnly = true)
    public long getAvailableSeats(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));

        return trip.getCapacity() - getOccupiedSeats(tripId);
    }
}