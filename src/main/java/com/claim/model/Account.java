package com.claim.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/** Represents an account.
 * @author Deborah Vanzin
*/
@Entity
public class Account {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private String username;
	private String email;
	private String passwordHash;
	private Date birthDate;
	private Integer ranking;
	
	public Account() {}
	
	public Account(Integer id, String username, String email, String passwordHash, Date birthDate, Integer ranking) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.birthDate = birthDate;
		this.ranking = ranking;
	}

	public Integer getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public Integer getRanking() {
		return ranking;
	}
}
