package com.claim.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@OneToOne
	@JsonIgnore //Declares the game property to be ignored when serializing to JSON
	private Game game;	
	
	@OneToMany
	@OrderColumn
	private List<Card> cards = new ArrayList<>();
	
	protected CardDeck() {}
	
	
	public CardDeck(Game game) {
		this.game = game;
	}

	public Integer getId() {
		return id;
	}

	protected void setId(Integer id)  {
		this.id = id;
	}

	public Game getGame() {
		return game;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	
}
