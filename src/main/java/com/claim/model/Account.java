package com.claim.model;


import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/** Represents an account.
 * @author Deborah Vanzin
 * -basic structure
*/
@Entity
@Table(name = "account")
public class Account {

	/** Generates an accountID
	*/
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	/** mapping corresponding column for variable
	*/
	@Column(name = "username")
	private String username;

	@Column(nullable = false, unique = true, length = 45)
	private String email;

	@Column(nullable = false, length = 64)
	private String passwordHash;

	@Column(nullable = false, length = 64)
	private LocalDate birthDate;

	private Integer score;
	
	/**Default Constructor: Object relational mapper (ORM) is a Framework, which
	 * mapps objects in database. In order for the mapper to create an object,
	 * it needs a default constructor
	*/
	protected Account() {}

	public Account(String username, String email, String passwordHash, LocalDate birthDate, Integer score) {

		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.birthDate = birthDate;
		this.score = 0;

	}

	/**@Getter & @Setter
	*/
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

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
}