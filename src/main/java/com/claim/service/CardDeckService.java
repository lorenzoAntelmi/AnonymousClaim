package com.claim.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.claim.database.CardDeckRepository;
import com.claim.database.CardRepository;
import com.claim.model.Card;
import com.claim.model.CardDeck;

/** Represents services for CardDeck
 * CardDeck refers to the initial game deck!
 * @author Deborah Vanzin
*/
@Service
public class CardDeckService {

	@Autowired
	private CardDeckRepository cardDeckRepository;

	@Autowired
	private CardRepository cardRepository;

	/** Returns the last card of the list in CardDeck, meaning the top card of the deck
	*/
	public Card topCard(Integer cardDeckId) {
		CardDeck cardDeck = cardDeckRepository.findById(cardDeckId).get();
		List<Card> cards = cardDeck.getCards();
		int indexLastCard = cards.size() - 1;

		/** We do not remove the card from the list, otherwise we would violate idempotency.
		 * But see method removeCardFromDeck
		*/
		return cards.get(indexLastCard);
	}


	public boolean removeCardFromDeck(Integer cardDeckId, Integer cardId) {
		CardDeck cardDeck = cardDeckRepository.findById(cardDeckId).get();
		Card cardToRemoveFromDeck = cardRepository.findById(cardId).get();

		/** Remove card from the cards in the deck
		*/
		List<Card> cards = cardDeck.getCards();
		boolean removedSuccessfully = cards.remove(cardToRemoveFromDeck);

		cardDeckRepository.save(cardDeck);
		return removedSuccessfully;
	}
}
