package com.claim.model;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/** Represents a Card.
 * @author Deborah Vanzin
 * -Creation of class (basic structure)
 * 
 * @author Rocco Saracino und Valentina Caldana
 * 	-ManyToOne annotation
 *  -Extention Constructor (Fraction & CardDeck)
*/

@Entity
public class Card {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	// This annotation specifies an Enumeration Property is being mapped to database
	@Enumerated(EnumType.STRING)
	private Fraction fraction;
	private Integer value;
	private Blob image;

	
	protected Card() {}
	
	public Card (Integer id, Fraction fraction, Integer value, Blob image) {
		this.id = id;
		this.fraction = fraction;
		this.value = value;
		this.image = image;
	}

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
