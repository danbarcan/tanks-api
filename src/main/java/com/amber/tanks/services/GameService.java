package com.amber.tanks.services;

import com.amber.tanks.entities.Game;
import com.amber.tanks.entities.Round;
import com.amber.tanks.entities.Tank;
import com.amber.tanks.multithreading.BattleSimulationTask;
import com.amber.tanks.repositories.GameRepository;
import com.amber.tanks.repositories.MapRepository;
import com.amber.tanks.repositories.RoundRepository;
import com.amber.tanks.repositories.TankRepository;
import com.amber.tanks.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class GameService {

    private final TankRepository tankRepository;
    private final MapRepository mapRepository;
    private final RoundRepository roundRepository;
    private final GameRepository gameRepository;
    private final TankUtils tankUtils;
    private final MapUtils mapUtils;

    private final Integer threadCount = Runtime.getRuntime().availableProcessors();

    public ResponseEntity<List<Tank>> getAllTanks() {
        List<Tank> tanks = tankRepository.findAll();
        // if there are no tanks in DB create 10 random tanks
        if (tanks.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                Tank t = tankUtils.randomTank();
                tankRepository.save(t);
            }
            tanks = tankRepository.findAll();
        }
        return ResponseEntity.of(Optional.ofNullable(tanks));
    }

    public ResponseEntity<Game> getGameById(Long gameId) {
        return ResponseEntity.of(gameRepository.findById(gameId));
    }

    public ResponseEntity<Map<Integer, Game>> simulateBattle(Long tankId1, Long tankId2, Long noOfBattles) {
        // create thread pool and list of futures to get result of simulations
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<Game>> futuresList = new ArrayList<>();

        // start the simulations and get the futures
        for (int i = 0; i < noOfBattles; i++) {
            Callable<Game> task = new BattleSimulationTask(tankRepository, mapUtils, tankUtils, tankId1, tankId2);
            Future<Game> future = executorService.submit(task);
            futuresList.add(future);
        }

        // get the result from simulations and add to result
        Map<Integer, Game> games = new HashMap<>();
        for (int i = 0; i < futuresList.size(); i++) {
            Game game = null;
            try {
                game = futuresList.get(i).get(20, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            game.setRounds(game.getRounds().stream()
                    .sorted(Comparator.comparing(Round::getRoundNumber))
                    .collect(Collectors.toSet()));

            // save each simulation
            if (game != null) {
                saveGame(game);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            games.put(i, game);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(20, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
        }

        return ResponseEntity.ok(games);
    }

    public ResponseEntity<Game> simulateBattle(Long tankId1, Long tankId2) throws Exception {
        Optional<Tank> optionalTank1 = tankRepository.findById(tankId1);
        Optional<Tank> optionalTank2 = tankRepository.findById(tankId2);

        if (!optionalTank1.isPresent() || !optionalTank2.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // use call method from callable task used in multiple simulations to simulate only one battle
        Callable<Game> task = new BattleSimulationTask(tankRepository, mapUtils, tankUtils, tankId1, tankId2);
        Game game = task.call();

        if (game != null) {
            saveGame(game);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(game);
    }

    // save the entire game
    public void saveGame(Game game) {
        mapRepository.save(game.getMap());
        gameRepository.save(game);
        game.getRounds().forEach(roundRepository::save);
    }

}
