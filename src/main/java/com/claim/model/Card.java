package com.claim.model;

import java.sql.Blob;

import javax.persistence.Entity;
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
	private Fraction fraction;
	private Integer value;
	private Blob image;
	
	@ManyToOne
	private CardDeck cardDeck;
	
	
	public Card (Integer id, Fraction fraction, Integer value, Blob image, CardDeck cardDeck) {
		this.id = id;
		this.fraction = fraction;
		this.value = value;
		this.image = image;
		this.cardDeck = cardDeck;
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
