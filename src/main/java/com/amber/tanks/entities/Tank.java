package com.amber.tanks.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.security.SecureRandom;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Tank {
    @Transient
    @JsonIgnore
    private SecureRandom random = new SecureRandom();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    private Integer hp;
    private Integer driveSprocket;
    private Integer track;
    private Integer viewport;
    private Integer frontArmor;
    private Integer sideArmor;
    private Integer turret;
    private Integer muzzle;
    private Integer barrelLength;

    @Transient
    private Integer x;
    @Transient
    private Integer y;

    @Transient
    private Orientation movementOrientation;

    @JsonIgnore
    public boolean isAlive() {
        return hp > 0;
    }

    public void takeHit() {
        hp--;
    }

    // speed of tank is determined by driveSprocket and track
    @JsonIgnore
    public Integer getSpeed() {
        return (driveSprocket + track) / 2;
    }

    // visibility range is determined by viewport
    @JsonIgnore
    public Integer getVisibilityDistance() {
        return 2 * viewport;
    }

    // shooting distance is determined by turret and barrel
    @JsonIgnore
    public Integer getShootingDistance() {
        return (turret + barrelLength) / 2;
    }

    // move according to tank orientation
    public void move(Integer maxDistance) {
        double rand = random.nextDouble();
        int distance = Math.min(getSpeed(), maxDistance);
        switch (movementOrientation) {
            case EAST:
                x += distance;
                break;
            case NORTH_EAST:
                x += (int)Math.round(distance * rand);
                y += (distance - (int)Math.round(distance * rand));
                break;
            case NORTH:
                y += distance;
                break;
            case NORTH_WEST:
                x -= (int)Math.round(distance * rand);
                y += (distance - (int)Math.round(distance * rand));
                break;
            case WEST:
                x -= distance;
                break;
            case SOUTH_WEST:
                x -= (int)Math.round(distance * rand);
                y -= (distance - (int)Math.round(distance * rand));
                break;
            case SOUTH:
                y -= distance;
                break;
            case SOUTH_EAST:
                x += (int)Math.round(distance * rand);
                y -= (distance - (int)Math.round(distance * rand));
                break;
            case CENTER:
            default:
                break;
        }
    }
}


