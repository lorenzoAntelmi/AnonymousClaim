package com.claim.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/** Represents an account.
 * @author Deborah Vanzin
*/
@Entity
@Table(name = "account")
public class Account {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "username")
	private String username;
	
	@Column(nullable = false, unique = true, length = 45)
	private String email;
	
	@Column(nullable = false, length = 64)
	private String passwordHash;
	
	@Column(nullable = false, length = 64)
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

	public void setPasswordHash(String encodedPassword) {
		this.passwordHash = encodedPassword;
		
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public void setRanking(Integer ranking) {
		this.ranking = ranking;
	}
}