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
	
	private String username;
	private String password;
	
	//need default constructor for JSON Parsing
		public JwtRequest()
		{
			
		}
		
		public JwtRequest(String username, String password) {
			this.setUsername(username);
			this.setPassword(password);
		}

	private void setPassword(String password) {
		this.password = password;
			
		}

	private void setUsername(String username) {
			this.username = username;
			
		}

	public String getPassword() {
		return this.password;
		
		}
	
	public String getUsername() {
		return this.username;
		
		}

}
