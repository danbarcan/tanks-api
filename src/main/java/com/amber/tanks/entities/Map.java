package com.amber.tanks.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer size;

    @OneToMany(mappedBy = "map", cascade = CascadeType.REMOVE)
    private Set<Obstacle> obstacles;
}
