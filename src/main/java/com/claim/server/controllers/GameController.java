package com.claim.server.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.claim.database.GameRepository;
import com.claim.model.Game;
import com.claim.service.GameService;

/** Provides game-related endpoints.
 * @author Deborah Vanzin
*/
@RestController
public class GameController {
	
	  @Autowired
	  public GameRepository repository;
	  @Autowired
	  private GameService gameService;

	
	  /**
	   * Represents the endpoint to list of all open games
	   */
	  @GetMapping(path = "/games/open", produces = "application/json")
	  public List<Game> getListOfOpenGames() {
		  return repository.findByPhase(0);
	  }
	  
	  /**
	   * Lists all games
	   */
	  @GetMapping(path = "/games", produces = "application/json")
	  public List<Game> listAllGames() {
		  return repository.findAll();
	  }
	  
	  /**
	   * Create a new game with the data passed in the request body
	   * @throws URISyntaxException 
	   */
	  @GetMapping(path = "/games/{playerId}")
	  public ResponseEntity<Game> createNewGame(@PathVariable Integer playerId) throws URISyntaxException {
		  Game game = gameService.initializeGame(playerId);
		  // Create an URI which identifies the resource created
		  URI location = new URI("http://localhost:8080/games/"+ game.getId());
		  return ResponseEntity.created(location).build();
	  }
	  
	  /**
	   * Get a game with a specific id
	   */
	  @GetMapping(path = "/games/{id}", produces = "application/json")
	  public ResponseEntity<Game> readGame(@PathVariable Integer id) {
		  Optional<Game>  optional = repository.findById(id);
		  if (optional.isPresent()) {
			  return ResponseEntity.ok(optional.get());
		  }
		  return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	  }
	  
	  /**WIRD GELÖSCHT
	   * To enable to see the played cards of each round per player
	   * @author: Rocco Saracino
	   */
	  @GetMapping (path = "/makeMove/{gameId}/{playerA}/{playerB}/{cardIdA}/{cardIdB}", produces = "application/jason")
	  public ResponseEntity<Game> makeMove(@PathVariable("gameId") Integer gameId, 
			  @PathVariable("playerA") Integer playerA, 
			  @PathVariable("playerB") Integer playerB,
			  @PathVariable("cardIdA") Integer cardIdA,
			  @PathVariable("cardIdB") Integer cardIdB) {
		
		  gameService.makeMovePlayerA(gameId, playerA, cardIdA);
		  gameService.makeMovePlayerB(gameId, playerB, cardIdB);
		  
		  return ResponseEntity.status(HttpStatus.CREATED).build();
	  }
	  
	  // Phase1 
	  @GetMapping (path = "/phase1/{gameId}/{playerA}/{playerB}/{cardIdA}/{cardIdB}",
			  produces = "application/jason")
	  public ResponseEntity<Game> phase1(@PathVariable("gameId") Integer gameId,
			  @PathVariable("playerA") Integer playerA, 
			  @PathVariable("playerB") Integer playerB,
			  @PathVariable("cardIdA") Integer cardIdA,
			  @PathVariable("cardIdB") Integer cardIdB) {
		  
		  gameService.phase1(gameId, playerA, playerB, cardIdA, cardIdB);
		  
		  return ResponseEntity.status(HttpStatus.CREATED).build();
		  
	  }
	  
	// Phase2
	  @GetMapping (path = "/phase2/{gameId}/{playerA}/{playerB}/{cardIdA}/{cardIdB}",
				  produces = "application/jason")
		  public ResponseEntity<Game> phase2(@PathVariable("gameId") Integer gameId,
				  @PathVariable("playerA") Integer playerA, 
				  @PathVariable("playerB") Integer playerB,
				  @PathVariable("cardIdA") Integer cardIdA,
				  @PathVariable("cardIdB") Integer cardIdB) {
			  
			  gameService.phase2(gameId, playerA, playerB, cardIdA, cardIdB);
			  
			  return ResponseEntity.status(HttpStatus.CREATED).build();
			  
		  }
	  
	  // Ranking
	  @GetMapping (path = "/calcScore/{playerA}/{playerB}",
			  produces = "application/jason")
	  public ResponseEntity<Game> calcScore(
			  @PathVariable("playerA") Integer playerA, 
			  @PathVariable("playerB") Integer playerB)
			  {
		  
		  gameService.calcScore(playerA, playerB);
		  
		  return ResponseEntity.status(HttpStatus.CREATED).build();
		  
	  }
	  
	 
	  /**
	   * To join a game with an id (playerId will be removed later)
	   */
		/*
		 * @PostMapping(path = "/games/{gameId}/join/{playerId}", produces =
		 * "application/json") public ResponseEntity<Game>
		 * joinGame(@PathVariable("gameId") Integer gameId, @PathVariable("playerId")
		 * Integer playerId) { gameService.initializeGame(gameId);
		 * 
		 * Game gameToJoin = repository.findById(gameId).get(); // 1. Retrieve user id
		 * from token (over UserDetails) // 2. Check if game has already one player,
		 * then add me as second player //if(gameToJoin.getPlayerA().getId() <= 0) { //
		 * gameToJoin.setPlayerA(new Player(gameId, gameToJoin.getPlayerA().getId(),
		 * gameToJoin.getPlayerA().getHand() ,gameToJoin)); } else
		 * if(gameToJoin.getPlayerB().getId() <= 0) { gameToJoin.setPlayerB(new
		 * Player(gameId, gameToJoin.getPlayerB().getId(),
		 * gameToJoin.getPlayerB().getHand(), gameToJoin)); gameToJoin.setPhase(1); }
		 * else { return ResponseEntity.status(HttpStatus.CONFLICT).build(); }
		 * repository.save(gameToJoin); return
		 * ResponseEntity.ok(repository.findById(gameId).get()); }
		 */
}
