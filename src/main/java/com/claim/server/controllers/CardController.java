package com.claim.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claim.database.CardRepository;
import com.claim.model.Card;

/** Provides card-related endpoints.
 * @author Deborah Vanzin
*/

@RestController
public class CardController {

	@Autowired
	private CardRepository cardRepository;

	@GetMapping(path="/cards",produces="application/json")
	public List<Card> getCards() {
		return cardRepository.findAll();
	}

}
