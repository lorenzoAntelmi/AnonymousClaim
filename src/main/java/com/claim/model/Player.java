package com.claim.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/** Represents a Player.
 * @author Rocco Saracino & Valentina Caldana */

@Entity
public class Player {
	
	/** Generates a playerID
	*/
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private Integer accountId;

	/**@OneToMany association = one Player to many hand
	 * @cascadeType.ALL = persistence gives/passes on, all EntityManager-operations (= PERSIST, REMOVE, REFRESH, MERGE, DETACH) 
	 * to the corresponding entities. So "cascade" tells the ORM to propagate all operations
	 * source: https://stackoverflow.com/questions/13027214/what-is-the-meaning-of-the-cascadetype-all-for-a-manytoone-jpa-association
	*/
	@OneToMany(cascade = CascadeType.ALL)
	private List<Card> hand = new ArrayList<>();
	
	/**@ManyToOne association = Many Player to one Game
	*/
	@ManyToOne
	private Game game;
	
	/**Default Constructor: Object relational mapper (ORM) is a Framework, which
	 * mapps objects in database. In order for the mapper to create an object,
	 * it needs a default constructor
	*/
	protected Player() {}

	public Player (Integer id, Integer accountId, Game game) {
		this.id = id;
		this.accountId = accountId;
		this.game = game;
		this.hand = new ArrayList<Card>();
	}

	/**@Getter & @Setter
	*/
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	/**@Getter & @Setter
	 * Hand of Player
	*/
	public List<Card> getHand() {
		return hand;
	}

	public void setHand(List<Card> hand) {
		this.hand = hand;
	}
	

}
