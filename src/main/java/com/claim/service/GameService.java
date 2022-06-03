package com.claim.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.claim.database.AccountRepository;
import com.claim.database.CardDeckRepository;
import com.claim.database.CardRepository;
import com.claim.database.GameRepository;
import com.claim.database.PlayerRepository;
import com.claim.model.Account;
import com.claim.model.Card;
import com.claim.model.CardDeck;
import com.claim.model.ChatMessage;
import com.claim.model.Fraction;
import com.claim.model.Game;
import com.claim.model.Player;

/**
 * Represents services for Game
 * @author Rocco Saracino & Lorenzo Antelmi (Security)
 */

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
	
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	public Game initializeGame() {
		/**
		 * Wir holen uns den Context aus dem SecurityContextHolder. In diesem Context
		 * wird das Principal (aka eingeloggter User) geholt. Mit dem Principal Objekt
		 * können wir den Account aus der Datenbank laden.
		 * 
		 * @author Lorenzo Antelmi (for security)
		 */
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String email;
		if (principal instanceof UserDetails) {
			email = ((UserDetails) principal).getUsername();
		} else {
			email = principal.toString();
		}

		Optional<Account> maybeAccount = accountRepository.findByEmail(email);

		Account account = maybeAccount.get();

		/**
		 * Every Game has its own card set (52 cards) If Game-List has Game objects, 52
		 * cards are generated (incl. stored in database) and given to Card-List. Else =
		 * empty Game-List thanks to @createCardsIfNotExist Cards can be retrieved from
		 * Card-repository and given to Card-List
		 */
		List<Card> cards = cardService.generateCards();

		cards = cardRepository.saveAll(cards);

		/** Generate a new Game */
		Game game = new Game();
		game = gameRepository.save(game);

		/** shuffles 52 generated cards */
		Collections.shuffle(cards);

		/** Distribute 13 cards per Player to generate a hand */
		List<Card> handPlayerA = cards.subList(0, 13);
		List<Card> handPlayerB = cards.subList(13, 26);

		/** Generate PlayerA and save in repository */
		Player playerA = new Player(account, handPlayerA, game, null, null);
		playerA = playerRepository.save(playerA);
		game.setPlayerA(playerA);

		/** Generate PlayerB and save in repository */
		Player playerB = new Player(null, handPlayerB, game, null, null);
		playerB = playerRepository.save(playerB);
		game.setPlayerB(playerB);

		/**
		 * Take remaining cards and create a CardDeck (26 cards to CardDeck)
		 */
		List<Card> cardsForDeck = cards.subList(26, 52);
		CardDeck deck = new CardDeck(game, cardsForDeck);

		game.setCardDeck(deck);
		deck.setGame(game);
		cardDeckRepository.save(deck);

		return game;
	}

	public Game pickCardPhase1(Game game, Integer cardId, String username) {
		Card card = removeCard(cardId, username);
		Player player = game.getCurrentPlayer();
		player.getDepositedCard().add(card);
		player.getPlayedCards().add(card);
		return game;
	}
	
	public Game pickCardPhase2(Game game, Integer cardId, String username) {
		Card card = removeCard(cardId, username);
		Player player = game.getCurrentPlayer();
		player.getDepositedCardPhase2().add(card);
		player.getPlayedCards().add(card);
		return game;
	}

	/**
	 * A specific Card of a specific Player hand will be removed and returned
	 */
	private Card removeCard(Integer cardId, String username) {

		Game game = getCurrentGame(username);

		Player player = game.getCurrentPlayer();

		/**
		 * Iteration: Check every card to see if it is the chosen card (by player) to be
		 * played/to make the move
		 */
		Card removedCard = cardRepository.findById(cardId)
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

		/** Card from iteration is removed from playerHand */
		if (player.getHand().isEmpty()) {
			player.getCardsPhase2().remove(removedCard);
		} else {
			player.getHand().remove(removedCard);
		}
		playerRepository.save(player);

		return removedCard;
	}

	public Game calcScore(Game game, String username) {
		
		game = getCurrentGame(username);

		/** Get specific Game & Player from database */
		var pA = game.getPlayerA();
		var pB = game.getPlayerB();

		/**
		 * List for pointStack per Player (List was already initialized in phase 1 for
		 * UNDEAD logic)
		 */
		List<Card> pointStackPlayerA = pA.getPointStack();
		List<Card> pointStackPlayerB = pB.getPointStack();

		/** List per Fraction Player A */
		List<Card> pAGoblin = new ArrayList<Card>();
		List<Card> pAKnight = new ArrayList<Card>();
		List<Card> pAUndead = new ArrayList<Card>();
		List<Card> pADoppelganger = new ArrayList<Card>();
		List<Card> pADwarf = new ArrayList<Card>();

		/**
		 * Iteration through pointStack Player A to check for every Fraction and to add
		 * them into the corresponding Fraction-List
		 */
		for (Card c : pointStackPlayerA) {
			switch (c.getFraction()) {
			case GOBLIN:
				pAGoblin.add(c);
				break;
			case KNIGHT:
				pAKnight.add(c);
				break;
			case UNDEAD:
				pAUndead.add(c);
				break;
			case DOPPELGANGER:
				pADoppelganger.add(c);
				break;
			case DWARF:
				pADwarf.add(c);
				break;
			}
		}

		/** List per Fraction Player B */
		List<Card> pBGoblin = new ArrayList<Card>();
		List<Card> pBKnight = new ArrayList<Card>();
		List<Card> pBUndead = new ArrayList<Card>();
		List<Card> pBDoppelganger = new ArrayList<Card>();
		List<Card> pBDwarf = new ArrayList<Card>();

		/**
		 * Iteration through pointStack Player B to check for every Fraction and to add
		 * them into the corresponding Fraction-List
		 */
		for (Card c : pointStackPlayerB) {
			switch (c.getFraction()) {
			case GOBLIN:
				pBGoblin.add(c);
				break;
			case KNIGHT:
				pBKnight.add(c);
				break;
			case UNDEAD:
				pBUndead.add(c);
				break;
			case DOPPELGANGER:
				pBDoppelganger.add(c);
				break;
			case DWARF:
				pBDwarf.add(c);
				break;
			}
		}

		int scoreA = 0;
		int scoreB = 0;

		/**
		 * List Fraction GOBLIN - comparing Player A & Player B Player with bigger list,
		 * gets his Score incremented
		 */
		if (pAGoblin.size() > pBGoblin.size()) {
			scoreA++;
		} else {
			scoreB++;
		}

		/**
		 * List Fraction KNIGHT - comparing Player A & Player B Player with bigger list,
		 * gets his Score incremented
		 */
		if (pAKnight.size() > pBKnight.size()) {
			scoreA++;
		} else {
			scoreB++;
		}

		/**
		 * List Fraction UNDEAD - comparing Player A & Player B Player with bigger list,
		 * gets his Score incremented
		 */
		if (pAUndead.size() > pBUndead.size()) {
			scoreA++;
		} else {
			scoreB++;
		}

		/**
		 * List Fraction DOPPELGANGER - comparing Player A & Player B Player with bigger
		 * list, gets his Score incremented
		 */
		if (pADoppelganger.size() > pBDoppelganger.size()) {
			scoreA++;
		} else {
			scoreB++;
		}

		/**
		 * List Fraction DWARF - comparing Player A & Player B Player with bigger list,
		 * gets his Score incremented
		 */
		if (pADwarf.size() > pBDwarf.size()) {
			scoreA++;
		} else {
			scoreB++;
		}

		
		if (scoreA > scoreB) {
			pA.getAccount().setScore(pA.getAccount().getScore() + 1);
			game.setPhaseWinner(pA.getAccount().getUsername());
		} else {
			pB.getAccount().setScore(pB.getAccount().getScore() + 1);
			game.setPhaseWinner(pB.getAccount().getUsername());
		}
		
		return game;
	}

	/**
	 * Represents services for Game
	 * 
	 * @author Deborah Vanzin joinGame: Player joins an existing Game
	 * @param user
	 */

	public Game joinGame(int gameId, UserDetails user) {

		Account account = accountRepository.findByEmail(user.getUsername())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		Game game = gameRepository.findById(gameId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		game.getPlayerB().setAccount(account);

		if (game.getPlayerA().getAccount().getBirthDate().isAfter(account.getBirthDate())) {
			game.setCurrentPlayer(game.getPlayerA());
		} else {
			game.setCurrentPlayer(game.getPlayerB());
		}
		ChatMessage chatMessage = new ChatMessage(ChatMessage.MessageType.CURRENT_PLAYER, game.getCurrentPlayer().getAccount().getUsername(),"SERVER");
		this.messagingTemplate.convertAndSend("/topic/game/"+game.getId(), chatMessage);

		return gameRepository.save(game);
	}

	// holt ein aktueller Game
	public Game getCurrentGame(String username) {
		var game = playerRepository.findFirstByAccount_Email(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
				.getGame();
		return game;
	}
	
	public boolean isValidMovePhase2(String username, Integer cardId) {
		var game = getCurrentGame(username);
		
		var currentPlayer = game.getCurrentPlayer();
		var oppositPlayer = game.getCurrentPlayer().getId() == game.getPlayerA().getId() ? game.getPlayerB(): game.getPlayerA();
		
		if(!game.getCurrentPlayer().getAccount().getEmail().equals(username)) {
			return  false;
		}
		
		if(currentPlayer.getDepositedCardPhase2().size() == oppositPlayer.getDepositedCardPhase2().size()) {
			return true;
		}
		
		var cardIdsPhase2 = currentPlayer
				.getCardsPhase2()
				.stream()
				.map(el -> el.getId())
				.collect(Collectors.toList());
		
		// kontrolliert ob Karte in Hand vorhanden ist
		if (!cardIdsPhase2.contains(cardId)) {
			return false;
		}
		
		var ownCardPhase2 = currentPlayer
				.getCardsPhase2()
				.stream()
				.filter(el-> el.getId().equals(cardId))
				.findFirst()
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST));
		
		var oppositeCardPhase2 = oppositPlayer.getDepositedCardPhase2().get( oppositPlayer.getDepositedCardPhase2().size()-1);
		
		var ownCardFractionsPhase2 = currentPlayer
				.getCardsPhase2()
				.stream()
				.map(el -> el.getFraction())
				.collect(Collectors.toSet());
		
		switch (oppositeCardPhase2.getFraction()) {
		case DOPPELGANGER:
			return true;
		case DWARF:
				if (ownCardPhase2.getFraction() == Fraction.DWARF || ownCardPhase2.getFraction() == Fraction.DOPPELGANGER) {
					return true;
				} else {
					return !ownCardFractionsPhase2.contains(Fraction.DWARF) || !ownCardFractionsPhase2.contains(Fraction.DOPPELGANGER);
				}
		case GOBLIN:
			if (ownCardPhase2.getFraction() == Fraction.GOBLIN || ownCardPhase2.getFraction() == Fraction.DOPPELGANGER || 
					ownCardPhase2.getFraction() == Fraction.KNIGHT) {
				return true;
			} else {
				return !ownCardFractionsPhase2.contains(Fraction.GOBLIN) || !ownCardFractionsPhase2.contains(Fraction.DOPPELGANGER) ||
						!ownCardFractionsPhase2.contains(Fraction.KNIGHT);
			}
		case KNIGHT:
			if (ownCardPhase2.getFraction() == Fraction.KNIGHT || ownCardPhase2.getFraction() == Fraction.DOPPELGANGER) {
				return true;
			} else {
				return !ownCardFractionsPhase2.contains(Fraction.KNIGHT) || !ownCardFractionsPhase2.contains(Fraction.DOPPELGANGER);
			}
		case UNDEAD:
			if (ownCardPhase2.getFraction() == Fraction.UNDEAD || ownCardPhase2.getFraction() == Fraction.DOPPELGANGER) {
				return true;
			} else {
				return !ownCardFractionsPhase2.contains(Fraction.UNDEAD) || !ownCardFractionsPhase2.contains(Fraction.DOPPELGANGER);
			}
		default: 
			return  false;
		}
	}
	
	// in dieser Methode werden die erlaubten Schritte definiert
	public boolean isValidMovePhase1(String username, Integer cardId) {
		var game = getCurrentGame(username);
		
		var currentPlayer = game.getCurrentPlayer();
		var oppositPlayer = game.getCurrentPlayer().getId() == game.getPlayerA().getId() ? game.getPlayerB(): game.getPlayerA();
		
		if(!game.getCurrentPlayer().getAccount().getEmail().equals(username)) {
			return  false;
		}
		
		if(currentPlayer.getDepositedCard().size() == oppositPlayer.getDepositedCard().size()) {
			return true;
		}
		
		var cardIdsPhase1 = currentPlayer
				.getHand()
				.stream()
				.map(el -> el.getId())
				.collect(Collectors.toList());
		
		// kontrolliert ob Karte in Hand vorhanden ist
		if (!cardIdsPhase1.contains(cardId)) {
			return false;
		}
		
		var ownCardPhase1 = currentPlayer
				.getHand()
				.stream()
				.filter(el-> el.getId().equals(cardId))
				.findFirst()
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST));
		
		var oppositeCardPhase1 = oppositPlayer.getDepositedCard().get(oppositPlayer.getDepositedCard().size()-1);
		
		var ownCardFractionsPhase1 = currentPlayer
				.getHand()
				.stream()
				.map(el -> el.getFraction())
				.collect(Collectors.toSet());
		
		switch (oppositeCardPhase1.getFraction()) {
		case DOPPELGANGER:
			return  true;
		case DWARF:
				if (ownCardPhase1.getFraction() == Fraction.DWARF || ownCardPhase1.getFraction() == Fraction.DOPPELGANGER) {
					return true;
				} else {
					return !ownCardFractionsPhase1.contains(Fraction.DWARF) || !ownCardFractionsPhase1.contains(Fraction.DOPPELGANGER);
				}
		case GOBLIN:
			if (ownCardPhase1.getFraction() == Fraction.GOBLIN || ownCardPhase1.getFraction() == Fraction.DOPPELGANGER || 
					ownCardPhase1.getFraction() == Fraction.KNIGHT) {
				return  true;
			} else {
				return !ownCardFractionsPhase1.contains(Fraction.GOBLIN) || !ownCardFractionsPhase1.contains(Fraction.DOPPELGANGER) ||
						!ownCardFractionsPhase1.contains(Fraction.KNIGHT);
			}
		case KNIGHT:
			if (ownCardPhase1.getFraction() == Fraction.KNIGHT || ownCardPhase1.getFraction() == Fraction.DOPPELGANGER) {
				return  true;
			} else {
				return !ownCardFractionsPhase1.contains(Fraction.KNIGHT) || !ownCardFractionsPhase1.contains(Fraction.DOPPELGANGER);
			}
		case UNDEAD:
			if (ownCardPhase1.getFraction() == Fraction.UNDEAD || ownCardPhase1.getFraction() == Fraction.DOPPELGANGER) {
				return  true;
			} else {
				return !ownCardFractionsPhase1.contains(Fraction.UNDEAD) || !ownCardFractionsPhase1.contains(Fraction.DOPPELGANGER);
			}
		default: 
			return false;
		}
	}

	// logik für ein ganzer Spielzug bis zur festnahme wer gwonnen hat Phase 1
	public Game makeMovePhase1(String username, Integer cardId) {
		// hier passiert die ganze spiellogik
		Game game = getCurrentGame(username);

		if(!isValidMovePhase1(username,cardId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		game = pickCardPhase1(game, cardId, username);
		
		if (game.getCurrentPlayer().getId().equals(game.getPlayerA().getId())) {
			game.setCurrentPlayer(game.getPlayerB());
			
		} else if (game.getCurrentPlayer().getId().equals(game.getPlayerB().getId())){
			game.setCurrentPlayer(game.getPlayerA());
		
		}
		
		game.setRoundWinner(null);
		
		if( game.getPlayerA().getPlayedCards().size()==game.getPlayerB().getPlayedCards().size()) {
			var playerACard = game.getPlayerA().getPlayedCards().get( game.getPlayerA().getPlayedCards().size()-1);
			var playerBCard = game.getPlayerB().getPlayedCards().get( game.getPlayerB().getPlayedCards().size()-1);
			
			game = phase1(game,playerACard,playerBCard);
			
			if (game.getRoundWinner().equals(game.getPlayerA().getAccount().getUsername())) {
				game.setCurrentPlayer(game.getPlayerA());
			} else if (game.getRoundWinner().equals(game.getPlayerB().getAccount().getUsername())) {
				game.setCurrentPlayer(game.getPlayerB());
			}
			game.getPlayerA().setDepositedCard(new ArrayList<Card>());
			game.getPlayerB().setDepositedCard(new ArrayList<Card>());
			
			return gameRepository.save(game);
			
		} else {
			return gameRepository.save(game);
		}	
	}
	
	public Game makeMovePhase2(String username, Integer cardId) {
		// hier passiert die ganze spiellogik
		Game game = getCurrentGame(username);
		
		if(!isValidMovePhase2(username,cardId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		game = pickCardPhase2(game, cardId, username);
		
		if (game.getCurrentPlayer().getId().equals(game.getPlayerA().getId())) {
			game.setCurrentPlayer(game.getPlayerB());
			
		} else if (game.getCurrentPlayer().getId().equals(game.getPlayerB().getId())){
			game.setCurrentPlayer(game.getPlayerA());
			
		}
		
		game.setRoundWinner(null);
		
		if( game.getPlayerA().getPlayedCards().size()==game.getPlayerB().getPlayedCards().size()) {
			var playerACard = game.getPlayerA().getPlayedCards().get( game.getPlayerA().getPlayedCards().size()-1);
			var playerBCard = game.getPlayerB().getPlayedCards().get( game.getPlayerB().getPlayedCards().size()-1);
			
			game = phase2(game,playerACard,playerBCard);
			
			if (game.getRoundWinner().equals(game.getPlayerA().getAccount().getUsername())) {
				game.setCurrentPlayer(game.getPlayerA());
			} else if (game.getRoundWinner().equals(game.getPlayerB().getAccount().getUsername())) {
				game.setCurrentPlayer(game.getPlayerB());
			}
			
			game.getPlayerA().setDepositedCardPhase2(new ArrayList<Card>());
			game.getPlayerB().setDepositedCardPhase2(new ArrayList<Card>());
			
			if (game.getPlayerA().getCardsPhase2().size() == 0 && game.getPlayerB().getPlayedCards().size() == 00) {
				game = calcScore(game, username);
			}
			
			return gameRepository.save(game);
		} else {
			return gameRepository.save(game);
		}	
	}
	
	private boolean ruleUndead(Game game, Card playedCardA, Card playedCardB) {
	 	
		/**
		 * This part handles UNDEAD logic, in which won Cards have to be put into the
		 * final pointStack of phase 2. For this purpose a temp List "pointsUndead" is
		 * created.
		 */
		var pA = game.getPlayerA();
	 	var pB = game.getPlayerB();
		
		/**
		 * If Player A wins and both played Cards are UNDEAD the played Cards go
		 * directly into the List "pointsUndead". Then the "pointStack" of Player A is
		 * being set with the Cards of "pointsUndead".
		 */

		if (isWinner(game, playedCardA, playedCardB)
				&& (playedCardA.getFraction() == Fraction.UNDEAD && playedCardB.getFraction() == Fraction.UNDEAD)) {
			pA.getPointStack().add(playedCardB);
			pA.getPointStack().add(playedCardA);
			return true;

			/**
			 * If Player B wins and both played Cards are UNDEAD the played Cards go
			 * directly into the List "pointsUndead". Then the "pointStack" of Player B is
			 * being set with the Cards of "pointsUndead".
			 */
		} else if (isWinner(game, playedCardB, playedCardA)
				&& (playedCardA.getFraction() == Fraction.UNDEAD && playedCardB.getFraction() == Fraction.UNDEAD)) {
			pB.getPointStack().add(playedCardB);
			pB.getPointStack().add(playedCardA);
			return true;
		} else {

			/**
			 * In case only one of the two played Cards is UNDEAD only the UNDEAD-Card
			 * should go into "pointStack". See the following iteration and switch per
			 * List-position:
			 */
			List<Card> playedCards = new ArrayList<Card>();
			playedCards.add(playedCardA);
			playedCards.add(playedCardB);

			int i = playedCards.size() - 2;
			for (Card c : playedCards) {
				switch (i) {

				case (0):
					/** Player A wins */
					if (isWinner(game, playedCardA, playedCardB) && c.getFraction() == Fraction.UNDEAD) {
						pA.getPointStack().add(playedCards.get(0));
						return true;
						/** Player B wins */
					} else if (isWinner(game, playedCardA, playedCardB) && c.getFraction() == Fraction.UNDEAD) {
						pB.getPointStack().add(playedCards.get(0));
						return true;
					}
					break;
				case (1):
					/** Player A wins */
					if (isWinner(game, playedCardA, playedCardB) && c.getFraction() == Fraction.UNDEAD) {
						pA.getPointStack().add(playedCards.get(1));
						return true;
						/** Player B wins */
					} else if (isWinner(game, playedCardA, playedCardB) && c.getFraction() == Fraction.UNDEAD) {
						pB.getPointStack().add(playedCards.get(1));					
						return true;
					}
					break;
				}
				i++;
			}	
		}
		return false;
	}

	// Logik für 1 Phase des Spiels
	public Game phase1(Game game, Card playedCardA, Card playedCardB) {
		
	 	var pA = game.getPlayerA();
	 	var pB = game.getPlayerB();
		/**
		 * Get top card of Deck and store it into a Card object...
		 * 
		 * @cardToWin = revealed Card
		 * @coveredCard = to pick a card from deck
		 */
	 		
	 		Card cardToWin = cardDeckService.topCard(game.getCardDeck().getId());
			cardDeckService.removeCardFromDeck(game.getCardDeck().getId(), cardToWin.getId());
			Card coveredCard = cardDeckService.topCard(game.getCardDeck().getId());
			cardDeckService.removeCardFromDeck(game.getCardDeck().getId(), coveredCard.getId());

			ArrayList<Card> handPhase2A;
			ArrayList<Card> handPhase2B;
			
			/** PlayerA */
			if (game.getPlayerA().getCardsPhase2().isEmpty()) {

				/** Creates empty deposit stack in first round */
				handPhase2A = new ArrayList<>();
			} else {

				/**
				 * In following rounds it gets the empty created deposit stack
				 */
				handPhase2A = new ArrayList(game.getPlayerA().getCardsPhase2());
			}

			/** PlayerB */
			if (game.getPlayerB().getCardsPhase2().isEmpty()) {

				/** Creates empty deposit stack in first round */
				handPhase2B = new ArrayList<>();
			} else {

				/**
				 * In following rounds it gets the empty created deposit stack
				 */
				handPhase2B = new ArrayList(game.getPlayerB().getCardsPhase2());
			}

			/**
			 * Finds out about who won a round & fills deposit stack("hand for phase 2")
			 */
			
			if (isWinner(game, playedCardA, playedCardB) 
					&& !ruleKnight(game, playedCardA, playedCardB)) {
				handPhase2A.add(cardToWin);
				handPhase2B.add(coveredCard);
				game.setRoundWinner(game.getPlayerA().getAccount().getUsername());
			} else if (!ruleKnight(game, playedCardA, playedCardB)){
				handPhase2B.add(cardToWin);
				handPhase2A.add(coveredCard);
				game.setRoundWinner(game.getPlayerB().getAccount().getUsername());
			}
			
			
			if (game.getCurrentPlayer().getId() == game.getPlayerA().getId()) {
				if (playedCardA.getFraction() == Fraction.KNIGHT && playedCardB.getFraction() == Fraction.GOBLIN) {
					handPhase2A.add(cardToWin);
					handPhase2B.add(coveredCard);
					game.setRoundWinner(game.getPlayerA().getAccount().getUsername());
				}
				
				if (playedCardB.getFraction() == Fraction.KNIGHT && playedCardA.getFraction() == Fraction.GOBLIN) {
					game.setRoundWinner(game.getPlayerB().getAccount().getUsername());
					handPhase2A.add(coveredCard);
					handPhase2B.add(cardToWin);
					game.setRoundWinner(game.getPlayerB().getAccount().getUsername());
				}
			}
			
			if (game.getCurrentPlayer().getId() == game.getPlayerB().getId()) {
				if (playedCardB.getFraction() == Fraction.KNIGHT && playedCardA.getFraction() == Fraction.GOBLIN) {
					handPhase2A.add(coveredCard);
					handPhase2B.add(cardToWin);
					game.setRoundWinner(game.getPlayerB().getAccount().getUsername());
				}
				if (playedCardA.getFraction() == Fraction.KNIGHT && playedCardB.getFraction() == Fraction.GOBLIN) {
					handPhase2A.add(cardToWin);
					handPhase2B.add(coveredCard);
					game.setRoundWinner(game.getPlayerA().getAccount().getUsername());
				}
			}
			
			ruleUndead(game, playedCardA, playedCardB);
			
			game.getPlayerA().setCardsPhase2(handPhase2A);
			playerRepository.save(pA);

			game.getPlayerB().setCardsPhase2(handPhase2B);
			playerRepository.save(pB);
	 	
	 	
		// played card werden auf null gesetzt
		pA.setPlayedCards(new ArrayList<Card>());
		pB.setPlayedCards(new ArrayList<Card>());
		
		return game;
	}
	
	private Game phase2(Game game, Card playerACard, Card playerBCard) {

		 /** Get specific Game & Player from database */
			var pA = game.getPlayerA();
			var pB = game.getPlayerB();
		  
		 /**
			 * List for pointStack per Player (List was already initialized in phase 1 for
			 * UNDEAD logic)
			 */
		  List<Card> pointStackPlayerA = pA.getPointStack(); 
		  List<Card> pointStackPlayerB = pB.getPointStack();
		  
		 /**
			 * Get the playedCards List and store the Cards into objects.
			 */
		  ArrayList<Card> playedCards = new ArrayList<Card>();
		  playedCards.add(playerACard);
		  playedCards.add(playerBCard);
		  
		 /**
			 * Iteration through List playedCards for following switch, which defines rules
			 * of phase 2:
			 */
		 /**
			 * Case: Player A wins & plays DWARF DWARF-Card (at List-position 0) goes into
			 * pointsStack of Player B (loosers pointStack).
			 */
		
		  if ((isWinner(game, playerACard, playerBCard) == true)
				  && (playerACard.getFraction() == Fraction.DWARF || 
						  playerBCard.getFraction() == Fraction.DWARF)) { 
			  pointStackPlayerB.add(playedCards.get(0));
			  pointStackPlayerA.add(playedCards.get(1));
			  
			  //set RoundWinner
			  game.setRoundWinner(game.getPlayerA().getAccount().getUsername());
		  
		 /**
			 * Case: Player A looses & plays DWARF DWARF-Card (at List-position 0) goes into
			 * pointsStack of Player A (loosers pointStack).
			 */
		  } else if ((isWinner(game, playerACard, playerBCard) == false) 
				  && (playerACard.getFraction() == Fraction.DWARF || 
				  playerBCard.getFraction() == Fraction.DWARF)){ 
			  pointStackPlayerA.add(playedCards.get(0));
			  pointStackPlayerB.add(playedCards.get(1));
		  
		  //set RoundWinner
		  game.setRoundWinner(game.getPlayerB().getAccount().getUsername());
		  
		  } 
		  
		  
		  if (playerACard.getFraction() != Fraction.DWARF 
				  && playerBCard.getFraction() != Fraction.DWARF){
			if (isWinner(game, playerACard, playerBCard) 
		  				&& !ruleKnight(game, playerACard, playerBCard)) { 
		  			pointStackPlayerA.addAll(playedCards);
			  
		  			//set RoundWinner
		  			game.setRoundWinner(game.getPlayerA().getAccount().getUsername());
			} else if (!ruleKnight(game, playerACard, playerBCard)) {
		  			pointStackPlayerB.addAll(playedCards); 	
	  
		  			//set RoundWinner
		  			game.setRoundWinner(game.getPlayerB().getAccount().getUsername());
	  
		  			}
		  	}
		  
		  playerRepository.save(pA); 
		  playerRepository.save(pB);
		  
		/**
		 * Get List of playedCards from database and remove its cards in Card objects
		 * (List should be empty per round)
		 */
		pA.setPlayedCards(new ArrayList<Card>());
		pB.setPlayedCards(new ArrayList<Card>());

		return game;
	}
	
	
	// ------ SPIELLOGIK - RULES ------- //
	
	/**@Support methods (see private): */
	/**Compares 2 cards by "Fraction" */
	
	private boolean compareFraction(Card playerACard, Card playerBCard) {
		if (playerACard.getFraction().name() == playerBCard.getFraction().name()) {
			return true;
		}
		return false;
	}
	
	/**Method compares Card values and returns true
	 * for the higher value  */
	private boolean compareValue(Card playerACard, Card playerBCard) {
		if (playerACard.getValue() > playerBCard.getValue()) {
			return true;
		}
		return false;
	}
	
	/**Doppelganger clones the opponents Fraction
	 * Method returns true when the combination of
	 * Doppelganger with every other Fraction
	 * (including Doppelganger itself) occurs */
	private boolean doppelgangerCloneRule(Game game, Card playerACard, Card playerBCard) {
		if (game.getCurrentPlayer().getId() == game.getPlayerA().getId()) {
			if (playerACard.getFraction().name() == "DOPPELGANGER" && (playerBCard.getFraction().name() == "GOBLIN"
					|| playerBCard.getFraction().name() == "KNIGHT" || playerBCard.getFraction().name() == "DOPPELGANGER"
					|| playerBCard.getFraction().name() == "UNDEAD" || playerBCard.getFraction().name() == "DWARF")) {
				return true;
			} 
			
			if (playerBCard.getFraction().name() == "DOPPELGANGER" && (playerACard.getFraction().name() == "GOBLIN"
				|| playerACard.getFraction().name() == "KNIGHT" || playerACard.getFraction().name() == "DOPPELGANGER"
				|| playerACard.getFraction().name() == "UNDEAD" || playerACard.getFraction().name() == "DWARF")) {
				return true;
			}
				
		}
		
		if (game.getCurrentPlayer().getId() == game.getPlayerB().getId()) {
			if (playerBCard.getFraction().name() == "DOPPELGANGER" && (playerACard.getFraction().name() == "GOBLIN"
					|| playerACard.getFraction().name() == "KNIGHT" || playerACard.getFraction().name() == "DOPPELGANGER"
					|| playerACard.getFraction().name() == "UNDEAD" || playerACard.getFraction().name() == "DWARF")) {
				return true;
			} 
			
			if (playerACard.getFraction().name() == "DOPPELGANGER" && (playerBCard.getFraction().name() == "GOBLIN"
				|| playerBCard.getFraction().name() == "KNIGHT" || playerBCard.getFraction().name() == "DOPPELGANGER"
				|| playerBCard.getFraction().name() == "UNDEAD" || playerBCard.getFraction().name() == "DWARF")) {
				return true;
			}
		}
		return false;
	}
	
	/**Doppelganger clones the opponents Fraction:
	 * Method returns true for higher Card value
	 * and for a Doppelganger-Card in combination with
	 * any other fraction.
	 * Because Doppelganger-Fraction can only win with a higher
	 * Card value */
	private boolean doppelgangerValueRule(Game game, Card playerACard, Card playerBCard) {
		if (doppelgangerCloneRule(game, playerACard, playerBCard) && compareValue(playerACard, playerBCard)) {
			return true;
		}
		return false;
	}
	
	/**In case of draw:
	 * Method returns true with same Fraction and
	 * Card value */
	private boolean draw(Game game, Card playerACard, Card playerBCard) {
		
		if (game.getCurrentPlayer().getId() == game.getPlayerA().getId()) {
		if (playerACard.getValue() == playerBCard.getValue() 
				&& playerACard.getFraction().name() == playerBCard.getFraction().name()) {
			return true;
			}
		}
		
		if (game.getCurrentPlayer().getId() == game.getPlayerA().getId()) {
			if (playerACard.getValue() == playerBCard.getValue() 
					&& doppelgangerCloneRule(game, playerACard, playerBCard)) {
				return true;
				}
			}
		
		return false;
	}
	
	/**Method returns true if...
	 * -two Cards have different Fractions
	 * -Knight-Rule does not occur
	 * -Doppelganger-Rule does not occur
	 *
	 * Because this would mean the opponent
	 * had no other choice than making a
	 * "random" move, which would
	 * automatically cause him to lose */
	private boolean effectlessMoveRule(Game game, Card playerACard, Card playerBCard) {
		if (game.getCurrentPlayer().getId() == game.getPlayerA().getId()) {
			if (!compareFraction(playerACard, playerBCard) 
				&& !doppelgangerCloneRule(game, playerACard, playerBCard)) {
			return true;
			}
		}
		return false;
	}
	
	/**Method returns true for the higher
	 * Card value and if both Cards have
	 * the same Fraction type */
	private boolean isHigherValueBySameFraction(Card playerACard, Card playerBCard) {
		if (compareFraction(playerACard, playerBCard) && compareValue(playerACard, playerBCard)) {
			return true;
		}
		return false;
	}
	
	/**Method describes requirements for a winner*/
	private boolean isWinner(Game game, Card playerACard, Card playerBCard) {
		if (doppelgangerValueRule(game, playerACard, playerBCard)
				|| effectlessMoveRule(game, playerACard, playerBCard)
					|| draw(game, playerACard, playerBCard)
						|| isHigherValueBySameFraction(playerACard, playerBCard)) {
			return true;
		}
		return false;
	}
	
	/**Knight-Fraction has to beat Goblin-Fraction
	 * Method returns true if a Knight-Card meets a
	 * Goblin-Card  */
	private boolean ruleKnight(Game game, Card playerACard, Card playerBCard) {
		
		if (game.getCurrentPlayer().getId() == game.getPlayerA().getId()) {
			if (playerACard.getFraction().name() == "KNIGHT" && playerBCard.getFraction().name() == "GOBLIN") {
				game.setRoundWinner(game.getPlayerA().getAccount().getUsername());
				return true;
			}
			
			if (playerBCard.getFraction().name() == "KNIGHT" && playerACard.getFraction().name() == "GOBLIN") {
				game.setRoundWinner(game.getPlayerB().getAccount().getUsername());
				return true;
			}
		}
		
		if (game.getCurrentPlayer().getId() == game.getPlayerB().getId()) {
			if (playerBCard.getFraction().name() == "KNIGHT" && playerACard.getFraction().name() == "GOBLIN") {
				game.setRoundWinner(game.getPlayerB().getAccount().getUsername());
				return true;
			}
			if (playerACard.getFraction().name() == "KNIGHT" && playerBCard.getFraction().name() == "GOBLIN") {
				game.setRoundWinner(game.getPlayerA().getAccount().getUsername());
				return true;
			}
		}
		
		return false;
	}
	
	// ----- LÖSCHEN VON GAMES ---- //

	/**Löschen von Games nach Username oder Email*/
	public void removeUserGames(UserDetails user) {
		this.gameRepository.deleteAllByPlayerA_Account_EmailOrPlayerB_Account_Email(user.getUsername(), user.getUsername());
	}
	
	/**Löschen von allen Games*/
	public void removeAllGames() {
		gameRepository.deleteAll();
	}
}
