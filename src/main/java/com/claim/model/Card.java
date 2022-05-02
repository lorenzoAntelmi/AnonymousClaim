package com.claim.model;

import java.sql.Blob;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/** Represents a card.
 * @author Deborah Vanzin
 * -basic structure
*/

@Entity
public class Card {
	
	/** Generates a cardID
	*/
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	/**@ManyToOne association = Many Cards to one Player/Game
	*/
	@ManyToOne
	private Player player;
	
	@ManyToOne
	private Game game;
	
	/** This annotation specifies an Enumeration Property is being mapped to database
	*/
	@Enumerated(EnumType.STRING)
	private Fraction fraction;
	private Integer value;
	private Blob image;

	/**Default Constructor: Object relational mapper (ORM) is a Framework, which
	 * mapps objects in database. In order for the mapper to create an object,
	 * it needs a default constructor
	*/
	public Card() {}
	
	public Card (Integer id, Fraction fraction, Integer value, Blob image) {
		this.id = id;
		this.fraction = fraction;
		this.value = value;
		this.image = image;
	}

	/**@Getter & @Setter
	*/
	public Integer getId() {
		return id;
	}

	public Fraction getFraction() {
		return fraction;
	}

	public Integer getValue() {
		return value;
	}

	public Blob getImage() {
		return image;
	}	
}
