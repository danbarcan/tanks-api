package com.amber.tanks.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer roundNumber;

    private Integer firstTankHP;
    private Integer firstTankX;
    private Integer firstTankY;
    private Orientation firstTankShootingOrientation;
    private Orientation firstTankMovementOrientation;

    private Integer secondTankHP;
    private Integer secondTankX;
    private Integer secondTankY;
    private Orientation secondTankShootingOrientation;
    private Orientation secondTankMovementOrientation;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;
}
