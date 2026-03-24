package ru.kabachok.abobus.dao;

import ru.kabachok.abobus.entity.Route;
import ru.kabachok.abobus.entity.RouteStop;
import ru.kabachok.abobus.entity.Trip;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransportDao {

    List<Route> getActiveRoutesByCompany(Long companyId);

    List<RouteStop> getOrderedStopsForRoute(Long routeId);

    Optional<BigDecimal> getFare(Long routeId, Long fromRouteStopId, Long toRouteStopId);

    List<Trip> getTripsForRouteOnDate(Long routeId, LocalDate date);

    long getOccupiedSeats(Long tripId);

    long getAvailableSeats(Long tripId);
}