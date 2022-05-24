package com.claim.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.claim.database.AccountRepository;
import com.claim.database.CardDeckRepository;
import com.claim.database.CardRepository;
import com.claim.database.GameRepository;
import com.claim.database.PlayerRepository;
import com.claim.model.Account;
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
	@Autowired
	private AccountRepository accountRepository;

	private Card removedCard;
	private List<Card> tempPlayedCards = new ArrayList<>();
	private List<Card> handPhase2A;
	private List<Card> handPhase2B;

	public Game initializeGame() {
		/** Wir holen uns den Context aus dem SecurityContextHolder.
		* In diesem Context wird das Principal (aka eingeloggter User) geholt.
		* Mit dem Principal Objekt können wir den Account aus der Datenbank laden.
		* @author Lorenzo Antelmi*/
		  Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		  String email;
		  if (principal instanceof UserDetails) {
		     email = ((UserDetails)principal).getUsername();
		  } else {
		     email = principal.toString();
		  } 
		  
		  Optional<Account> maybeAccount = accountRepository.findByEmail(email);
		  
		  Account account = maybeAccount.get();
		
		  
		/**Every Game has its own card set (52 cards)
		 * If Game-List has Game objects, 52 cards
		 * are generated (incl. stored in database)
		 * and given to Card-List.
		 * Else = empty Game-List
		 * thanks to @createCardsIfNotExist
		 * Cards can be retrieved from Card-repository
		 * and given to Card-List */
		List<Card> cards = new ArrayList<>();
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
		List<Card> handPlayerA = cards.subList(0, 13);
		List<Card> handPlayerB = cards.subList(13, 26);

		/**Generate PlayerA and save in repository*/
		Player playerA = new Player(account, handPlayerA, game, null, null);
		game.setPlayerA(playerA);
		playerRepository.save(playerA);

		/**Generate PlayerB and save in repository*/
		Player playerB = new Player(null, handPlayerB, game, null, null);
		game.setPlayerB(playerB);
		playerRepository.save(playerB);

		/**Take remaining cards and
		 *create a CardDeck (26 cards to CardDeck)*/
		List<Card> cardsForDeck = cards.subList(27, 52);
		CardDeck deck = new CardDeck(game, cardsForDeck);

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
			handPhase2A = new ArrayList<>();
		} else {

		/**In following rounds it gets the empty created
		 * deposit stack*/
			handPhase2A = pA.getCardsForPhase2();
		}

		/**PlayerB*/
		if (pB.getCardsForPhase2().isEmpty()) {

			/**Creates empty deposit stack in first round */
			handPhase2B = new ArrayList<>();
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


		/**This part handles UNDEAD logic, in which won Cards
		 * have to be put into the final pointStack of phase 2.
		 * For this purpose a temp List "pointsUndead" is
		 * created.*/
		List<Card> pointsUndead = new ArrayList<Card>();
		
		/**If Player A wins and both played Cards are UNDEAD
		 * the played Cards go directly into the List 
		 * "pointsUndead". Then the "pointStack" of Player A is
		 * being set with the Cards of "pointsUndead". */

		if (cardA.isWinner(cardB) && (playedCards.get(1).getFraction().name() == "UNDEAD"
				&& playedCards.get(0).getFraction().name() == "UNDEAD")) {
			pointsUndead.add(playedCards.get(0));
			pointsUndead.add(playedCards.get(1));
			pA.setPointStack(pointsUndead);
			
		/**If Player B wins and both played Cards are UNDEAD
		 * the played Cards go directly into the List 
		 * "pointsUndead". Then the "pointStack" of Player B is
		 * being set with the Cards of "pointsUndead". */
		} else if (cardB.isWinner(cardA) && (playedCards.get(1).getFraction().name() == "UNDEAD"
				&& playedCards.get(0).getFraction().name() == "UNDEAD")) {
			pointsUndead.add(playedCards.get(0));
			pointsUndead.add(playedCards.get(1));
			pB.setPointStack(pointsUndead);
		} else {
			
			/**In case only one of the two played Cards is UNDEAD
			 * only the UNDEAD-Card should go into "pointStack". 
			 * See the following iteration and switch per List-position:*/
			int i = playedCards.size() - 2;
			for (Card c : playedCards) {
				switch (i) {

				case (0): 
					/**Player A wins*/

					if (cardA.isWinner(cardB) && c.getFraction().name() == "UNDEAD") {
						pointsUndead.add(playedCards.get(0));
						pA.setPointStack(pointsUndead);
						/**Player B wins*/
					} else if (cardB.isWinner(cardA) && c.getFraction().name() == "UNDEAD") {
						pointsUndead.add(playedCards.get(0));
						pB.setPointStack(pointsUndead);
					}
				break;
				case (1):

					/**Player A wins*/

					if (cardA.isWinner(cardB) && c.getFraction().name() == "UNDEAD") {
						pointsUndead.add(playedCards.get(1));
						pA.setPointStack(pointsUndead);
						/**Player B wins*/
					} else if (cardB.isWinner(cardA) && c.getFraction().name() == "UNDEAD") {
						pointsUndead.add(playedCards.get(1));
						pB.setPointStack(pointsUndead);
					}
				break;
				}
				i++;
			}
		}

		/**Get List of playedCards from database
		 * and remove its cards in Card objects
		 * (List should be empty per round) */
		playedCards.remove(1); playedCards.remove(0);
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
	
	/**Method handles 13 rounds of
	 * phase 2*/
	public void phase2 (Integer gameId, Integer playerA, 
		Integer playerB, Integer cardIdA, Integer cardIdB) {
			
		/**Get specific Game & Player from database*/
		Game game = gameRepository.getById(gameId);
		Player pA = playerRepository.getById(playerA);
		Player pB = playerRepository.getById(playerB);

		/**List for pointStack per Player (List was 
		 * already initialized in phase 1 for UNDEAD logic)*/
		List<Card> pointStackPlayerA = pA.getPointStack();
		List<Card> pointStackPlayerB = pB.getPointStack();

		/**Get the playedCards List and store the Cards into
		 * objects.*/
		List<Card> playedCards = game.getPlayedCards();
		Card cardA = playedCards.get(0);
		Card cardB = playedCards.get(1);
		
		/**Iteration through List playedCards for
		 * following switch, which defines rules
		 * of phase 2:*/
		int i = playedCards.size() - 2;
		for (Card c : playedCards) {
			switch (i) {
			case (0): 
				
				/**Case: Player A wins & plays DWARF
				 * DWARF-Card (at List-position 0) goes  
				 * into pointsStack of Player B 
				 * (loosers pointStack).*/
				if ((cardA.isWinner(cardB) == true) 
						&& c.getFraction().name() == "DWARF") {
					c = playedCards.get(0);
					pointStackPlayerB.add(c);
					pB.setPointStack(pointStackPlayerB);
				
					/**Case: Player A looses & plays DWARF
					 * DWARF-Card (at List-position 0) goes  
					 * into pointsStack of Player A 
					 * (loosers pointStack).*/
				} else if ((cardA.isWinner(cardB) == false) 
						&& c.getFraction().name() == "DWARF"){
					c = playedCards.get(0);
					pointStackPlayerA.add(c);
					pA.setPointStack(pointStackPlayerA);
				}
			break;
			case (1):
				
				/**Case: Player A wins & plays DWARF,
				 * DWARF-Card (at List-position 1) goes  
				 * into pointsStack of Player B 
				 * (loosers pointStack).*/
				if ((cardA.isWinner(cardB) == true) 
						&& c.getFraction().name() == "DWARF") {
					c = playedCards.get(1);
					pointStackPlayerB.add(c);
					pB.setPointStack(pointStackPlayerB);
					
					/**Case: Player A looses & plays DWARF,
					 * DWARF-Card (at List-position 1) goes  
					 * into pointsStack of Player A 
					 * (loosers pointStack).*/
				} else if ((cardA.isWinner(cardB) == false) 
						&& c.getFraction().name() == "DWARF"){
					c = playedCards.get(1);
					pointStackPlayerA.add(c);
					pA.setPointStack(pointStackPlayerA);
				}
			break;
			case (2):
				if (cardA.isWinner(cardB)) {
					pointStackPlayerA.addAll(playedCards);
					pA.setPointStack(pointStackPlayerA);
				} else {
					pointStackPlayerB.addAll(playedCards);
					pB.setPointStack(pointStackPlayerB);
				}
			break;
			}
			
			/**This part handles the increment of "i"
			 * after every break and checks if both
			 * Cards are NOT DWARFS!
			 * -> Goes case (0), then i++ & if, goes case (1),
			 * then i++ & if, goes case (2)*/
			i++;
			if (playedCards.get(0).getFraction().name() != "DWARF" && 
					playedCards.get(1).getFraction().name() != "DWARF") {
				i = 2;
			}
		} 

		playerRepository.save(pA);
		playerRepository.save(pB);

		/**Get List of playedCards from database
		 * and remove its cards in Card objects
		 * (List should be empty per round) */
		playedCards.remove(1); playedCards.remove(0);
		game.setPlayedCards(playedCards);
		gameRepository.save(game);
	}
	
	public void calcScore(Integer playerA, Integer playerB) {
		
		Player pA = playerRepository.getById(playerA);
		Player pB = playerRepository.getById(playerB);

		/**List for pointStack per Player (List was 
		 * already initialized in phase 1 for UNDEAD logic)*/
		List<Card> pointStackPlayerA = pA.getPointStack();
		List<Card> pointStackPlayerB = pB.getPointStack();
		
		/**List per Fraction Player A*/
		List<Card> pAGoblin = new ArrayList<Card>();
		List<Card> pAKnight = new ArrayList<Card>();
		List<Card> pAUndead = new ArrayList<Card>();
		List<Card> pADoppelganger = new ArrayList<Card>();
		List<Card> pADwarf = new ArrayList<Card>();
		
		/**Iteration through pointStack Player A
		 * to check for every Fraction and to
		 * add them into the corresponding
		 * Fraction-List*/
		for (Card c : pointStackPlayerA) {
			switch (c.getFraction().name()) {
			case ("GOBLIN"): pAGoblin.add(c);
			break;
			case ("KNIGHT"): pAKnight.add(c);
			break;
			case ("UNDEAD"): pAUndead.add(c);
			break;
			case ("DOPPELGANGER"): pADoppelganger.add(c);
			break;
			case ("DWARF"): pADwarf.add(c);
			break;
			}
		}
		
		/**List per Fraction Player B*/
		List<Card> pBGoblin = new ArrayList<Card>();
		List<Card> pBKnight = new ArrayList<Card>();
		List<Card> pBUndead = new ArrayList<Card>();
		List<Card> pBDoppelganger = new ArrayList<Card>();
		List<Card> pBDwarf = new ArrayList<Card>();
		
		/**Iteration through pointStack Player B
		 * to check for every Fraction and to
		 * add them into the corresponding
		 * Fraction-List*/
		for (Card c : pointStackPlayerB) {
			switch (c.getFraction().name()) {
			case ("GOBLIN"): pBGoblin.add(c);
			break;
			case ("KNIGHT"): pBKnight.add(c);
			break;
			case ("UNDEAD"): pBUndead.add(c);
			break;
			case ("DOPPELGANGER"): pBDoppelganger.add(c);
			break;
			case ("DWARF"): pBDwarf.add(c);
			break;
			}
		}
		
		//ENTWURF, DARIN SOLL DER PUNKTESTAND GESPEICHERT WERDEN
		//ALLERDINGS WISSEN WIR NICHT WO WIR DEN SPEICHERN SOLLEN
		//SOLLTE MIT ACCOUNT ZUSAMMENHÄNGEN
		int scoreA = 0;
		int scoreB = 0;
		
		/**List Fraction GOBLIN - comparing Player A & Player B
		 * Player with bigger list, gets his Score incremented*/
		if (pAGoblin.size() > pBGoblin.size()) {
			scoreA++;
		} else {
			scoreB++;
		}
		
		/**List Fraction KNIGHT - comparing Player A & Player B
		 * Player with bigger list, gets his Score incremented*/
		if (pAKnight.size() > pBKnight.size()) {
			scoreA++;
		} else {
			scoreB++;
		}
		
		/**List Fraction UNDEAD - comparing Player A & Player B
		 * Player with bigger list, gets his Score incremented*/
		if (pAUndead.size() > pBUndead.size()) {
			scoreA++;
		} else {
			scoreB++;
		}
		
		/**List Fraction DOPPELGANGER - comparing Player A & Player B
		 * Player with bigger list, gets his Score incremented*/
		if (pADoppelganger.size() > pBDoppelganger.size()) {
			scoreA++;
		} else {
			scoreB++;
		}
		
		/**List Fraction DWARF - comparing Player A & Player B
		 * Player with bigger list, gets his Score incremented*/
		if (pADwarf.size() > pBDwarf.size()) {
			scoreA++;
		} else {
			scoreB++;
		}
		
		//muss noch angeschaut werden, so wird immer auf 1 gsetzt
		if (scoreA > scoreB) {	
			Integer i = pA.getAccount().getScore() + 1;
			pA.getAccount().setScore(i);
		} else {
			Integer i = pB.getAccount().getScore() + 1;
			pB.getAccount().setScore(i);
		}
		
	}
	
	/**Represents services for Game
	 * @author Deborah Vanzin
	 * joinGame: Player joins an existing GAme
	 */

	public void joinGame(int gameId) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		  String email;
		  if (principal instanceof UserDetails) {
		     email = ((UserDetails)principal).getUsername();
		  } else {
		     email = principal.toString();
		  } 
		  
		  Optional<Account> maybeAccount = accountRepository.findByEmail(email);
		  
		  Account account = maybeAccount.get();
		  
		  Game game = gameRepository.findById(gameId).get();
		  game.getPlayerB().setAccountId(account);
		  gameRepository.save(game);
		  
	}

	// TODO: Geschäftslogik
	public void putCard(Integer gameId, Integer playerId, Integer cardId) {
		Card card = cardRepository.getById(cardId);
		Game game = gameRepository.findById(gameId).get();
		// Annahme, ein Spieler kann nicht gegen sich selbst spielen.
		if (game.getPlayerA().getId().equals(playerId)) {
			game.getPlayerA().getHand().remove(card);
		}
		
	}

	public void removeAllGames() {
		gameRepository.deleteAll();
		
	}
	
	//Methode fehlt für "Wer macht den ersten Zug" (Birth Date) - Gewinner/Verlierer aus Phase 1
	//isWinner phase 1 und isWinner phase 2
}
