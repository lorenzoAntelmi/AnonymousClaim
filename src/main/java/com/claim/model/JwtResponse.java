package com.claim.model;

import java.io.Serializable;

/**
 *
 * @author Lorenzo Antelmi
 * creating a response containing the JWT to be returned to the user
 *
 */

public class JwtResponse implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8367939239902862223L;

	private final String jwttoken;

	public JwtResponse(String jwttoken) {
		this.jwttoken = jwttoken;

	}

	public String getToken() {
		return this.jwttoken;
	}

}
