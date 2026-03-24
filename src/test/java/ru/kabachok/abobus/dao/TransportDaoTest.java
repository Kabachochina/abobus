package ru.kabachok.abobus.dao;

import ru.kabachok.abobus.entity.Route;
import ru.kabachok.abobus.entity.RouteStop;
import ru.kabachok.abobus.entity.Trip;
import ru.kabachok.abobus.support.BaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

public class TransportDaoTest extends BaseIntegrationTest {

    @Autowired
    private TransportDao transportDao;

    @Test
    public void shouldReturnActiveRoutesByCompany() {
        List<Route> routes = transportDao.getActiveRoutesByCompany(1L);

        assertNotNull(routes);
        assertEquals(routes.size(), 1);
        assertEquals(routes.get(0).getRouteNumber(), "BLN-101");
        assertTrue(routes.get(0).getIsActive());
    }

    @Test
    public void shouldReturnEmptyRoutesForUnknownCompany() {
        List<Route> routes = transportDao.getActiveRoutesByCompany(999L);

        assertNotNull(routes);
        assertTrue(routes.isEmpty());
    }

    @Test
    public void shouldReturnOrderedStopsForRoute() {
        List<RouteStop> stops = transportDao.getOrderedStopsForRoute(1L);

        assertNotNull(stops);
        assertEquals(stops.size(), 4);
        assertEquals(stops.get(0).getSeq().intValue(), 1);
        assertEquals(stops.get(1).getSeq().intValue(), 2);
        assertEquals(stops.get(2).getSeq().intValue(), 3);
        assertEquals(stops.get(3).getSeq().intValue(), 4);
    }

    @Test
    public void shouldReturnFareIfExists() {
        Optional<BigDecimal> fare = transportDao.getFare(1L, 1L, 4L);

        assertTrue(fare.isPresent());
        assertEquals(fare.get(), new BigDecimal("599.00"));
    }

    @Test
    public void shouldReturnEmptyFareIfNotExists() {
        Optional<BigDecimal> fare = transportDao.getFare(1L, 4L, 1L);

        assertTrue(fare.isEmpty());
    }

    @Test
    public void shouldReturnTripsForRouteOnDate() {
        List<Trip> trips = transportDao.getTripsForRouteOnDate(1L, LocalDate.of(2026, 3, 10));

        assertNotNull(trips);
        assertEquals(trips.size(), 1);
        assertEquals(trips.get(0).getId().longValue(), 1L);
        assertEquals(trips.get(0).getCapacity().intValue(), 45);
    }

    @Test
    public void shouldCalculateOccupiedSeats() {
        long occupied = transportDao.getOccupiedSeats(1L);

        assertEquals(occupied, 4L);
    }

    @Test
    public void shouldCalculateAvailableSeats() {
        long available = transportDao.getAvailableSeats(1L);

        assertEquals(available, 41L);
    }

    @Test(expectedExceptions = InvalidDataAccessApiUsageException.class)
    public void shouldThrowWhenTripNotFoundForAvailableSeats() {
        transportDao.getAvailableSeats(999L);
    }
}