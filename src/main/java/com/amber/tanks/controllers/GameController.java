package com.amber.tanks.controllers;

import com.amber.tanks.entities.Game;
import com.amber.tanks.entities.Tank;
import com.amber.tanks.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class GameController {

    private final GameService gameService;

    @GetMapping(path = "/tanks")
    public ResponseEntity<List<Tank>> getAllTanks() {
        return gameService.getAllTanks();
    }

    @GetMapping(path = "/tanks/battles/{tank1}/{tank2}/{noOfBattles}")
    public ResponseEntity<Map<Integer, Game>> simulateBattle(@PathVariable("tank1") Long tankId1, @PathVariable("tank2") Long tankId2, @PathVariable("noOfBattles") Long noOfBattles) {
        return gameService.simulateBattle(tankId1, tankId2, noOfBattles);
    }

    @GetMapping(path = "/tanks/battles/{tank1}/{tank2}")
    public ResponseEntity<Game> simulateBattle(@PathVariable("tank1") Long tankId1, @PathVariable("tank2") Long tankId2) throws Exception {
        return gameService.simulateBattle(tankId1, tankId2);
    }

    @GetMapping(path = "/tanks/battles/{battleId}")
    public ResponseEntity<Game> getBattle(@PathVariable("battleId") Long battleId) {
        return gameService.getGameById(battleId);
    }
}
