package ru.kabachok.abobus.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "route_stop",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_route_stop_seq", columnNames = {"route_id", "seq"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private Integer seq;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stop_id", nullable = false)
    private Stop stop;

    @Column(name = "dwell_min")
    private Integer dwellMin;
}