package com.claim.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Represents a CardDeck.
 * @author Deborah Vanzin
*/

@Entity
public class CardDeck {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private Integer gameId;
	private Integer cardId;
	
	public CardDeck(Integer id, Integer gameId, Integer cardId) {
		this.id = id;
		this.gameId = gameId;
		this.cardId = cardId;
	}

	public Integer getId() {
		return id;
	}

	public Integer getGameId() {
		return gameId;
	}

	public Integer getCardId() {
		return cardId;
	}
}
