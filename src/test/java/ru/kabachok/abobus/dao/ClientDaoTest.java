package ru.kabachok.abobus.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import ru.kabachok.abobus.entity.Client;
import ru.kabachok.abobus.entity.OrderEntity;
import ru.kabachok.abobus.support.BaseIntegrationTest;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

@Transactional
public class ClientDaoTest extends BaseIntegrationTest {

    @Autowired
    private ClientDao clientDao;

    @Test
    public void shouldReturnOnlyActiveClients() {
        List<Client> clients = clientDao.getAllActiveClients();

        assertNotNull(clients);
        assertEquals(clients.size(), 6);
        assertTrue(clients.stream().allMatch(client -> !client.getIsDeleted()));
    }

    @Test
    public void shouldReturnClientByIdWhenExistsAndNotDeleted() {
        Optional<Client> clientOpt = clientDao.getClientById(1L);

        assertTrue(clientOpt.isPresent());

        Client client = clientOpt.get();
        assertEquals(client.getId(), Long.valueOf(1L));
        assertEquals(client.getFullName(), "Салат Оливье Петрович");
        assertEquals(client.getEmail(), "olivie@food.example");
        assertEquals(client.getPhone(), "+7-900-100-00-01");
        assertEquals(client.getAddress(), "Блиноград ул Огуречная 1");
        assertFalse(client.getIsDeleted());
        assertNotNull(client.getCreatedAt());
    }

    @Test
    public void shouldReturnEmptyWhenClientNotFound() {
        Optional<Client> clientOpt = clientDao.getClientById(999L);

        assertTrue(clientOpt.isEmpty());
    }

    @Test
    public void shouldUpdateClientWhenExists() {
        Client updated = new Client();
        updated.setFullName("Новое имя клиента");
        updated.setEmail("updated@example.com");
        updated.setPhone("+7-999-111-22-33");
        updated.setAddress("Новый адрес клиента");

        Optional<Client> updatedOpt = clientDao.updateClient(1L, updated);

        assertTrue(updatedOpt.isPresent());

        Client client = updatedOpt.get();
        assertEquals(client.getId(), Long.valueOf(1L));
        assertEquals(client.getFullName(), "Новое имя клиента");
        assertEquals(client.getEmail(), "updated@example.com");
        assertEquals(client.getPhone(), "+7-999-111-22-33");
        assertEquals(client.getAddress(), "Новый адрес клиента");
        assertFalse(client.getIsDeleted());
        assertNotNull(client.getCreatedAt());
    }

    @Test
    public void shouldReturnEmptyWhenUpdateMissingClient() {
        Client updated = new Client();
        updated.setFullName("Ghost");
        updated.setEmail("ghost@example.com");
        updated.setPhone("+7-000-000-00-00");
        updated.setAddress("Nowhere");

        Optional<Client> updatedOpt = clientDao.updateClient(999L, updated);

        assertTrue(updatedOpt.isEmpty());
    }

    @Test
    public void shouldSoftDeleteClientWhenExists() {
        boolean deleted = clientDao.softDeleteClient(2L);

        assertTrue(deleted);

        Optional<Client> clientOpt = clientDao.getClientById(2L);
        assertTrue(clientOpt.isEmpty());

        List<Client> clients = clientDao.getAllActiveClients();
        assertEquals(clients.size(), 5);
        assertTrue(clients.stream().noneMatch(client -> client.getId().equals(2L)));
    }

    @Test
    public void shouldReturnFalseWhenSoftDeleteMissingClient() {
        boolean deleted = clientDao.softDeleteClient(999L);

        assertFalse(deleted);
    }

    @Test
    public void shouldReturnClientOrderHistory() {
        List<OrderEntity> orders = clientDao.getClientOrderHistory(1L);

        assertNotNull(orders);
        assertEquals(orders.size(), 2);
        assertTrue(orders.stream().allMatch(order -> order.getClient().getId().equals(1L)));

        OrderEntity first = orders.get(0);
        OrderEntity second = orders.get(1);

        assertNotNull(first.getId());
        assertNotNull(first.getStatus());
        assertNotNull(first.getPaymentStatus());
        assertNotNull(first.getCreatedAt());

        assertNotNull(second.getId());
        assertNotNull(second.getStatus());
        assertNotNull(second.getPaymentStatus());
        assertNotNull(second.getCreatedAt());

        assertTrue(
                first.getCreatedAt().isAfter(second.getCreatedAt())
                        || first.getCreatedAt().isEqual(second.getCreatedAt())
        );
    }

    @Test
    public void shouldReturnThreeOrdersForSecondClient() {
        List<OrderEntity> orders = clientDao.getClientOrderHistory(2L);

        assertNotNull(orders);
        assertEquals(orders.size(), 3);
        assertTrue(orders.stream().allMatch(order -> order.getClient().getId().equals(2L)));
    }

    @Test
    public void shouldReturnEmptyHistoryForUnknownClient() {
        List<OrderEntity> orders = clientDao.getClientOrderHistory(999L);

        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void shouldCreateClient() {
        Client client = new Client();
        client.setFullName("Новый клиент");
        client.setEmail("newclient@example.com");
        client.setPhone("+7-999-123-45-67");
        client.setAddress("Тестовый адрес");

        Client saved = clientDao.createClient(client);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(saved.getFullName(), "Новый клиент");
        assertEquals(saved.getEmail(), "newclient@example.com");
        assertEquals(saved.getPhone(), "+7-999-123-45-67");
        assertEquals(saved.getAddress(), "Тестовый адрес");
        assertFalse(saved.getIsDeleted());
        assertNotNull(saved.getCreatedAt());

        Optional<Client> fromDbOpt = clientDao.getClientById(saved.getId());
        assertTrue(fromDbOpt.isPresent());

        Client fromDb = fromDbOpt.get();
        assertEquals(fromDb.getId(), saved.getId());
        assertEquals(fromDb.getFullName(), "Новый клиент");
        assertEquals(fromDb.getEmail(), "newclient@example.com");
        assertEquals(fromDb.getPhone(), "+7-999-123-45-67");
        assertEquals(fromDb.getAddress(), "Тестовый адрес");
        assertFalse(fromDb.getIsDeleted());
        assertNotNull(fromDb.getCreatedAt());
    }
}