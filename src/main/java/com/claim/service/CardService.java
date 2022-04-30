package com.claim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.claim.database.CardRepository;
import com.claim.model.Card;
import com.claim.model.Fraction;

/** Represents services for Card (generates Cards for once)
 * @author Rocco Saracino und Valentina Caldana
 * -Added repository (card)
 * -Method generateCards() to generate a cardDeck
*/

@Service
public class CardService {
	
	@Autowired
	private CardRepository cardRepository;



	public void generateCards() {

		/**
		 * Each of them 10 cards with value 0 - 9
		 */
		for (int i = 0; i < 10; i++) {
			Card dwarf = new Card(null, Fraction.DWARF, i, null);
			Card undead = new Card(null, Fraction.UNDEAD, i, null);
			Card doppelganger = new Card(null, Fraction.DOPPELGANGER, i, null);

			cardRepository.save(dwarf);
			cardRepository.save(undead);
			cardRepository.save(doppelganger);
		}

		/**
		 * 14 cards with value 0 - 9 (GOBLIN has 5 times value 0)
		 */
		for (int i = 1; i < 15; i++) {
			if (i < 10) {
				Card goblin = new Card(null, Fraction.GOBLIN, i, null);
				cardRepository.save(goblin);

			} else {
				Card goblin = new Card(null, Fraction.GOBLIN, 0, null);
				cardRepository.save(goblin);
			}

		}

		/**
		 * 8 cards with value 2 - 9
		 */
		for (int i = 2; i < 10; i++) {
			Card knight = new Card(null, Fraction.KNIGHT, i, null);

			cardRepository.save(knight);
		}


	}

}
