package ru.kabachok.abobus.web;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.testng.annotations.Test;
import ru.kabachok.abobus.dao.ClientDao;
import ru.kabachok.abobus.dao.OrderDao;
import ru.kabachok.abobus.dao.TransportDao;
import ru.kabachok.abobus.entity.Client;
import ru.kabachok.abobus.entity.Company;
import ru.kabachok.abobus.entity.OrderEntity;
import ru.kabachok.abobus.entity.Route;
import ru.kabachok.abobus.entity.RouteStop;
import ru.kabachok.abobus.entity.Stop;
import ru.kabachok.abobus.entity.Trip;
import ru.kabachok.abobus.repository.CompanyRepository;
import ru.kabachok.abobus.repository.RouteStopRepository;
import ru.kabachok.abobus.repository.TripRepository;
import ru.kabachok.abobus.repository.TripStopTimeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class WebControllerBranchTest {

    @Test
    public void shouldCoverClientControllerAlternativeBranches() {
        ClientDao clientDao = mock(ClientDao.class);
        ClientController controller = new ClientController(clientDao);

        Client first = client(1L, "Первый Клиент", "first@example.com", "+7-111");
        Client second = client(2L, "Второй Клиент", "second@example.com", "+7-222");
        Client noContacts = client(3L, "Без Контактов", null, null);
        OrderEntity secondOrder = new OrderEntity();
        secondOrder.setTrip(trip(7L, route(5L)));

        when(clientDao.getAllActiveClients()).thenReturn(List.of(first, second, noContacts));
        when(clientDao.getClientOrderHistory(1L)).thenReturn(List.of());
        when(clientDao.getClientOrderHistory(2L)).thenReturn(List.of(secondOrder));
        when(clientDao.getClientOrderHistory(3L)).thenReturn(List.of());

        Model listModel = new ExtendedModelMap();
        assertEquals(controller.list("", "+7-222", 7L, listModel), "clients/list");
        List<Client> clients = castList(listModel.getAttribute("clients"));
        assertEquals(clients.size(), 1);
        assertEquals(clients.get(0).getId(), Long.valueOf(2L));

        Model blankContactModel = new ExtendedModelMap();
        assertEquals(controller.list(null, " ", null, blankContactModel), "clients/list");
        assertEquals(castList(blankContactModel.getAttribute("clients")).size(), 3);

        Model nullContactModel = new ExtendedModelMap();
        assertEquals(controller.list(null, null, null, nullContactModel), "clients/list");
        assertEquals(castList(nullContactModel.getAttribute("clients")).size(), 3);

        Model emailContactModel = new ExtendedModelMap();
        assertEquals(controller.list(null, "first@example.com", null, emailContactModel), "clients/list");
        List<Client> emailClients = castList(emailContactModel.getAttribute("clients"));
        assertEquals(emailClients.size(), 1);
        assertEquals(emailClients.get(0).getId(), Long.valueOf(1L));

        Model missingContactModel = new ExtendedModelMap();
        assertEquals(controller.list(null, "missing", null, missingContactModel), "clients/list");
        assertTrue(castList(missingContactModel.getAttribute("clients")).isEmpty());

        Model nullNameCreate = new ExtendedModelMap();
        Client nullName = new Client();
        assertEquals(controller.create(nullName, nullNameCreate), "clients/form");
        assertEquals(nullNameCreate.getAttribute("error"), "ФИО обязательно");

        Model missingDetails = new ExtendedModelMap();
        when(clientDao.getClientById(404L)).thenReturn(Optional.empty());
        assertEquals(controller.details(404L, missingDetails), "not-found");
        assertEquals(missingDetails.getAttribute("message"), "Клиент не найден");

        Model missingEdit = new ExtendedModelMap();
        assertEquals(controller.edit(404L, missingEdit), "not-found");

        Model invalidUpdate = new ExtendedModelMap();
        Client emptyName = new Client();
        emptyName.setFullName(" ");
        assertEquals(controller.update(1L, emptyName, invalidUpdate), "clients/form");
        assertEquals(invalidUpdate.getAttribute("error"), "ФИО обязательно");

        Model nullNameUpdate = new ExtendedModelMap();
        Client nullUpdateName = new Client();
        assertEquals(controller.update(1L, nullUpdateName, nullNameUpdate), "clients/form");
        assertEquals(nullNameUpdate.getAttribute("error"), "ФИО обязательно");

        Model missingUpdate = new ExtendedModelMap();
        Client updated = client(null, "Новое имя", "new@example.com", "+7-333");
        when(clientDao.updateClient(404L, updated)).thenReturn(Optional.empty());
        assertEquals(controller.update(404L, updated, missingUpdate), "not-found");

        Model missingOrders = new ExtendedModelMap();
        assertEquals(controller.orders(404L, missingOrders), "not-found");
    }

    @Test
    public void shouldCoverRouteControllerAlternativeBranches() {
        TransportDao transportDao = mock(TransportDao.class);
        CompanyRepository companyRepository = mock(CompanyRepository.class);
        TripRepository tripRepository = mock(TripRepository.class);
        RouteController controller = new RouteController(transportDao, companyRepository, tripRepository);

        Route first = route(1L);
        Route second = route(2L);
        when(transportDao.searchActiveRoutes(null, null)).thenReturn(List.of(first, second));
        when(transportDao.getTripsForRouteOnDate(1L, LocalDate.parse("2026-03-10"))).thenReturn(List.of(trip(1L, first)));
        when(transportDao.getTripsForRouteOnDate(2L, LocalDate.parse("2026-03-10"))).thenReturn(List.of());

        Model validDateModel = new ExtendedModelMap();
        assertEquals(controller.list(null, null, "2026-03-10", validDateModel), "routes/list");
        List<Route> routes = castList(validDateModel.getAttribute("routes"));
        assertEquals(routes.size(), 1);
        assertEquals(routes.get(0).getId(), Long.valueOf(1L));

        Model badDateModel = new ExtendedModelMap();
        assertEquals(controller.list(null, null, "bad-date", badDateModel), "routes/list");
        assertEquals(castList(badDateModel.getAttribute("routes")).size(), 2);

        Model blankDateModel = new ExtendedModelMap();
        assertEquals(controller.list(null, null, " ", blankDateModel), "routes/list");
        assertEquals(castList(blankDateModel.getAttribute("routes")).size(), 2);

        Model missingDetails = new ExtendedModelMap();
        when(transportDao.getRouteById(404L)).thenReturn(Optional.empty());
        assertEquals(controller.details(404L, missingDetails), "not-found");

        Model missingEdit = new ExtendedModelMap();
        assertEquals(controller.edit(404L, missingEdit), "not-found");

        Model invalidCreate = new ExtendedModelMap();
        when(companyRepository.findById(404L)).thenReturn(Optional.empty());
        when(companyRepository.findAll()).thenReturn(List.of(company(1L)));
        Route noNumberRoute = route(null);
        noNumberRoute.setRouteNumber("");
        assertEquals(controller.create(404L, noNumberRoute, invalidCreate), "routes/form");

        Model nullNumberCreate = new ExtendedModelMap();
        Route nullNumberRoute = route(null);
        nullNumberRoute.setRouteNumber(null);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company(1L)));
        assertEquals(controller.create(1L, nullNumberRoute, nullNumberCreate), "routes/form");

        Model invalidUpdate = new ExtendedModelMap();
        assertEquals(controller.update(1L, 404L, noNumberRoute, invalidUpdate), "routes/form");

        Model blankNumberUpdate = new ExtendedModelMap();
        assertEquals(controller.update(1L, 1L, noNumberRoute, blankNumberUpdate), "routes/form");

        Model nullNumberUpdate = new ExtendedModelMap();
        assertEquals(controller.update(1L, 1L, nullNumberRoute, nullNumberUpdate), "routes/form");

        Model missingUpdate = new ExtendedModelMap();
        Company company = company(1L);
        Route updated = route(null);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(transportDao.updateRoute(404L, updated)).thenReturn(Optional.empty());
        assertEquals(controller.update(404L, 1L, updated, missingUpdate), "not-found");
    }

    @Test
    public void shouldCoverOrderControllerAlternativeBranches() {
        OrderDao orderDao = mock(OrderDao.class);
        ClientDao clientDao = mock(ClientDao.class);
        TransportDao transportDao = mock(TransportDao.class);
        TripRepository tripRepository = mock(TripRepository.class);
        RouteStopRepository routeStopRepository = mock(RouteStopRepository.class);
        TripStopTimeRepository tripStopTimeRepository = mock(TripStopTimeRepository.class);
        OrderController controller = new OrderController(
                orderDao,
                clientDao,
                transportDao,
                tripRepository,
                routeStopRepository,
                tripStopTimeRepository
        );

        Model missingOrder = new ExtendedModelMap();
        when(orderDao.getOrderById(404L)).thenReturn(Optional.empty());
        assertEquals(controller.details(404L, missingOrder), "not-found");

        Model missingCancel = new ExtendedModelMap();
        assertEquals(controller.cancelForm(404L, missingCancel), "not-found");

        Model missingTrip = new ExtendedModelMap();
        when(tripRepository.findById(404L)).thenReturn(Optional.empty());
        assertEquals(controller.newOrder(404L, missingTrip), "not-found");

        Model missingPayment = new ExtendedModelMap();
        assertEquals(controller.payment(404L, missingPayment), "not-found");

        assertEquals(controller.cancel(1L, "Причина"), "redirect:/orders/1");
        verify(orderDao).cancelOrder(1L, "Причина");

        OrderEntity missingSuccessOrder = null;
        when(orderDao.getOrderById(405L)).thenReturn(Optional.ofNullable(missingSuccessOrder));
        Model successModel = new ExtendedModelMap();
        assertEquals(controller.paymentSuccess(405L, successModel), "orders/payment-success");
        assertNull(successModel.getAttribute("order"));

        Route route = route(5L);
        Trip trip = trip(7L, route);
        Client client = client(6L, "Компот Ягодович", "compote@example.com", "+7-666");
        RouteStop from = routeStop(16L, 2);
        RouteStop to = routeStop(17L, 3);

        when(tripRepository.findById(7L)).thenReturn(Optional.of(trip));
        when(clientDao.getClientById(6L)).thenReturn(Optional.of(client));
        when(routeStopRepository.findById(16L)).thenReturn(Optional.of(from));
        when(routeStopRepository.findById(17L)).thenReturn(Optional.of(to));
        when(transportDao.getOrderedStopsForRoute(5L)).thenReturn(List.of(from, to));
        when(clientDao.getAllActiveClients()).thenReturn(List.of(client));
        when(tripStopTimeRepository.findByTripIdOrderByRouteStopSeqAsc(7L)).thenReturn(List.of());
        when(transportDao.getAvailableSeats(7L)).thenReturn(10L);

        Model missingFormData = new ExtendedModelMap();
        assertEquals(controller.create(404L, 6L, 16L, 17L, missingFormData), "not-found");

        Model missingClient = new ExtendedModelMap();
        when(tripRepository.findById(8L)).thenReturn(Optional.of(trip));
        when(clientDao.getClientById(404L)).thenReturn(Optional.empty());
        assertEquals(controller.create(8L, 404L, 16L, 17L, missingClient), "not-found");

        Model missingFromStop = new ExtendedModelMap();
        when(routeStopRepository.findById(404L)).thenReturn(Optional.empty());
        assertEquals(controller.create(8L, 6L, 404L, 17L, missingFromStop), "not-found");

        Model missingToStop = new ExtendedModelMap();
        assertEquals(controller.create(8L, 6L, 16L, 404L, missingToStop), "not-found");

        Model missingPrice = new ExtendedModelMap();
        when(transportDao.getFare(5L, 16L, 17L)).thenReturn(Optional.empty());
        assertEquals(controller.create(7L, 6L, 16L, 17L, missingPrice), "orders/form");
        assertEquals(missingPrice.getAttribute("error"), "Для выбранной пары остановок нет цены");

        Model noSeats = new ExtendedModelMap();
        when(transportDao.getFare(5L, 16L, 17L)).thenReturn(Optional.of(BigDecimal.valueOf(199)));
        when(transportDao.getAvailableSeats(7L)).thenReturn(0L);
        assertEquals(controller.create(7L, 6L, 16L, 17L, noSeats), "orders/form");
        assertEquals(noSeats.getAttribute("error"), "Свободных мест нет");

        assertEquals(controller.pay(1L, null), "redirect:/orders/1/payment-success");
        verify(orderDao).markOrderAsPaid(1L);

        assertEquals(controller.pay(2L, "4000 0000 0000 0000"), "redirect:/orders/2/payment-failed");
        verify(orderDao).markOrderPaymentFailed(2L);

        Model failedModel = new ExtendedModelMap();
        when(orderDao.getOrderById(406L)).thenReturn(Optional.empty());
        assertEquals(controller.paymentFailed(406L, failedModel), "orders/payment-failed");
        assertNull(failedModel.getAttribute("order"));
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> castList(Object value) {
        return (List<T>) value;
    }

    private Client client(Long id, String fullName, String email, String phone) {
        Client client = new Client();
        client.setId(id);
        client.setFullName(fullName);
        client.setEmail(email);
        client.setPhone(phone);
        client.setAddress("Адрес");
        client.setIsDeleted(false);
        return client;
    }

    private Company company(Long id) {
        Company company = new Company();
        company.setId(id);
        company.setName("Компания");
        return company;
    }

    private Route route(Long id) {
        Route route = new Route();
        route.setId(id);
        route.setCompany(company(1L));
        route.setRouteNumber("R-" + id);
        route.setName("Маршрут");
        route.setIsActive(true);
        return route;
    }

    private Trip trip(Long id, Route route) {
        Trip trip = new Trip();
        trip.setId(id);
        trip.setRoute(route);
        trip.setCapacity(40);
        trip.setStatus("scheduled");
        return trip;
    }

    private RouteStop routeStop(Long id, int seq) {
        Stop stop = new Stop();
        stop.setId(id);
        stop.setCity("Город " + seq);
        stop.setName("Остановка " + seq);

        RouteStop routeStop = new RouteStop();
        routeStop.setId(id);
        routeStop.setRoute(route(5L));
        routeStop.setStop(stop);
        routeStop.setSeq(seq);
        return routeStop;
    }
}
