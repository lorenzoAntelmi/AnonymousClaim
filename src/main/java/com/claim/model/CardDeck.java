package com.claim.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a CardDeck.
 *
 * @author Deborah Vanzin
 * -basic structure
 */

@Entity
public class CardDeck {

	/**Generates a cardDeckID*/
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	/**
	 * @OneToOne association = one CardDeck to one Game
	 * @JsonIgnore = Declares the game property to be ignored when serializing to
	 *             JSON
	 */
	@OneToOne
	@JsonIgnore
	private Game game;

	/**
	 * @OneToMany association = one CardDeck to many Cards
	 * @OrderColumn = to maintain the persistent order of a list source:
	 *              https://www.concretepage.com/hibernate/example-ordercolumn-hibernate
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@OrderColumn
	private List<Card> cards;

	/**
	 * Default Constructor: Object relational mapper (ORM) is a Framework, which
	 * mapps objects in database. In order for the mapper to create an object, it
	 * needs a default constructor
	 */
	public CardDeck() {	}

	public CardDeck(Integer id, Game game, List<Card> cards) {
		super(); //WIESO
		this.id = id;
		this.game = game;
		this.cards = cards;
		/**List is not instantiated yet
		 * otherwise we would have to give
		 * an instantiated List to the parameters
		 */
	}


	/**
	 * @Getter & @Setter
	 */
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}


}
