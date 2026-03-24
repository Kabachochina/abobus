package ru.kabachok.abobus.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import ru.kabachok.abobus.entity.Company;
import ru.kabachok.abobus.entity.Route;
import ru.kabachok.abobus.entity.RouteStop;
import ru.kabachok.abobus.entity.Trip;
import ru.kabachok.abobus.support.BaseIntegrationTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

@Transactional
public class TransportDaoTest extends BaseIntegrationTest {

    @Autowired
    private TransportDao transportDao;

    @Test
    public void shouldReturnActiveRoutesByCompany() {
        List<Route> routes = transportDao.getActiveRoutesByCompany(1L);

        assertNotNull(routes);
        assertEquals(routes.size(), 1);

        Route route = routes.get(0);
        assertEquals(route.getId(), Long.valueOf(1L));
        assertEquals(route.getRouteNumber(), "BLN-101");
        assertEquals(route.getName(), "Блиноград Пельмешкино Борщевск Шаурмск");
        assertTrue(route.getIsActive());
        assertNotNull(route.getCompany());
        assertEquals(route.getCompany().getId(), Long.valueOf(1L));
        assertNotNull(route.getCreatedAt());
    }

    @Test
    public void shouldReturnEmptyRoutesForUnknownCompany() {
        List<Route> routes = transportDao.getActiveRoutesByCompany(999L);

        assertNotNull(routes);
        assertTrue(routes.isEmpty());
    }

    @Test
    public void shouldSearchActiveRoutesByRouteNumber() {
        List<Route> routes = transportDao.searchActiveRoutes("BLN", "");

        assertNotNull(routes);
        assertEquals(routes.size(), 1);

        Route route = routes.get(0);
        assertEquals(route.getId(), Long.valueOf(1L));
        assertEquals(route.getRouteNumber(), "BLN-101");
        assertTrue(route.getIsActive());
    }

    @Test
    public void shouldSearchActiveRoutesByNameFragment() {
        List<Route> routes = transportDao.searchActiveRoutes("", "Сушивиль");

        assertNotNull(routes);
        assertFalse(routes.isEmpty());
        assertTrue(routes.stream().anyMatch(route ->
                route.getId().equals(5L)
                        && "SUS-505".equals(route.getRouteNumber())
                        && "Сушивиль Сыроград Пирожково Блиноград".equals(route.getName())
                        && Boolean.TRUE.equals(route.getIsActive())
        ));
    }

    @Test
    public void shouldReturnEmptyWhenSearchFindsNothing() {
        List<Route> routes = transportDao.searchActiveRoutes("ZZZ", "Несуществующий");

        assertNotNull(routes);
        assertTrue(routes.isEmpty());
    }

    @Test
    public void shouldReturnRouteByIdWhenExists() {
        Optional<Route> routeOpt = transportDao.getRouteById(1L);

        assertTrue(routeOpt.isPresent());

        Route route = routeOpt.get();
        assertEquals(route.getId(), Long.valueOf(1L));
        assertEquals(route.getRouteNumber(), "BLN-101");
        assertEquals(route.getName(), "Блиноград Пельмешкино Борщевск Шаурмск");
        assertTrue(route.getIsActive());
        assertNotNull(route.getCompany());
        assertEquals(route.getCompany().getId(), Long.valueOf(1L));
        assertNotNull(route.getCreatedAt());
    }

    @Test
    public void shouldReturnEmptyWhenRouteByIdNotFound() {
        Optional<Route> routeOpt = transportDao.getRouteById(999L);

        assertTrue(routeOpt.isEmpty());
    }

    @Test
    public void shouldCreateRoute() {
        Company company = transportDao.getRouteById(1L).orElseThrow().getCompany();

        Route route = new Route();
        route.setCompany(company);
        route.setRouteNumber("BLN-999");
        route.setName("Тестовый маршрут");
        route.setIsActive(true);

        Route saved = transportDao.createRoute(route);

        assertNotNull(saved.getId());
        assertEquals(saved.getRouteNumber(), "BLN-999");
        assertEquals(saved.getName(), "Тестовый маршрут");
        assertTrue(saved.getIsActive());
        assertNotNull(saved.getCompany());
        assertEquals(saved.getCompany().getId(), Long.valueOf(1L));
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    public void shouldSetDefaultsWhenCreateRouteWithoutCreatedAtAndIsActive() {
        Company company = transportDao.getRouteById(1L).orElseThrow().getCompany();

        Route route = new Route();
        route.setCompany(company);
        route.setRouteNumber("BLN-998");
        route.setName("Маршрут по умолчанию");

        Route saved = transportDao.createRoute(route);

        assertNotNull(saved.getId());
        assertEquals(saved.getRouteNumber(), "BLN-998");
        assertEquals(saved.getName(), "Маршрут по умолчанию");
        assertTrue(saved.getIsActive());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getCompany());
        assertEquals(saved.getCompany().getId(), Long.valueOf(1L));
    }

    @Test
    public void shouldUpdateRouteWhenExists() {
        Optional<Route> beforeOpt = transportDao.getRouteById(1L);
        assertTrue(beforeOpt.isPresent());

        Route before = beforeOpt.get();
        assertNotNull(before.getCompany());
        assertNotNull(before.getIsActive());

        Route updated = new Route();
        updated.setRouteNumber("BLN-101-UPD");
        updated.setName("Обновлённый маршрут");

        Optional<Route> updatedOpt = transportDao.updateRoute(1L, updated);

        assertTrue(updatedOpt.isPresent());

        Route route = updatedOpt.get();
        assertEquals(route.getId(), Long.valueOf(1L));
        assertEquals(route.getRouteNumber(), "BLN-101-UPD");
        assertEquals(route.getName(), "Обновлённый маршрут");

        assertNotNull(route.getCompany());
        assertEquals(route.getCompany().getId(), before.getCompany().getId());
        assertEquals(route.getIsActive(), before.getIsActive());
    }

    @Test
    public void shouldReturnEmptyWhenUpdateMissingRoute() {
        Route updated = new Route();
        updated.setRouteNumber("NONE");
        updated.setName("No route");

        Optional<Route> updatedOpt = transportDao.updateRoute(999L, updated);

        assertTrue(updatedOpt.isEmpty());
    }

    @Test
    public void shouldDeactivateRouteWhenExists() {
        boolean deactivated = transportDao.deactivateRoute(1L);

        assertTrue(deactivated);

        Optional<Route> routeOpt = transportDao.getRouteById(1L);
        assertTrue(routeOpt.isPresent());
        assertFalse(routeOpt.get().getIsActive());

        List<Route> activeRoutes = transportDao.getActiveRoutesByCompany(1L);
        assertNotNull(activeRoutes);
        assertTrue(activeRoutes.isEmpty());
    }

    @Test
    public void shouldReturnFalseWhenDeactivateMissingRoute() {
        boolean deactivated = transportDao.deactivateRoute(999L);

        assertFalse(deactivated);
    }

    @Test
    public void shouldReturnOrderedStopsForRoute() {
        List<RouteStop> stops = transportDao.getOrderedStopsForRoute(1L);

        assertNotNull(stops);
        assertEquals(stops.size(), 4);

        assertEquals(stops.get(0).getId(), Long.valueOf(1L));
        assertEquals(stops.get(0).getSeq(), Integer.valueOf(1));

        assertEquals(stops.get(1).getId(), Long.valueOf(2L));
        assertEquals(stops.get(1).getSeq(), Integer.valueOf(2));

        assertEquals(stops.get(2).getId(), Long.valueOf(3L));
        assertEquals(stops.get(2).getSeq(), Integer.valueOf(3));

        assertEquals(stops.get(3).getId(), Long.valueOf(4L));
        assertEquals(stops.get(3).getSeq(), Integer.valueOf(4));
    }

    @Test
    public void shouldReturnEmptyStopsForUnknownRoute() {
        List<RouteStop> stops = transportDao.getOrderedStopsForRoute(999L);

        assertNotNull(stops);
        assertTrue(stops.isEmpty());
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

        Trip trip = trips.get(0);
        assertEquals(trip.getId(), Long.valueOf(1L));
        assertEquals(trip.getCapacity(), Integer.valueOf(45));
        assertEquals(trip.getStatus(), "scheduled");
        assertNotNull(trip.getDepartureAt());
        assertNotNull(trip.getCreatedAt());
    }

    @Test
    public void shouldReturnEmptyTripsForRouteOnDateWhenNoTrips() {
        List<Trip> trips = transportDao.getTripsForRouteOnDate(1L, LocalDate.of(2026, 3, 15));

        assertNotNull(trips);
        assertTrue(trips.isEmpty());
    }

    @Test
    public void shouldCalculateOccupiedSeats() {
        long occupied = transportDao.getOccupiedSeats(1L);

        assertEquals(occupied, 4L);
    }

    @Test
    public void shouldReturnZeroOccupiedSeatsForUnknownTrip() {
        long occupied = transportDao.getOccupiedSeats(999L);

        assertEquals(occupied, 0L);
    }

    @Test
    public void shouldSearchActiveRoutesWhenNullArgumentsPassed() {
        List<Route> routes = transportDao.searchActiveRoutes(null, null);

        assertNotNull(routes);
        assertFalse(routes.isEmpty());
    }

    @Test
    public void shouldCreateRouteWithoutChangingExplicitCreatedAtAndIsActive() {
        Company company = transportDao.getRouteById(1L).orElseThrow().getCompany();

        Route route = new Route();
        route.setCompany(company);
        route.setRouteNumber("BLN-997");
        route.setName("Маршрут с явными полями");
        route.setIsActive(false);
        route.setCreatedAt(java.time.OffsetDateTime.now().minusDays(1));

        Route saved = transportDao.createRoute(route);

        assertNotNull(saved.getId());
        assertEquals(saved.getRouteNumber(), "BLN-997");
        assertEquals(saved.getName(), "Маршрут с явными полями");
        assertFalse(saved.getIsActive());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    public void shouldUpdateRouteCompanyAndIsActiveWhenProvided() {
        Route updated = new Route();

        Company anotherCompany = new Company();
        anotherCompany.setId(2L);

        updated.setCompany(anotherCompany);
        updated.setIsActive(false);

        Optional<Route> updatedOpt = transportDao.updateRoute(1L, updated);

        assertTrue(updatedOpt.isPresent());

        Route route = updatedOpt.get();
        assertNotNull(route.getCompany());
        assertEquals(route.getCompany().getId(), Long.valueOf(2L));
        assertFalse(route.getIsActive());
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