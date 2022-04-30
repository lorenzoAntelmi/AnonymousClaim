package com.claim.service;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.claim.database.CardRepository;
import com.claim.database.GameRepository;
import com.claim.model.Card;
import com.claim.model.CardDeck;
import com.claim.model.Game;
import com.claim.model.HandCard;

/** Represents services for Game
 * @author Deborah Vanzin
*/

@Service
public class GameService {
	
	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private CardRepository cardRepository;

	public Game initializeGame(Integer playerAId) {
		
		/** Generate a new game
		*/
		Game game = new Game();
		game.setPlayerA(playerAId);
		
		/** Get all cards (52) from database and shuffle
		*/
		List<Card> cards = cardRepository.findAll();
		Collections.shuffle(cards);

		/** Distribute cards to player to generate HandCard
		*/
		for (int i = 0 ; i  < 13; i++) {
			Card nextCardA = cards.remove(cards.size()-1);
			Card nextCardB = cards.remove(cards.size() -1);
			game.getCardsPlayerA().add(new HandCard(game, nextCardA));
			game.getCardsPlayerB().add(new HandCard(game, nextCardB));
		}
		
		/**Create a CardDeck to give it
		 * remaining cards (26 cards to CardDeck) 
		*/
		CardDeck deck = new CardDeck(game);
		for (Card card : cards) {
			deck.getCards().add(card);
		}
		
		game.setCardDeck(deck);
		gameRepository.save(game);
		return game;

	}



}
