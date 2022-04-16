package com.claim.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Represents a move of Player.
 * @author Deborah Vanzin
*/

@Entity
public class Move {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private Integer handCardsId;
	
	public Move(Integer id, Integer handCardsId) {
		this.id = id;
		this.handCardsId = handCardsId;
	}

	public Integer getId() {
		return id;
	}

	public Integer getHandCardsId() {
		return handCardsId;
	}
}
