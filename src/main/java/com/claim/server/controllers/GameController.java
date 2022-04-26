package com.claim.server.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.claim.database.GameRepository;
import com.claim.model.Game;

/** Provides game-related endpoints.
 * @author Deborah Vanzin
*/
@RestController
public class GameController {
	
	  @Autowired
	  public GameRepository repository;

	
	  /**
	   * Represents the endpoint to list all open games
	   */
	  @GetMapping(path = "/games/open", produces = "application/json")
	  public List<Game> getListOfOpenGames() {
		  return repository.findByPhase(0);
	  }
	  /*
	  @GetMapping(path = "/games", produces = "application/json")
	  public List<Game> getListOfGamesForAccount(@RequestParam int accountId){
		  return null;
	  }*/
	  
	  /**
	   * Lists all games
	   */
	  @GetMapping(path = "/games", produces = "application/json")
	  public List<Game> listAllGames() {
		  return repository.findAll();
	  }
	  
	  /**
	   * Create a new game with the data passed in the request body
	   */
	  @PostMapping(path = "/games", produces = "application/json")
	  public ResponseEntity<Game> createNewGame(@RequestBody Game game) {
		  repository.save(game);
		  Game response = repository.findById(game.getId()).get();
		  return ResponseEntity.status(HttpStatus.CREATED).body(response);
	  }
	  
	  /**
	   * Get a game with a speficic id
	   */
	  @GetMapping(path = "/games/{id}", produces = "application/json")
	  public ResponseEntity<Game> readGame(@PathVariable("id") Integer id) {
		  Optional<Game>  optional = repository.findById(id);
		  if (optional.isPresent()) {
			  return ResponseEntity.ok(optional.get());
		  }
		  return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	  }
	  
	  /**
	   * To join a game with an id (playerId will be removed later)
	   */
	  @PostMapping(path = "/games/{gameId}/join/{playerId}", produces = "application/json")
	  public ResponseEntity<Game> joinGame(@PathVariable("gameId") Integer gameId, @PathVariable("playerId") Integer playerId) {
		  Game gameToJoin = repository.findById(gameId).get();
		  // 1. Retrieve user id from token (over UserDetails)
		  // 2. Check if game has already one player, then add me as second player
		  if(gameToJoin.getPlayerA() <= 0) {
			  gameToJoin.setPlayerA(playerId);
		  }
		  else if(gameToJoin.getPlayerB() <= 0) {
			  gameToJoin.setPlayerB(playerId);
			  gameToJoin.setPhase(1);
		  }
		  else {
			  return ResponseEntity.status(HttpStatus.CONFLICT).build();
		  }
		  repository.save(gameToJoin);
		  return ResponseEntity.ok(repository.findById(gameId).get());
	  }
}
