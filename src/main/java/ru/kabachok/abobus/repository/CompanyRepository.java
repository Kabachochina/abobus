package ru.kabachok.abobus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kabachok.abobus.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
