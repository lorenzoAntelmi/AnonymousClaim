package com.claim.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Represents the Cards of every Players hands.
 * @author Deborah Vanzin
*/

@Entity
public class HandCards {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private Integer accountId;
	private Integer cardId;
	private Integer gameId;
	
	public HandCards(Integer id, Integer accountId, Integer cardId, Integer gameId) {
		this.id = id;
		this.accountId = accountId;
		this.cardId = cardId;
		this.gameId = gameId;
	}

	public Integer getId() {
		return id;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public Integer getCardId() {
		return cardId;
	}

	public Integer getGameId() {
		return gameId;
	}
}
