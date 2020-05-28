package com.amber.tanks.utils;

import com.amber.tanks.entities.Map;
import com.amber.tanks.entities.Obstacle;
import com.amber.tanks.entities.Orientation;
import com.amber.tanks.entities.Tank;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;

@Log
@Service
public class TankUtils {
    private static Integer MAX_VALUE_FOR_STAT = 3;
    private static Integer TOTAL_VALUE_FOR_STATS = 10;
    private static Integer NUMBER_OF_VARIABLE_STATS = 8;

    private static final SecureRandom random = new SecureRandom();

    // generate a random tank with total stats = 10
    public Tank randomTank() {
        int[] stats = new int[8];
        int currentTankStats = TOTAL_VALUE_FOR_STATS;
        Arrays.fill(stats, 1);
        while (currentTankStats > 0) {
            int n = random.nextInt(NUMBER_OF_VARIABLE_STATS);
            if (stats[n] < MAX_VALUE_FOR_STAT) {
                stats[n]++;
                currentTankStats--;
            }
        }
        return Tank.builder()
                .hp(MAX_VALUE_FOR_STAT) // a tank will die if gets hit 3 times
                .driveSprocket(stats[0])
                .track(stats[1])
                .viewport(stats[2])
                .frontArmor(stats[3])
                .sideArmor(stats[4])
                .turret(stats[5])
                .muzzle(stats[6])
                .barrelLength(stats[7])
                .build();
    }

    // set random starting position and orientation for a tank
    public Tank randomStartingPositionForTank(Tank tank, Tank secondTank, Map map) {
        tank.setMovementOrientation(Orientation.values()[random.nextInt(Orientation.values().length)]);

        boolean obstacleOnXY;
        int x, y;

        // do not start where is the enemy or where is an obstacle
        do {
            obstacleOnXY = false;
            x = random.nextInt(map.getSize());
            y = random.nextInt(map.getSize());

            for (Obstacle o : map.getObstacles()) {
                if (o.getX().equals(x) && o.getY().equals(y)) {
                    obstacleOnXY = true;
                }
            }
            if (secondTank != null && secondTank.getX().equals(x) && secondTank.getY().equals(y)) {
                obstacleOnXY = true;
            }
        } while (obstacleOnXY);

        tank.setX(x);
        tank.setY(y);

        return tank;
    }

    // calculate if a tank can harm the enemy based on attacking and defending stats and adding some chance there
    public boolean canFirstTankHarmSecondTank(Tank firstTank, Tank secondTank) {
        double firstTankPower = ((double) (firstTank.getMuzzle() + firstTank.getTurret() + firstTank.getBarrelLength())) / (3 * MAX_VALUE_FOR_STAT);
        double secondTankPower = ((double) (secondTank.getFrontArmor() + secondTank.getSideArmor())) / (2 * MAX_VALUE_FOR_STAT);

        return firstTankPower * 10 * random.nextDouble() > secondTankPower * 10 * random.nextDouble();
    }
}
