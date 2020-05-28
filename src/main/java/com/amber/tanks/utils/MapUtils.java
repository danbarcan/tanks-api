package com.amber.tanks.utils;

import com.amber.tanks.entities.Map;
import com.amber.tanks.entities.Obstacle;
import com.amber.tanks.entities.Orientation;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Service
public class MapUtils {
    private static Integer MAX_SIZE = 20;
    private static Integer MIN_SIZE = 8;

    private static final SecureRandom random = new SecureRandom();

    // generate a random map with random size and random obstacles
    public Map randomMap() {
        Map map = new Map();

        int size = random.nextInt(MAX_SIZE - MIN_SIZE) + MIN_SIZE;
        int obstacleNo = random.nextInt(size) + size;
        Set<Obstacle> obstacles = new HashSet<>();
        while (obstacles.size() < obstacleNo) {
            int x = random.nextInt(size);
            int y = random.nextInt(size);
            obstacles.add(Obstacle.builder().map(map).x(x).y(y).build());
        }

        map.setObstacles(obstacles);
        map.setSize(size);
        return map;
    }

    public Integer calculateDistanceBetweenPoints(Integer x1, Integer y1, Integer x2, Integer y2) {
        return (int) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    public Orientation calculateOrientation(Integer x1, Integer y1, Integer x2, Integer y2) {
        if (x1 < x2) {
            if (y1 < y2) {
                return Orientation.NORTH_EAST;
            } else if (y1 > y2) {
                return Orientation.SOUTH_EAST;
            } else {
                return Orientation.EAST;
            }
        } else if (x1 > x2) {
            if (y1 < y2) {
                return Orientation.NORTH_WEST;
            } else if (y1 > y2) {
                return Orientation.SOUTH_WEST;
            } else {
                return Orientation.WEST;
            }
        } else {
            if (y1 < y2) {
                return Orientation.NORTH;
            } else if (y1 > y2) {
                return Orientation.SOUTH;
            } else {
                return Orientation.CENTER;
            }
        }
    }
}
