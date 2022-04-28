package com.claim.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** Represents the Cards of every Players hands.
 * @author Deborah Vanzin
*/

@Entity
public class HandCard {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	//private Integer accountId;
	@ManyToOne
	private Card card;
	@ManyToOne
	@JsonIgnore //Declares the game property to be ignored when serializing to JSON
	private Game game;
	
	//Default Constructor. Objektrelationalermapper ist ein Framework das Objekte in die Datenbank mappt. Damit der Mapper ein Objekt erstellen kann braucht es einen
	protected HandCard() {
		
	}
	
	public HandCard(Game game, Card card) {
		this.card = card;
		this.game = game;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Integer getId() {
		return id;
	}

	public Card getCard() {
		return card;
	}

	public Game getGame() {
		return game;
	}
	
	
}