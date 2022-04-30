package com.claim.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/** Represents a Game.
 * @author Deborah Vanzin
*/

@Entity
public class Game {
	
	/** Generates a gameID
	*/
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private Integer playerA;
	private Integer playerB;
	private Integer phase;
	
	/**@OneToMany association = one Game to many handCards &
	 * one Game to one CardDeck
	 * @cascadeType.ALL = persistence gives/passes on, all EntityManager-operations (= PERSIST, REMOVE, REFRESH, MERGE, DETACH) 
	 * to the corresponding entities. So "cascade" tells the ORM to propagate all operations
	 * source: https://stackoverflow.com/questions/13027214/what-is-the-meaning-of-the-cascadetype-all-for-a-manytoone-jpa-association
	*/
	@OneToMany(cascade = CascadeType.ALL)
	private List<HandCard> cardsPlayerA = new ArrayList<>();
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<HandCard> cardsPlayerB = new ArrayList<>();
	
	@OneToOne(cascade = CascadeType.ALL)
	private CardDeck cardDeck;
	

	/**Default Constructor: Object relational mapper (ORM) is a Framework, which
	 * mapps objects in database. In order for the mapper to create an object,
	 * it needs a default constructor
	*/
	public Game() {}

	public Game(Integer id, Integer playerA, Integer playerB, Integer phase) {
		super();
		this.id = id;
		this.playerA = playerA;
		this.playerB = playerB;
		this.phase = phase;
	}
	

	/**@Getter & @Setter (parameters Game)
	*/
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getPlayerA() {
		return playerA;
	}
	
	public void setPlayerA(Integer playerA) {
		this.playerA = playerA;
	}
	
	public void setPlayerB(Integer playerB) {
		this.playerB = playerB;
	}
	
	public Integer getPlayerB() {
		return playerB;
	}
	
	public Integer getPhase() {
		return phase;
	}
	
	public void setPhase(int phase) {
		this.phase = phase;
	}
	
	public void setPhase(Integer phase) {
		this.phase = phase;
	}
	
	/**@Getter & @Setter "CardDeck"
	*/
	public CardDeck getCardDeck() {
		return cardDeck;
	}

	public void setCardDeck(CardDeck cardDeck) {
		this.cardDeck = cardDeck;
	}

	/**@Getter & @Setter "HandCards" & "Cards of Players"
	*/

	public void setCardsPlayerA(List<HandCard> cardsPlayerA) {
		this.cardsPlayerA = cardsPlayerA;
	}

	public void setCardsPlayerB(List<HandCard> cardsPlayerB) {
		this.cardsPlayerB = cardsPlayerB;
	}

	public List<HandCard> getCardsPlayerA() {
		return cardsPlayerA;
	}

	public List<HandCard> getCardsPlayerB() {
		return cardsPlayerB;
	}
}
