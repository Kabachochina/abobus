package ru.kabachok.abobus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kabachok.abobus.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByIdAndIsDeletedFalse(Long id);

    Optional<Client> findByEmailAndIsDeletedFalse(String email);

    List<Client> findByIsDeletedFalseOrderByFullNameAsc();
}