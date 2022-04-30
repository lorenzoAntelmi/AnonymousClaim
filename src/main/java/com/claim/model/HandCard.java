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
	
	/** Generates a handCardID
	*/
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	
	/** "Integer id" refers to accountId
	*/
	private Integer id;

	/**@ManyToOne association = !!!!!!!! One HandCard to many Cards? 
	*/
	@ManyToOne
	private Card card;
	
	/**@ManyToOne association = many HandCards to one Game
	 * @JsonIgnore = Declares the game property to be ignored when serializing to JSON
	*/
	@ManyToOne
	@JsonIgnore 
	private Game game;
	
	/**Default Constructor: Object relational mapper (ORM) is a Framework, which
	 * mapps objects in database. In order for the mapper to create an object,
	 * it needs a default constructor
	*/
	protected HandCard() {}
	
	public HandCard(Game game, Card card) {
		this.card = card;
		this.game = game;
	}

	
	/**@Getter & @Setter
	*/
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