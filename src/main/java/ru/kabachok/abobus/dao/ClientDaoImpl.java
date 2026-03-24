package ru.kabachok.abobus.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kabachok.abobus.entity.Client;
import ru.kabachok.abobus.entity.OrderEntity;
import ru.kabachok.abobus.repository.ClientRepository;
import ru.kabachok.abobus.repository.OrderRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class ClientDaoImpl implements ClientDao {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Client> getAllActiveClients() {
        return clientRepository.findByIsDeletedFalseOrderByFullNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> getClientById(Long clientId) {
        return clientRepository.findByIdAndIsDeletedFalse(clientId);
    }

    @Override
    public Client createClient(Client client) {
        client.setId(null);
        client.setIsDeleted(false);

        if (client.getCreatedAt() == null) {
            client.setCreatedAt(OffsetDateTime.now());
        }

        return clientRepository.save(client);
    }

    @Override
    public Optional<Client> updateClient(Long clientId, Client updatedClient) {
        Optional<Client> optionalClient = clientRepository.findByIdAndIsDeletedFalse(clientId);
        if (optionalClient.isEmpty()) {
            return Optional.empty();
        }

        Client client = optionalClient.get();
        client.setFullName(updatedClient.getFullName());
        client.setEmail(updatedClient.getEmail());
        client.setPhone(updatedClient.getPhone());
        client.setAddress(updatedClient.getAddress());

        return Optional.of(clientRepository.save(client));
    }

    @Override
    public boolean softDeleteClient(Long clientId) {
        Optional<Client> optionalClient = clientRepository.findByIdAndIsDeletedFalse(clientId);
        if (optionalClient.isEmpty()) {
            return false;
        }

        Client client = optionalClient.get();
        client.setIsDeleted(true);
        clientRepository.save(client);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> getClientOrderHistory(Long clientId) {
        return orderRepository.findByClientIdOrderByCreatedAtDesc(clientId);
    }
}