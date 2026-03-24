package ru.kabachok.abobus.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "route_fare",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_route_fare",
                        columnNames = {"route_id", "from_route_stop_id", "to_route_stop_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteFare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_route_stop_id", nullable = false)
    private RouteStop fromRouteStop;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_route_stop_id", nullable = false)
    private RouteStop toRouteStop;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}