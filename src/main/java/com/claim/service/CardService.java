package com.claim.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.claim.database.CardRepository;
import com.claim.model.Card;
import com.claim.model.Fraction;

/** Represents services for Card (generates 52 Cards)
 * @author Rocco Saracino und Valentina Caldana*/

@Service
public class CardService {

	@Autowired
	private CardRepository cardRepository;

	public List<Card> generateCards() {

		List<Card> initialCards = new ArrayList<>();
		/**
		 * Each of them 10 cards with value 0 - 9
		 */
		for (int i = 0; i < 10; i++) {
			Card dwarf = new Card(null, Fraction.DWARF, i, null);
			Card undead = new Card(null, Fraction.UNDEAD, i, null);
			Card doppelganger = new Card(null, Fraction.DOPPELGANGER, i, null);

			initialCards.add(dwarf);
			initialCards.add(undead);
			initialCards.add(doppelganger);
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
				initialCards.add(goblin);
				cardRepository.save(goblin);

			} else {
				Card goblin = new Card(null, Fraction.GOBLIN, 0, null);
				initialCards.add(goblin);
				cardRepository.save(goblin);
			}
		}

		/**
		 * 8 cards with value 2 - 9
		 */
		for (int i = 2; i < 10; i++) {
			Card knight = new Card(null, Fraction.KNIGHT, i, null);

			initialCards.add(knight);
			cardRepository.save(knight);
		}
		return initialCards;
	}
}
