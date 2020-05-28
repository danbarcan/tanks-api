package com.amber.tanks.repositories;

import com.amber.tanks.entities.Obstacle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObstacleRepository extends JpaRepository<Obstacle, Long> {
}
