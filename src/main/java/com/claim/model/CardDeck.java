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
 * -basic structure
*/

@Entity
public class CardDeck {
	
	/** Generates a cardDeckID
	*/
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	/**@OneToOne association = one CardDeck to one Game
	 * @JsonIgnore = Declares the game property to be ignored when serializing to JSON
	*/
	@OneToOne
	@JsonIgnore 
	private Game game;	
	
	/**@OneToOne association = one CardDeck to many Cards
	 * @OrderColumn = to maintain the persistent order of a list
	 * source: https://www.concretepage.com/hibernate/example-ordercolumn-hibernate
	*/
	@OneToMany
	@OrderColumn
	private List<Card> cards = new ArrayList<>();
	
	/**Default Constructor: Object relational mapper (ORM) is a Framework, which
	 * mapps objects in database. In order for the mapper to create an object,
	 * it needs a default constructor
	*/
	protected CardDeck() {}
	
	
	public CardDeck(Game game) {
		this.game = game;
	}

	/**@Getter & @Setter
	*/
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
