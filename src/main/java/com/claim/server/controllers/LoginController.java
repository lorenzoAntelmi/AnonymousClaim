package com.claim.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Provides login-related endpoints.
 * @author Rocco Saracino
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
}
