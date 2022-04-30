package com.claim.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Represents a move of Player.
 * @author Deborah Vanzin
*/

@Entity
public class Move {
	
	/** Generates a moveID
	*/
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private Integer handCardsId;
	
	/**Default Constructor: Object relational mapper (ORM) is a Framework, which
	 * mapps objects in database. In order for the mapper to create an object,
	 * it needs a default constructor
	*/
	protected Move() {}
	
	public Move(Integer id, Integer handCardsId) {
		this.id = id;
		this.handCardsId = handCardsId;
	}

	/**@Getter
	*/
	public Integer getId() {
		return id;
	}

	public Integer getHandCardsId() {
		return handCardsId;
	}
}
