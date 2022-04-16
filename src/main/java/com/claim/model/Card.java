package com.claim.model;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Represents a Card.
 * @author Deborah Vanzin
*/

@Entity
public class Card {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private Integer fraction;
	private Integer value;
	private Blob image;
	
	public Card (Integer id, Integer fraction, Integer value, Blob image) {
		this.id = id;
		this.fraction = fraction;
		this.value = value;
		this.image = image;
	}

	public Integer getId() {
		return id;
	}

	public Integer getFraction() {
		return fraction;
	}

	public Integer getValue() {
		return value;
	}

	public Blob getImage() {
		return image;
	}	
}
