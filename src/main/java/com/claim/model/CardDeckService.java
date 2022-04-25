package com.claim.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.claim.database.CardDeckRepository;
import com.claim.database.CardRepository;

/** Represents services for CardDeck
 * @author Rocco Saracino und Valentina Caldana
 * -Added repositories (card and cardDeck)
 * -Method generateCards() to generate a cardDeck
*/

@Service
public class CardDeckService {
	
	@Autowired
	private CardDeckRepository cardDeckRepository;

	@Autowired
	private CardRepository cardRepository;
	
	public CardDeck generateCards() {
		
		CardDeck deck = new CardDeck(null, null);
		deck = cardDeckRepository.save(deck);
		
		/** Each of them 10 cards with value 0 - 9
		*/
		for(int i=0; i<10; i++) {
			Card dwarf = new Card(null, Fraction.DWARF, i, null, deck);
			Card undead = new Card(null, Fraction.UNDEAD, i, null, deck);
			Card doppelganger = new Card(null, Fraction.DOPPELGANGER, i, null, deck);
			Card goblin = new Card(null, Fraction.GOBLIN, i, null, deck);
			
			cardRepository.save(dwarf);
			cardRepository.save(undead);
			cardRepository.save(doppelganger);
		}
		
		/** 14 cards with value 0 - 9
		 * (GOBLIN has 5 times value 0)
		*/
		for(int i=1; i<15; i++) {
			if(i<10) { 
				Card goblin = new Card(null, Fraction.GOBLIN, i, null, deck);
				cardRepository.save(goblin);

			}else {
				Card goblin = new Card(null, Fraction.GOBLIN, 0, null, deck);
				cardRepository.save(goblin);
			}
			
		}
		
		/** 8 cards with value 2 - 9
		*/
		for(int i=2; i<10; i++) {
			Card knight = new Card(null, Fraction.KNIGHT, i, null, deck);
			
			cardRepository.save(knight);
		}
		
		return deck;
		
	}
	
	
}
