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

@Service
public class GameService {
	
	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private CardRepository cardRepository;

	public Game initializeGame(Integer playerAId) {
		
		// Erstelle ein neues Spiel
		Game game = new Game();
		game.setPlayerA(playerAId);
		
		// Wir holen uns alle Karten (52) aus der Datenbank und verteilen sie.
		List<Card> cards = cardRepository.findAll();
		Collections.shuffle(cards);

		// Verteilung der Karten an die Spiele
		for (int i = 0 ; i  < 13; i++) {
			Card nextCardA = cards.remove(cards.size()-1);
			Card nextCardB = cards.remove(cards.size() -1);
			game.getCardsPlayerA().add(new HandCard(game, nextCardA));
			game.getCardsPlayerB().add(new HandCard(game, nextCardB));
		}
		
		// Übrig gebliebene Karten, werden auf das Deck gegeben.
		CardDeck deck = new CardDeck(game);
		for (Card card : cards) {
			deck.getCards().add(card);
		}
		
		game.setCardDeck(deck);
		gameRepository.save(game);
		return game;

	}



}
