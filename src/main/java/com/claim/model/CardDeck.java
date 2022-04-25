package com.claim.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/** Represents a CardDeck.
 * @author Deborah Vanzin
 * -Creation of class (basic structure)
 * 
@author Rocco Saracino und Valentina Caldana
 * 	-OneToMany annotation
 *  -Adjustion Constructor (cardID removed)
*/

@Entity
public class CardDeck {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private Integer gameId;	
	
	@OneToMany
	private List<Card> cards;
	
	
	public CardDeck(Integer id, Integer gameId) {
		this.id = id;
		this.gameId = gameId;
	}

	public Integer getId() {
		return id;
	}

	public Integer getGameId() {
		return gameId;
	}

}
