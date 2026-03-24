package ru.kabachok.abobus.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "trip_stop_time",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_trip_stop_time", columnNames = {"trip_id", "route_stop_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripStopTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_stop_id", nullable = false)
    private RouteStop routeStop;

    @Column(name = "arrival_at")
    private OffsetDateTime arrivalAt;

    @Column(name = "departure_at")
    private OffsetDateTime departureAt;
}