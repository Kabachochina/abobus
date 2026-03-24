package ru.kabachok.abobus.dao;

import ru.kabachok.abobus.entity.Route;
import ru.kabachok.abobus.entity.RouteFare;
import ru.kabachok.abobus.entity.RouteStop;
import ru.kabachok.abobus.entity.Trip;
import ru.kabachok.abobus.repository.OrderRepository;
import ru.kabachok.abobus.repository.RouteFareRepository;
import ru.kabachok.abobus.repository.RouteRepository;
import ru.kabachok.abobus.repository.RouteStopRepository;
import ru.kabachok.abobus.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransportDaoImpl implements TransportDao {

    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final RouteFareRepository routeFareRepository;
    private final TripRepository tripRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<Route> getActiveRoutesByCompany(Long companyId) {
        return routeRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    @Override
    public List<RouteStop> getOrderedStopsForRoute(Long routeId) {
        return routeStopRepository.findByRouteIdOrderBySeqAsc(routeId);
    }

    @Override
    public Optional<BigDecimal> getFare(Long routeId, Long fromRouteStopId, Long toRouteStopId) {
        return routeFareRepository
                .findByRouteIdAndFromRouteStopIdAndToRouteStopId(routeId, fromRouteStopId, toRouteStopId)
                .map(RouteFare::getPrice);
    }

    @Override
    public List<Trip> getTripsForRouteOnDate(Long routeId, LocalDate date) {
        OffsetDateTime from = date.atStartOfDay().atOffset(ZoneOffset.ofHours(3));
        OffsetDateTime to = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.ofHours(3));
        return tripRepository.findTripsByRouteAndDay(routeId, from, to);
    }

    @Override
    public long getOccupiedSeats(Long tripId) {
        return orderRepository.countByTripIdAndStatusIn(tripId, List.of("paid", "created"));
    }

    @Override
    public long getAvailableSeats(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));

        return trip.getCapacity() - getOccupiedSeats(tripId);
    }
}