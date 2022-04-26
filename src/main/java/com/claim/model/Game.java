package com.claim.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Represents a Game.
 * @author Deborah Vanzin
*/

@Entity
public class Game {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private Integer playerA;
	private Integer playerB;
	private Integer phase;
	
	public Game() {}

	public Game(Integer id, Integer playerA, Integer playerB, Integer phase) {
		super();
		this.id = id;
		this.playerA = playerA;
		this.playerB = playerB;
		this.phase = phase;
	}
	
	public Integer getId() {
		return id;
	}
	
	public Integer getPlayerA() {
		return playerA;
	}
	
	public void setPlayerA(Integer playerA) {
		this.playerA = playerA;
	}
	
	public void setPlayerB(Integer playerB) {
		this.playerB = playerB;
	}
	
	public Integer getPlayerB() {
		return playerB;
	}
	
	public Integer getPhase() {
		return phase;
	}
	
	public void setPhase(int phase) {
		this.phase = phase;
	}
}
