package ru.kabachok.abobus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kabachok.abobus.entity.TripStopTime;

import java.util.List;

public interface TripStopTimeRepository extends JpaRepository<TripStopTime, Long> {

    List<TripStopTime> findByTripIdOrderByRouteStopSeqAsc(Long tripId);
}
