package ru.kabachok.abobus.repository;

import ru.kabachok.abobus.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("""
            select t
            from Trip t
            where t.route.id = :routeId
              and t.departureAt >= :from
              and t.departureAt < :to
            order by t.departureAt
            """)
    List<Trip> findTripsByRouteAndDay(
            @Param("routeId") Long routeId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );
}