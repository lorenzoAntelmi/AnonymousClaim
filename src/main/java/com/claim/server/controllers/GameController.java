package com.claim.server.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claim.database.GameRepository;
import com.claim.model.Game;
import com.claim.service.GameService;

/**Provides game-related endpoints.
 * @author Deborah Vanzin */
@RestController
public class GameController {

	@Autowired
	public GameRepository repository;
	@Autowired
	private GameService gameService;

	/**To make a move & update a game after every move
	 * @author: Rocco Saracino*/
	@GetMapping(path = "/phase1/{cardId}")
	public Game makeMovePhase1(
			@AuthenticationPrincipal UserDetails user,
			 @PathVariable("cardId") Integer cardId) {

		return gameService.makeMovePhase1(user.getUsername(), cardId);	
	}
	
	// neue Methode für Phase2
	@GetMapping(path = "/phase2/{cardId}")
	public Game makeMovePhase2(
			@AuthenticationPrincipal UserDetails user,
			 @PathVariable("cardId") Integer cardId) {

		return gameService.makeMovePhase2(user.getUsername(), cardId);	
	}

	/**Create a new game with the data passed in the request body
	 * @throws URISyntaxException*/
	@PostMapping(path = "/games")
	public ResponseEntity<Game> createNewGame() throws URISyntaxException {

		Game game = gameService.initializeGame();
		URI location = new URI("http://localhost:8080/games/" + game.getId());
		return ResponseEntity.created(location).body(game);
	}
	
	/**To join an open game as Player B*/
	@GetMapping(path = "/games/{gameId}/join")
	public Game joinGame(
			@PathVariable int gameId, 
			@AuthenticationPrincipal UserDetails user
			) throws URISyntaxException {
		return gameService.joinGame(gameId,user);
	}
	
	// Support Endpoints
	/**Get a game with a specific id */
	@GetMapping(path = "/games/{id}")
	public ResponseEntity<Game> readGame(@PathVariable Integer id) {
		Optional<Game> optional = repository.findById(id);
		if (optional.isPresent()) {
			return ResponseEntity.ok(optional.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/**To delete all games -- POSTMAN*/
	@DeleteMapping(path = "/games")
	public ResponseEntity<Void> deleteAllgames() {
		gameService.removeAllGames();
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	/**löscht alle Spiele nach User oder Mail*/
	@DeleteMapping("/games/") 
	public ResponseEntity<Void> deleteGameByID(@AuthenticationPrincipal UserDetails user) {
		gameService.removeUserGames(user);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	/**To get the currentGame*/
	@GetMapping("/getCurrentGame")
	public Game getCurrentGame(@AuthenticationPrincipal UserDetails user) {
		return gameService.getCurrentGame(user.getUsername());
	}
	
	/**Lists all open games*/
	@GetMapping(path = "/games/open")
	public List<Game> getListOfOpenGames() {
		return repository.findByPhase(0);
	}

	/**Lists all games*/
	@GetMapping(path = "/games")
	public List<Game> listAllGames() {
		return repository.findAll();
	}

	/**Open games that have only one player waiting for an opponent*/
	@GetMapping(path = "/opengames")
	public List<Game> listAllOpenGames() {
		return repository.findByPlayerBAccount(null);
	}

}
