package com.amber.tanks.multithreading;

import com.amber.tanks.entities.*;
import com.amber.tanks.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

@Log
@RequiredArgsConstructor
public class BattleSimulationTask implements Callable<Game> {
    private final Optional<Tank> optionalTank1;
    private final Optional<Tank> optionalTank2;
    private final MapUtils mapUtils;
    private final TankUtils tankUtils;

    private final SecureRandom random = new SecureRandom();

    @Override
    public Game call() {
        // for each simulation create a random map
        log.info("Starting thread");
        Map map = mapUtils.randomMap();

        log.info("Map generated");

        Tank firstTank;
        Tank secondTank;

        synchronized (tankUtils) {
            // choose who moves first
            if (random.nextBoolean()) {
                firstTank = tankUtils.randomStartingPositionForTank(optionalTank1.get(), null, map);
                secondTank = tankUtils.randomStartingPositionForTank(optionalTank2.get(), optionalTank1.get(), map);
            } else {
                firstTank = tankUtils.randomStartingPositionForTank(optionalTank2.get(), null, map);
                secondTank = tankUtils.randomStartingPositionForTank(optionalTank1.get(), optionalTank2.get(), map);
            }
        }

        log.info("First goes: " + firstTank.getId());

        Game game = Game.builder()
                .firstTank(firstTank)
                .secondTank(secondTank)
                .map(map)
                .build();

        Set<Round> rounds = new HashSet<>();
        int roundNumber = 0;

        // play until one tank is dead or max 50 rounds
        while (firstTank.isAlive() && secondTank.isAlive() && roundNumber < 50) {
            log.info("round: " + roundNumber);
            Round round = Round.builder()
                    .roundNumber(roundNumber++)
                    .firstTankHP(firstTank.getHp())
                    .firstTankX(firstTank.getX())
                    .firstTankY(firstTank.getY())
                    .firstTankMovementOrientation(firstTank.getMovementOrientation())
                    .secondTankHP(secondTank.getHp())
                    .secondTankX(secondTank.getX())
                    .secondTankY(secondTank.getY())
                    .secondTankMovementOrientation(secondTank.getMovementOrientation())
                    .game(game)
                    .build();

            rounds.add(round);

            synchronized (firstTank) {
                calculateMoves(map, firstTank, secondTank);
            }
        }

        if (roundNumber < 50) {
            game.setWinner(secondTank.isAlive() ? secondTank : firstTank);
        }

        game.setRounds(rounds);

        log.info("Ending thread");

        return game;
    }

    private void calculateMoves(Map map, Tank firstTank, Tank secondTank) {
        // need to know distance and orientation between tanks to calculate next move
        int distanceBetweenTanks = mapUtils.calculateDistanceBetweenPoints(firstTank.getX(), firstTank.getY(), secondTank.getX(), secondTank.getY());
        Orientation orientationBetweenTank1AndTank2 = mapUtils.calculateOrientation(firstTank.getX(), firstTank.getY(), secondTank.getX(), secondTank.getY());
        Orientation orientationBetweenTank2AndTank1 = mapUtils.calculateOrientation(secondTank.getX(), secondTank.getY(), firstTank.getX(), firstTank.getY());

        calculateMovesForOneTank(map, firstTank, secondTank, distanceBetweenTanks, orientationBetweenTank1AndTank2);
        if (secondTank.isAlive()) {
            calculateMovesForOneTank(map, secondTank, firstTank, distanceBetweenTanks, orientationBetweenTank2AndTank1);
        }
    }

    private void calculateMovesForOneTank(Map map, Tank playingTank, Tank waitingTank, int distanceBetweenTanks, Orientation orientationBetweenTanks) {
        if (distanceBetweenTanks <= playingTank.getVisibilityDistance()) {
            if (distanceBetweenTanks <= playingTank.getShootingDistance()) {
                // try hit the enemy if is in shooting range
                if (tankUtils.canFirstTankHarmSecondTank(playingTank, waitingTank)) {
                    waitingTank.takeHit();
                }
            } else {
                if (orientationBetweenTanks.equals(playingTank.getMovementOrientation())) {
                    // move towards enemy if enemy is visible an the tank is well oriented
                    playingTank.move(distanceBetweenTanks);
                } else {
                    // change orientation towards the enemy
                    playingTank.setMovementOrientation(orientationBetweenTanks);
                }
            }
        } else {
            // if enemy is not visible move to center or change orientation towards center
            Orientation orientationTankToCenter = mapUtils.calculateOrientation(playingTank.getX(), playingTank.getY(), map.getSize() / 2, map.getSize() / 2);
            if (orientationTankToCenter.equals(playingTank.getMovementOrientation())) {
                playingTank.move(playingTank.getSpeed());
            } else {
                playingTank.setMovementOrientation(orientationTankToCenter);
            }
        }
    }
}
