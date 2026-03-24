package ru.kabachok.abobus.dao;

import ru.kabachok.abobus.entity.Client;
import ru.kabachok.abobus.entity.OrderEntity;

import java.util.List;
import java.util.Optional;

public interface ClientDao {

    List<Client> getAllActiveClients();

    Optional<Client> getClientById(Long clientId);

    Client createClient(Client client);

    Optional<Client> updateClient(Long clientId, Client updatedClient);

    boolean softDeleteClient(Long clientId);

    List<OrderEntity> getClientOrderHistory(Long clientId);
}