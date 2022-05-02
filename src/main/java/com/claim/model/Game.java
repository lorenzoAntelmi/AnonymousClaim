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
	private Integer phase;
	
	/**@OneToOne association = one Game to one Player 
	 * @cascadeType.ALL = persistence gives/passes on, all EntityManager-operations (= PERSIST, REMOVE, REFRESH, MERGE, DETACH) 
	 * to the corresponding entities. So "cascade" tells the ORM to propagate all operations
	 * source: https://stackoverflow.com/questions/13027214/what-is-the-meaning-of-the-cascadetype-all-for-a-manytoone-jpa-association
	*/
	@OneToOne(cascade = CascadeType.ALL)
	private Player playerA;
	@OneToOne(cascade = CascadeType.ALL)
	private Player playerB;
	
	/**@OneToMany association = one Game to many playedCards 
	*/
	@OneToMany(cascade = CascadeType.ALL)
	private List<Card> playedCards;
	
	/**@OneToOne association = one Game to one CardDeck 
	*/
	@OneToOne(cascade = CascadeType.ALL)
	private CardDeck cardDeck;
	

	/**Default Constructor: Object relational mapper (ORM) is a Framework, which
	 * mapps objects in database. In order for the mapper to create an object,
	 * it needs a default constructor
	*/
	public Game() {}

	public Game(Integer id, Player playerA, Player playerB, Integer phase) {
		super();
		this.id = id;
		this.playerA = playerA;
		this.playerB = playerB;
		this.phase = phase;
		this.playedCards = new ArrayList<>();
	}
	

	/**@Getter & @Setter (parameters Game)
	*/
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Player getPlayerA() {
		return playerA;
	}
	
	public void setPlayerA(Player playerA) {
		this.playerA = playerA;
	}
	
	public void setPlayerB(Player playerB) {
		this.playerB = playerB;
	}
	
	public Player getPlayerB() {
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
	
	/**@Getter & @Setter
	 * List of played cards from PlayerA & PlayerB
	 * (List should have only 2 cards per round)
	*/
	
	public List<Card> getPlayedCards() {
		return playedCards;
	}

	public void setPlayedCards(List<Card> playedCards) {
		this.playedCards = playedCards;
	}
	
	/**@Getter & @Setter "CardDeck"
	*/
	public CardDeck getCardDeck() {
		return cardDeck;
	}

	public void setCardDeck(CardDeck cardDeck) {
		this.cardDeck = cardDeck;
	}

}
