package com.claim.model;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Represents a card.
 *
 * @author Deborah Vanzin
 * -basic structure
 *
 * @author Rocco Saracino & Valentina Caldana
 * -rules for cards (all rule-related methods)
 */

@Entity
public class Card {

	/**
	 * Generates a cardID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	/**
	 * @ManyToOne association = Many Cards to one Player/Game/CardDeck
	 */
	@ManyToOne
	private Player player;

	@ManyToOne
	private CardDeck cardDeck;

	@ManyToOne
	private Game game;

	/**
	 * This annotation specifies an Enumeration Property is being mapped to database
	 */
	@Enumerated(EnumType.STRING)
	private Fraction fraction;
	private Integer value;
	private Blob image;

	/**
	 * Default Constructor: Object relational mapper (ORM) is a Framework, which
	 * mapps objects in database. In order for the mapper to create an object, it
	 * needs a default constructor
	 */
	public Card() {
	}

	public Card(Integer id, Fraction fraction, Integer value, Blob image) {
		this.id = id;
		this.fraction = fraction;
		this.value = value;
		this.image = image;
	}

	/**
	 * @Getter & @Setter
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

	/**@Support methods (see private): */

	/**Compares 2 cards by "Fraction" */
	private boolean compareFraction(Card otherCard) {
		boolean bo = false;
		if (this.getFraction().name() == otherCard.getFraction().name()) {
			bo = true;
		}
		return bo;
	}

	/**Method compares Card values and returns true
	 * for the higher value  */
	public boolean compareValue(Card otherCard) {
		boolean bo = false;

		if (this.getValue() > otherCard.getValue()) {
			bo = true;
		}
		return bo;
	}

	/**Knight-Fraction has to beat Goblin-Fraction
	 * Method returns true if a Knight-Card meets a
	 * Goblin-Card  */
	private boolean ruleKnight(Card otherCard) {
		boolean bo = false;
		if (this.getFraction().name() == "KNIGHT" && otherCard.getFraction().name() == "GOBLIN") {
			bo = true;
		}
		return bo;
	}

	/**Doppelganger clones the opponents Fraction
	 * Method returns true when the combination of
	 * Doppelganger with every other Fraction
	 * (including Doppelganger itself) occurs */
	private boolean doppelgangerCloneRule(Card otherCard) {
		boolean bo = false;

		if (this.getFraction().name() == "DOPPELGANGER" && (otherCard.getFraction().name() == "GOBLIN"
				|| otherCard.getFraction().name() == "KNIGHT" || otherCard.getFraction().name() == "DOPPELGANGER"
				|| otherCard.getFraction().name() == "UNDEAD" || otherCard.getFraction().name() == "DWARF")) {
			bo = true;
		}
		return bo;

	}

	/**Doppelganger clones the opponents Fraction:
	 * Method returns true for higher Card value
	 * and for a Doppelganger-Card in combination with
	 * any other fraction.
	 * Because Doppelganger-Fraction can only win with a higher
	 * Card value */
	public boolean doppelgangerValueRule(Card otherCard) {
		boolean bo = false;
		if (this.doppelgangerCloneRule(otherCard) && this.compareValue(otherCard)) {
			bo = true;
		}
		return bo;
	}

	/**In case of draw:
	 * Method returns true with same Fraction and
	 * Card value */
	public boolean draw(Card otherCard) {
		boolean bo = false;
		if (this.getValue() == otherCard.getValue() && this.getFraction().name() == otherCard.getFraction().name()) {
			return bo = true;
		}
		return bo;
	}

	/**Method returns true if...
	 * -two Cards have different Fractions
	 * -Knight-Rule does not occur
	 * -Doppelganger-Rule does not occur
	 *
	 * Because this would mean the opponent
	 * had no other choice than making a
	 * "random" move, which would
	 * automatically cause him to lose */
	public boolean effectlessMoveRule(Card otherCard) {
		boolean bo = false;
		if (!this.compareFraction(otherCard) &&
				!this.ruleKnight(otherCard)
				&& !this.doppelgangerCloneRule(otherCard)) {
			bo = true;
		}
		return bo;
	}

	/**Method returns true for the higher
	 * Card value and if both Cards have
	 * the same Fraction type */
	public boolean isHigherValueBySameFraction(Card otherCard) {
		boolean bo = false;
		if (this.compareFraction(otherCard) && this.compareValue(otherCard)) {
			bo = true;
		}
		return bo;
	}

	/**Method describes requirements for a winner*/
	public boolean isWinner(Card otherCard) {
		boolean bo = false;
		if (this.doppelgangerValueRule(otherCard)
			 || this.effectlessMoveRule(otherCard)
				|| this.ruleKnight(otherCard)
					|| this.draw(otherCard)
						|| this.isHigherValueBySameFraction(otherCard)) {
			bo = true;
		}
		return bo;
	}

	public boolean ruleUndead(Card otherCard) {
		boolean bo = false;
		if (this.getFraction().name() == "UNDEAD" && otherCard.getFraction().name() == "UNDEAD") {
			bo = true;
		} else if (this.getFraction().name() == "UNDEAD" || otherCard.getFraction().name() == "UNDEAD"){
			bo = true;
		}


		return bo;
	}

}
