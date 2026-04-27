package ru.kabachok.abobus.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kabachok.abobus.dao.TransportDao;
import ru.kabachok.abobus.entity.Company;
import ru.kabachok.abobus.entity.Route;
import ru.kabachok.abobus.entity.RouteStop;
import ru.kabachok.abobus.entity.Trip;
import ru.kabachok.abobus.repository.CompanyRepository;
import ru.kabachok.abobus.repository.TripRepository;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final TransportDao transportDao;
    private final CompanyRepository companyRepository;
    private final TripRepository tripRepository;

    @GetMapping
    public String list(@RequestParam(required = false) String number,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String date,
                       Model model) {
        List<Route> routes = transportDao.searchActiveRoutes(number, name).stream()
                .filter(route -> hasTripOnDate(route, date))
                .toList();
        model.addAttribute("routes", routes);
        model.addAttribute("number", number);
        model.addAttribute("name", name);
        model.addAttribute("date", date);
        return "routes/list";
    }

    @GetMapping("/new")
    public String newRoute(Model model) {
        model.addAttribute("route", new Route());
        model.addAttribute("companies", companyRepository.findAll());
        model.addAttribute("action", "/routes");
        return "routes/form";
    }

    @PostMapping
    public String create(@RequestParam Long companyId, @ModelAttribute Route route, Model model) {
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null || route.getRouteNumber() == null || route.getRouteNumber().isBlank()) {
            model.addAttribute("error", "Компания и номер рейса обязательны");
            model.addAttribute("route", route);
            model.addAttribute("companies", companyRepository.findAll());
            model.addAttribute("action", "/routes");
            return "routes/form";
        }
        route.setCompany(company);
        Route saved = transportDao.createRoute(route);
        return "redirect:/routes/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Route route = transportDao.getRouteById(id).orElse(null);
        if (route == null) {
            model.addAttribute("message", "Рейс не найден");
            return "not-found";
        }
        List<RouteStop> stops = transportDao.getOrderedStopsForRoute(id);
        List<Trip> trips = tripRepository.findAll().stream()
                .filter(trip -> trip.getRoute().getId().equals(id))
                .toList();
        model.addAttribute("route", route);
        model.addAttribute("stops", stops);
        model.addAttribute("trips", trips);
        return "routes/details";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Route route = transportDao.getRouteById(id).orElse(null);
        if (route == null) {
            model.addAttribute("message", "Рейс не найден");
            return "not-found";
        }
        model.addAttribute("route", route);
        model.addAttribute("companies", companyRepository.findAll());
        model.addAttribute("action", "/routes/" + id);
        return "routes/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam Long companyId,
                         @ModelAttribute Route route,
                         Model model) {
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null || route.getRouteNumber() == null || route.getRouteNumber().isBlank()) {
            route.setId(id);
            model.addAttribute("error", "Компания и номер рейса обязательны");
            model.addAttribute("route", route);
            model.addAttribute("companies", companyRepository.findAll());
            model.addAttribute("action", "/routes/" + id);
            return "routes/form";
        }
        route.setCompany(company);
        return transportDao.updateRoute(id, route)
                .map(updated -> "redirect:/routes/" + updated.getId())
                .orElseGet(() -> {
                    model.addAttribute("message", "Рейс не найден");
                    return "not-found";
                });
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        transportDao.deactivateRoute(id);
        return "redirect:/routes";
    }

    private boolean hasTripOnDate(Route route, String date) {
        if (date == null || date.isBlank()) {
            return true;
        }
        try {
            return !transportDao.getTripsForRouteOnDate(route.getId(), LocalDate.parse(date)).isEmpty();
        } catch (RuntimeException ignored) {
            return true;
        }
    }
}
