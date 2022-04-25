package com.claim.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claim.model.CardDeck;
import com.claim.model.CardDeckService;

/** Represents the CardDeckController, which generates
 * a CardDeck
 * @author Rocco Saracino und Valentina Caldana
 * -initialized CardDeck with HTTP GetMapping
*/

@RestController
public class CardDeckController {
	
	@Autowired
	private CardDeckService cardDeckService;
	
	@GetMapping("/initializeDeck")
	public CardDeck initializeDeck() {
		
		return cardDeckService.generateCards();
	}

}
