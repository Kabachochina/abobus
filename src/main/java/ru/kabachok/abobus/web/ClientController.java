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
import ru.kabachok.abobus.dao.ClientDao;
import ru.kabachok.abobus.entity.Client;
import ru.kabachok.abobus.entity.OrderEntity;

import java.util.List;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientDao clientDao;

    @GetMapping
    public String list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String contact,
                       @RequestParam(required = false) Long tripId,
                       Model model) {
        List<Client> clients = clientDao.getAllActiveClients().stream()
                .filter(client -> contains(client.getFullName(), name))
                .filter(client -> contact == null || contact.isBlank()
                        || contains(client.getEmail(), contact)
                        || contains(client.getPhone(), contact))
                .filter(client -> tripId == null || clientDao.getClientOrderHistory(client.getId()).stream()
                        .anyMatch(order -> order.getTrip().getId().equals(tripId)))
                .toList();

        model.addAttribute("clients", clients);
        model.addAttribute("name", name);
        model.addAttribute("contact", contact);
        model.addAttribute("tripId", tripId);
        return "clients/list";
    }

    @GetMapping("/new")
    public String newClient(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("action", "/clients");
        return "clients/form";
    }

    @PostMapping
    public String create(@ModelAttribute Client client, Model model) {
        if (client.getFullName() == null || client.getFullName().isBlank()) {
            model.addAttribute("error", "ФИО обязательно");
            model.addAttribute("client", client);
            model.addAttribute("action", "/clients");
            return "clients/form";
        }
        Client saved = clientDao.createClient(client);
        return "redirect:/clients/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Client client = clientDao.getClientById(id).orElse(null);
        if (client == null) {
            model.addAttribute("message", "Клиент не найден");
            return "not-found";
        }
        model.addAttribute("client", client);
        return "clients/details";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Client client = clientDao.getClientById(id).orElse(null);
        if (client == null) {
            model.addAttribute("message", "Клиент не найден");
            return "not-found";
        }
        model.addAttribute("client", client);
        model.addAttribute("action", "/clients/" + id);
        return "clients/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Client client, Model model) {
        if (client.getFullName() == null || client.getFullName().isBlank()) {
            client.setId(id);
            model.addAttribute("error", "ФИО обязательно");
            model.addAttribute("client", client);
            model.addAttribute("action", "/clients/" + id);
            return "clients/form";
        }
        return clientDao.updateClient(id, client)
                .map(updated -> "redirect:/clients/" + updated.getId())
                .orElseGet(() -> {
                    model.addAttribute("message", "Клиент не найден");
                    return "not-found";
                });
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        clientDao.softDeleteClient(id);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/orders")
    public String orders(@PathVariable Long id, Model model) {
        Client client = clientDao.getClientById(id).orElse(null);
        if (client == null) {
            model.addAttribute("message", "Клиент не найден");
            return "not-found";
        }
        List<OrderEntity> orders = clientDao.getClientOrderHistory(id);
        model.addAttribute("client", client);
        model.addAttribute("orders", orders);
        return "clients/orders";
    }

    private boolean contains(String value, String part) {
        return part == null || part.isBlank()
                || value != null && value.toLowerCase().contains(part.toLowerCase());
    }
}
