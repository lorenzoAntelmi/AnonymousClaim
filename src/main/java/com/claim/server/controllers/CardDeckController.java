package com.claim.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.claim.model.Card;
import com.claim.model.CardDeck;
import com.claim.service.CardDeckService;

/** Represents the CardDeckController, which provides
 * Endpoints to get the topcard of a deck and to remove the topcard of a deck
 * @author Deborah Vanzin
*/

@RestController
public class CardDeckController {
	
	@Autowired
	private CardDeckService cardDeckService;
	

	@GetMapping(path="/carddecks/{cardDeckId}/topcard")
	public Card getTopOfDeck(@PathVariable("cardDeckId") Integer cardDeckId) {
		return cardDeckService.topCard(cardDeckId);
	}
	
	// Create an endpoint to remove the top card of the deck
}
