package com.claim.server.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.claim.database.GameRepository;
import com.claim.model.Game;
import com.claim.model.HandCard;

@RestController
public class HandCardController {
	
	@Autowired
	private GameRepository gameRepository;
	
	@GetMapping(path ="/myCards/{gameId}/{playerId}")
	public List<HandCard> getMyCards(@PathVariable("gameId") Integer gameId, @PathVariable("playerId") Integer playerId) {
		
		/**Retrieve the Game with gameId from Database
		*/
		Game game = gameRepository.findById(gameId).get();
		
		
		/**Check if playerId is equals Player A 
		*/
		if (game.getPlayerA().equals(playerId))
			// Get the HandCards for Player A
			// The HandCard entity has a reference to the card itself
			// We need to map the HandCard to a Card using the map function of stream.
			//return game.getCardsPlayerA().stream().map(h -> h.getCard()).toList();
			return game.getCardsPlayerA();
		
		/**else-part: 
		*/
		//return game.getCardsPlayerB().stream().map(h ->h.getCard()).toList();
		return game.getCardsPlayerB();
}
}