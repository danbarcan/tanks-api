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
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "first_tank_id")
    private Tank firstTank;

    @ManyToOne
    @JoinColumn(name = "second_tank_id")
    private Tank secondTank;

    @OneToOne
    @JoinColumn(name = "map_id")
    private Map map;

    @OneToMany(mappedBy = "game", cascade = CascadeType.REMOVE)
    private Set<Round> rounds;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Tank winner;
}
