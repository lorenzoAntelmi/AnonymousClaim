package com.claim.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents the ranking.
 * 
 * @author Hanna Kroopf
 */

public class Ranking {

	/**
	 * Generates a ranking
	 */
	
	private Integer rank;
	private String username;
	private Integer score;

	//Konstruktor um ein Ranking-Objekt zu kreieren/
	public Ranking() {
		
	}

	//@Getter & @Setter/
	public Integer getRank() {
		return rank;
	}

	public String getUsername() {
		return username;
	}

	public Integer getScore() {
		return score;
	}

	
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public void setScore(Integer score) {
		this.score = score;

	}

}
