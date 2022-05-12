package com.claim.model;

import java.io.Serializable;

/**
 *
 * @author Lorenzo Antelmi
 * storing the username and password we recieve from the client
 *
 * Source: https://www.javainuse.com/spring/boot-jwt
 *
 */

public class JwtRequest implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1115667106783763856L;

	private String email;
	private String password;

	//need default constructor for JSON Parsing
		public JwtRequest()
		{

		}

		public JwtRequest(String email, String password) {
			this.setEmail(email);
			this.setPassword(password);
		}

	private void setPassword(String password) {
		this.password = password;

		}

	private void setEmail(String email) {
			this.email = email;

		}

	public String getPassword() {
		return this.password;

		}

	public String getEmail() {
		return this.email;

		}

}
