package com.claim.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.claim.database.CardDeckRepository;
import com.claim.database.CardRepository;
import com.claim.database.GameRepository;
import com.claim.database.PlayerRepository;
import com.claim.model.Card;
import com.claim.model.CardDeck;
import com.claim.model.Game;
import com.claim.model.Player;

/**Represents services for Game
 * @author Rocco Saracino & Valentina Caldana*/

@Service
public class GameService {

	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private PlayerRepository playerRepository;
	@Autowired
	private CardDeckRepository cardDeckRepository;
	@Autowired
	private CardRepository cardRepository;
	@Autowired
	private CardDeckService cardDeckService;
	@Autowired
	private CardService cardService;

	private Card removedCard;
	private List<Card> tempPlayedCards = new ArrayList<Card>();
	private List<Card> handPhase2A;
	private List<Card> handPhase2B;

	public Game initializeGame(Integer playerAId) {

		/**Very Game has its own card set (52 cards)
		 * If Game-List has Game objects, 52 cards
		 * are generated (incl. stored in database)
		 * and given to Card-List.
		 * Else = empty Game-List
		 * thanks to @createCardsIfNotExist
		 * Cards can be retrieved from Card-repository 
		 * and given to Card-List */
		List<Card> cards = new ArrayList<Card>();
		List<Game> games = gameRepository.findAll();
		if (!games.isEmpty()) {
			cards = cardService.generateCards();
		} else if (games.isEmpty()) {
			cards = cardRepository.findAll();
		}

		/** Generate a new Game*/
		Game game = new Game();
		gameRepository.save(game);

		/** shuffles 52 generated cards	 */
		Collections.shuffle(cards);

		/**Distribute 13 cards per Player to generate a hand*/
		List<Card> handPlayerA = cards.subList(1, 14);
		List<Card> handPlayerB = cards.subList(14, 27);

		/**Generate PlayerA and save in repository*/
		Player playerA = new Player(null, playerAId, handPlayerA, game, null, null);
		game.setPlayerA(playerA);
		playerRepository.save(playerA);

		/**Generate PlayerB and save in repository*/
		Player playerB = new Player(null, null, handPlayerB, game, null, null);
		game.setPlayerB(playerB);
		playerRepository.save(playerB);

		/**Take remaining cards and 
		 *create a CardDeck (26 cards to CardDeck)*/
		List<Card> cardsForDeck = cards.subList(27, 52);
		CardDeck deck = new CardDeck(null, game, cardsForDeck);
		
		game.setCardDeck(deck);
		deck.setGame(game);
		cardDeckRepository.save(deck);
		
		return game;
	}

	/**Method handles 13 rounds of
	 * phase 1*/
	public void phase1(Integer gameId, Integer playerA, Integer playerB, Integer cardIdA, Integer cardIdB) {

		/**Get specific Game & Player from database*/
		Game game = gameRepository.getById(gameId);
		Player pA = playerRepository.getById(playerA);
		Player pB = playerRepository.getById(playerB);

		/**Get List of playedCards from database
		 * and store its cards in Card objects */
		List<Card> playedCards = game.getPlayedCards();
		Card cardA = playedCards.get(0);
		Card cardB = playedCards.get(1);

		/**Get top card of Deck and store it into
		 * a Card object...
		 * @cardToWin = revealed Card
		 * @coveredCard = to pick a card from deck */
		Card cardToWin = cardDeckService.topCard(game.getCardDeck().getId());
		cardDeckService.removeCardFromDeck(game.getCardDeck().getId(), cardToWin.getId());
		Card coveredCard = cardDeckService.topCard(game.getCardDeck().getId());
		cardDeckService.removeCardFromDeck(game.getCardDeck().getId(), coveredCard.getId());

		/**PlayerA*/
		if (pA.getCardsForPhase2().isEmpty()) {
			
		/**Creates empty deposit stack in first round */
			handPhase2A = new ArrayList<Card>();
		} else {
			
		/**In following rounds it gets the empty created
		 * deposit stack*/
			handPhase2A = pA.getCardsForPhase2();
		}
		 
		/**PlayerB*/
		if (pB.getCardsForPhase2().isEmpty()) {
			
			/**Creates empty deposit stack in first round */
			handPhase2B = new ArrayList<Card>();
		} else {
			
			/**In following rounds it gets the empty created
			 * deposit stack*/
			handPhase2B = pB.getCardsForPhase2();
		}

		/**Finds out about who won a round &
		 * fills deposit stack("hand for phase 2")*/
		if (cardA.isWinner(cardB)) {
			handPhase2A.add(cardToWin);
			handPhase2B.add(coveredCard);
		} else {
			handPhase2B.add(cardToWin);
			handPhase2A.add(coveredCard);
		}
		
		pA.setCardsPhase2(handPhase2A);
		playerRepository.save(pA);
		
		pB.setCardsPhase2(handPhase2B);
		playerRepository.save(pB);

		/**Get List of playedCards from database
		 * and remove its cards in Card objects
		 * (List should be empty per round) */
		playedCards.remove(1);
		playedCards.remove(0);
		game.setPlayedCards(playedCards);
		gameRepository.save(game);
	}

	/**Allow Player A to play a card from hand*/
	public void makeMovePlayerA(Integer gameId, Integer playerA, Integer cardIdA) {
		Game game = gameRepository.getById(gameId);

		/**Pick a specific card by playerID and 
		 * cardID, add it to List tempPlayedCards */
		Card cardA = removeCard(cardIdA, playerA);
		tempPlayedCards.add(cardA);

		/**Put content of tempPlayedCards in 
		 * List playedCards of Game-Constructor*/
		game.setPlayedCards(tempPlayedCards);
		gameRepository.save(game);
	}

	/**Allow Player B to play a card from hand*/
	public void makeMovePlayerB(Integer gameId, Integer playerB, Integer cardIdB) {
		Game game = gameRepository.getById(gameId);

		/**Pick a specific card by playerID and 
		 * cardID, add it to List tempPlayedCards */
		Card cardB = removeCard(cardIdB, playerB);
		tempPlayedCards.add(cardB);

		/**Put content of tempPlayedCards in 
		 * List playedCards of Game-Constructor*/
		game.setPlayedCards(tempPlayedCards);
		gameRepository.save(game);
	}

	/**A specific Card of a specific Player hand 
	 * will be removed and returned*/
	private Card removeCard(Integer cardId, Integer playerId) {
		removedCard = new Card();

		Player player = playerRepository.getById(playerId);
		List<Card> playerHand = player.getHand();
		
		/**Iteration: Check every card to see if
		 * it is the chosen card (by player)
		 * to be played/to make the move */
		for (Card card : playerHand) {
			if (card.getId() == cardId) {
				
				/**As soon as match is found, 
				 * card is stored in card object*/
				removedCard = card;
			}
		}
		
		/**Card from iteration is removed from playerHand*/
		playerHand.remove(removedCard);
		playerRepository.save(player);

		return removedCard;
	}

}
