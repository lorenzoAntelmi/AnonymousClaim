package com.claim.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Provides all endpoints for every single view.
 * @author Rocco Saracino & Valentina Caldana
 */

@Controller
public class LoginController {

	@GetMapping("/login-de")
	public String getLoginDE() {
		return "login-DE";
	}

	@GetMapping("/login-en")
	public String getLoginEN() {
		return "login-EN";
	}

	@GetMapping("/registration-de")
	public String getRegistrationDE() {
		return "registration-DE";
	}

	@GetMapping("/registration-en")
	public String getRegistrationEN() {
		return "registration-EN";
	}

	
	@GetMapping("/gameSurface-DE")
	public String getGameSurface() {
		return "gameSurface-DE";
	}
	
	@GetMapping("/lobby-DE")
	public String getLobbyDE() {
		return "lobby-DE";
	}
	
	@GetMapping("/lobby-EN")
	public String getLobbyEN() {
		return "lobby-EN";
	}
	
	@GetMapping("/password-reset-DE")
	public String getPasswordResetDE() {
		return "password-reset-DE";
	}
}
