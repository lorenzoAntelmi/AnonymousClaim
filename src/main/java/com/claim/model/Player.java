package com.claim.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**Represents a Player.
 * @author Rocco Saracino & Valentina Caldana */

@Entity
public class Player {

	/** Generates a playerID*/
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@OneToOne
	private Account account;

	/**@ManyToMany association = many Player to many hand*/
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Card> hand;

	/**@OneToOne association = one Player to one Game*/
	@OneToOne(cascade = CascadeType.ALL)
	@JsonIgnore
	private Game game;

	/**@OneToMany association = one Player to many hand*/
	@OneToMany(cascade = CascadeType.ALL)
	private List<Card> cardsPhase2;

	/**@OneToMany association = one Game to many playedCards*/
	@OneToMany(cascade = CascadeType.ALL)
	private List<Card> playedCards;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Card> pointStack;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Card> depositedCard;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Card> depositedCardPhase2;

	/** Default Constructor: Object relational mapper (ORM) is a Framework, which
	 * maps objects in database. In order for the mapper to create an object, it
	 * needs a default constructor */
	protected Player() {
	}

	public Player(Account account, List<Card> hand, Game game, List<Card> cardsPhase2,
			List<Card> pointStack) {
		this.account = account;
		this.game = game;
		this.hand = hand;
		this.cardsPhase2 = cardsPhase2;
		this.pointStack = pointStack;
	}

	/**@Getter & @Setter */
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public List<Card> getHand() {
		return hand;
	}

	public void setHand(List<Card> hand) {
		this.hand = hand;
	}

	public List<Card> getPointStack() {
		return pointStack;
	}

	public void setPointStack(List<Card> pointStack) {
		this.pointStack = pointStack;
	}

	/**@Getter & @Setter
	 * List of played cards from PlayerA & PlayerB
	 * (List should have only 2 cards per round)*/

	public List<Card> getPlayedCards() {
		return playedCards;
	}

	public void setPlayedCards(List<Card> playedCards) {
		this.playedCards = playedCards;
	}
	
	public List<Card> getDepositedCard() {
		return depositedCard;
	}

	public void setDepositedCard(List<Card> depositedCard) {
		this.depositedCard = depositedCard;
	}

	public List<Card> getCardsPhase2() {
		return cardsPhase2;
	}
	
	public void setCardsPhase2(List<Card> cardsPhase2) {
		this.cardsPhase2 = cardsPhase2;
	}

	public List<Card> getDepositedCardPhase2() {
		return depositedCardPhase2;
	}

	public void setDepositedCardPhase2(List<Card> depositedCardPhase2) {
		this.depositedCardPhase2 = depositedCardPhase2;
	}


}
