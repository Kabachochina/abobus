package ru.kabachok.abobus.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kabachok.abobus.dao.ClientDao;
import ru.kabachok.abobus.dao.OrderDao;
import ru.kabachok.abobus.dao.TransportDao;
import ru.kabachok.abobus.entity.Client;
import ru.kabachok.abobus.entity.OrderEntity;
import ru.kabachok.abobus.entity.RouteStop;
import ru.kabachok.abobus.entity.Trip;
import ru.kabachok.abobus.repository.RouteStopRepository;
import ru.kabachok.abobus.repository.TripRepository;
import ru.kabachok.abobus.repository.TripStopTimeRepository;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderDao orderDao;
    private final ClientDao clientDao;
    private final TransportDao transportDao;
    private final TripRepository tripRepository;
    private final RouteStopRepository routeStopRepository;
    private final TripStopTimeRepository tripStopTimeRepository;

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        OrderEntity order = orderDao.getOrderById(id).orElse(null);
        if (order == null) {
            model.addAttribute("message", "Заказ не найден");
            return "not-found";
        }
        model.addAttribute("order", order);
        return "orders/details";
    }

    @GetMapping("/{id}/cancel")
    public String cancelForm(@PathVariable Long id, Model model) {
        OrderEntity order = orderDao.getOrderById(id).orElse(null);
        if (order == null) {
            model.addAttribute("message", "Заказ не найден");
            return "not-found";
        }
        model.addAttribute("order", order);
        return "orders/cancel";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, @RequestParam(defaultValue = "Отмена клиентом") String reason) {
        orderDao.cancelOrder(id, reason);
        return "redirect:/orders/" + id;
    }

    @GetMapping("/new")
    public String newOrder(@RequestParam Long tripId, Model model) {
        Trip trip = tripRepository.findById(tripId).orElse(null);
        if (trip == null) {
            model.addAttribute("message", "Поездка не найдена");
            return "not-found";
        }
        fillOrderForm(model, trip, null);
        return "orders/form";
    }

    @PostMapping
    public String create(@RequestParam Long tripId,
                         @RequestParam Long clientId,
                         @RequestParam Long fromRouteStopId,
                         @RequestParam Long toRouteStopId,
                         Model model) {
        Trip trip = tripRepository.findById(tripId).orElse(null);
        Client client = clientDao.getClientById(clientId).orElse(null);
        RouteStop from = routeStopRepository.findById(fromRouteStopId).orElse(null);
        RouteStop to = routeStopRepository.findById(toRouteStopId).orElse(null);
        if (trip == null || client == null || from == null || to == null) {
            model.addAttribute("message", "Нельзя создать заказ: не найдены данные формы");
            return "not-found";
        }

        if (from.getSeq() >= to.getSeq()) {
            fillOrderForm(model, trip, "Пункт отправления должен быть раньше пункта прибытия");
            return "orders/form";
        }
        BigDecimal price = transportDao.getFare(trip.getRoute().getId(), from.getId(), to.getId()).orElse(null);
        if (price == null) {
            fillOrderForm(model, trip, "Для выбранной пары остановок нет цены");
            return "orders/form";
        }
        if (transportDao.getAvailableSeats(tripId) <= 0) {
            fillOrderForm(model, trip, "Свободных мест нет");
            return "orders/form";
        }

        OrderEntity order = new OrderEntity();
        order.setTrip(trip);
        order.setClient(client);
        order.setFromRouteStop(from);
        order.setToRouteStop(to);
        order.setPrice(price);

        OrderEntity saved = orderDao.createOrder(order);
        return "redirect:/orders/" + saved.getId() + "/payment";
    }

    @GetMapping("/{id}/payment")
    public String payment(@PathVariable Long id, Model model) {
        OrderEntity order = orderDao.getOrderById(id).orElse(null);
        if (order == null) {
            model.addAttribute("message", "Заказ не найден");
            return "not-found";
        }
        model.addAttribute("order", order);
        return "orders/payment";
    }

    @PostMapping("/{id}/payment")
    public String pay(@PathVariable Long id, @RequestParam String cardNumber) {
        if (cardNumber != null && cardNumber.replace(" ", "").endsWith("0000")) {
            orderDao.markOrderPaymentFailed(id);
            return "redirect:/orders/" + id + "/payment-failed";
        }
        orderDao.markOrderAsPaid(id);
        return "redirect:/orders/" + id + "/payment-success";
    }

    @GetMapping("/{id}/payment-success")
    public String paymentSuccess(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderDao.getOrderById(id).orElse(null));
        return "orders/payment-success";
    }

    @GetMapping("/{id}/payment-failed")
    public String paymentFailed(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderDao.getOrderById(id).orElse(null));
        return "orders/payment-failed";
    }

    private void fillOrderForm(Model model, Trip trip, String error) {
        List<RouteStop> stops = transportDao.getOrderedStopsForRoute(trip.getRoute().getId());
        model.addAttribute("trip", trip);
        model.addAttribute("stops", stops);
        model.addAttribute("clients", clientDao.getAllActiveClients());
        model.addAttribute("times", tripStopTimeRepository.findByTripIdOrderByRouteStopSeqAsc(trip.getId()));
        model.addAttribute("availableSeats", transportDao.getAvailableSeats(trip.getId()));
        model.addAttribute("error", error);
    }
}
