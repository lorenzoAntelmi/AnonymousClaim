package com.claim.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.claim.database.CardRepository;
import com.claim.database.GameRepository;
import com.claim.database.PlayerRepository;
import com.claim.model.Card;
import com.claim.model.CardDeck;
import com.claim.model.Game;
import com.claim.model.Player;

/** Represents services for Game
 * @author Rocco Saracino & Valentina Caldana
*/

@Service
public class GameService {
	
	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private CardRepository cardRepository;
	@Autowired
	private PlayerRepository playerRepository;
	
	private Card removedCard;
	
	private List<Card> tempPlayedCards = new ArrayList<Card>();

	/**@author Deborah Vanzin
	*/
	public Game initializeGame(Integer playerAId) {
		
		/**Generate a new Game and Player
		*/
		Game game = new Game();
		Player playerA = new Player(null, playerAId, game);
		Player playerB = new Player(null, null, game);
		
		game.setPlayerA(playerA);
		game.setPlayerB(playerB);
		
		/** Get all cards (52) from database and shuffle
		*/
		List<Card> cards = cardRepository.findAll();
		//Collections.shuffle(cards); Zu Testzwecken erstmal auskommentiert

		/** Distribute 13 cards per Player to generate a hand
		*/
		List<Card> handPlayerA = new ArrayList<Card>(); 
		List<Card> handPlayerB = new ArrayList<Card>(); 
		
		for (int i = 0 ; i  < 13; i++) {
			Card nextCardA = cards.remove(cards.size()-1);			
			handPlayerA.add(nextCardA);
		}
		playerA.setHand(handPlayerA);
		
		for (int i= 0; i < 13; i++) {
			Card nextCardB = cards.remove(cards.size()-1);
			handPlayerB.add(nextCardB);
		}
		playerB.setHand(handPlayerB);
		
		/**Create a CardDeck to give it
		 * remaining cards (26 cards to CardDeck) 
		*/
		CardDeck deck = new CardDeck(game);
		for (Card card : cards) {
			deck.getCards().add(card);
		}
		
		game.setCardDeck(deck);
		gameRepository.save(game);
		
		playerRepository.save(playerA);
		playerRepository.save(playerB);
		
		return game;

	}

	/** Following methods to allow Player to play a card from hand
	*/	
		public void makeMovePlayerA (Integer gameId, Integer playerA, Integer cardIdA) {
			Game game = gameRepository.getById(gameId);
			
			/**Pick a specific card by playerID and cardID,
			 * add it to List tempPlayedCards
			 */
			Card cardA = removeCard(cardIdA, playerA);
			tempPlayedCards.add(cardA);
			
			/**Put content of tempPlayedCards in playedCards
			 * of Game-Constructor
			 */
			game.setPlayedCards(tempPlayedCards);
			gameRepository.save(game);

		}
		
		public void makeMovePlayerB (Integer gameId, Integer playerB, Integer cardIdB) {
			Game game = gameRepository.getById(gameId);
			
			/**Pick a specific card by playerID and cardID,
			 * add it to List tempPlayedCards
			 */
			Card cardB = removeCard(cardIdB, playerB);
			tempPlayedCards.add(cardB);
			
			/**Put content of tempPlayedCards in playedCards
			 * of Game-Constructor
			 */
			game.setPlayedCards(tempPlayedCards);
			gameRepository.save(game);
		}
		
		/**A specific Card of a specific Player hand will be removed and returned
		 */
			private Card removeCard(Integer cardId, Integer playerId) {
				removedCard = new Card();
				
				Player cardPlayer = playerRepository.getById(playerId);
				
				List<Card> playerHand = cardPlayer.getHand();
				
				for (Card card : playerHand) {
					
					if (card.getId() == cardId) {
						removedCard = card;
					}
				}
				playerHand.remove(removedCard);
				playerRepository.save(cardPlayer);
				
				return removedCard;
			}


}
