package com.claim.model;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/** Represents a Game.
 * @author Deborah Vanzin*/

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
	 * source: https://stackoverflow.com/questions/13027214/what-is-the-meaning-of-the-cascadetype-all-for-a-manytoone-jpa-association*/
	@OneToOne(cascade = CascadeType.ALL)
	private Player playerA;

	@OneToOne(cascade = CascadeType.ALL)
	private Player playerB;

	/**Is PlayerA or PlayerB, depending on who makes the move*/
	@OneToOne(cascade = CascadeType.ALL)
	private Player currentPlayer;
	
	// Variable damit der Winner pro Runde ausgegeben werden kann
	private String roundWinner;
	
	// Variable damit der Winner pro Phase ausgegeben werden kann
	private String gameWinner;

	public String getGameWinner() {
		return gameWinner;
	}

	public void setGameWinner(String gameWinner) {
		this.gameWinner = gameWinner;
	}

	public String getRoundWinner() {
		return roundWinner;
	}

	public void setRoundWinner(String roundWinner) {
		this.roundWinner = roundWinner;
	}

	/**@OneToOne association = one Game to one CardDeck*/
	@OneToOne(cascade = CascadeType.ALL)
	private CardDeck cardDeck;


	/**Default Constructor: Object relational mapper (ORM) is a Framework, which
	 * mapps objects in database. In order for the mapper to create an object,
	 * it needs a default constructor*/
	public Game() {}

	/**@Getter & @Setter */
	
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
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	
	public String getPhaseWinner() {
		return gameWinner;
	}

	public void setPhaseWinner(String phaseWinner) {
		this.gameWinner = phaseWinner;
	}

	/**@Getter & @Setter "CardDeck"	*/
	public CardDeck getCardDeck() {
		return cardDeck;
	}

	public void setCardDeck(CardDeck cardDeck) {
		this.cardDeck = cardDeck;
	}

}
