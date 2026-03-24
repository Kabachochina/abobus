package ru.kabachok.abobus.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String city;

    @Column
    private String address;
}