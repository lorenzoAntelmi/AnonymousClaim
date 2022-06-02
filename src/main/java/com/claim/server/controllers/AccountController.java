package com.claim.server.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.claim.config.SendMail;
import com.claim.database.AccountRepository;
import com.claim.model.Account;
import com.claim.model.JwtRequest;
import com.claim.model.Registration;
import com.claim.util.PasswordUtil;

/**
 * @author Deborah Vanzin Provides account-related endpoints und validiert die
 *         übergebenen Parameter.
 */

@RestController
@CrossOrigin(origins = "*")
public class AccountController {

	@Autowired
	public AccountRepository repository;

	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}

	@PostMapping("/account")
	@ResponseBody
	public Account registerAccount(@Valid @RequestBody(required = true) Registration registration) {
		if (repository.existsByEmail(registration.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"This email has already been registered in the system!");
		}

		if (repository.existsByUsername(registration.getUsername())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"This username has already been registered in the system!");
		}

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(registration.getPassword());
		// FIXME: Convert to correct Birthdate!
		Account account = new Account(registration.getUsername(), registration.getEmail(), encodedPassword,
				registration.getBirthDate(), registration.getScore());
		account.setPasswordHash(encodedPassword);

		repository.save(account);
		return account;
	}

	/**
	 * Endpoint to send a new password to the specified email in the JwtRequest
	 */
	@PostMapping("/account/password")
	public ResponseEntity<Account> sendNewPassword(@RequestBody(required=true) JwtRequest request) {
		String password = PasswordUtil.generatePassword(8);
		Account account = repository.findByEmail(request.getEmail()).get();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(password);
		account.setPasswordHash(encodedPassword);

		// Mail mit neuem Password versenden!
		SendMail mail = new SendMail(account.getEmail(), "New Password", "Your new Password is: " + password);
		mail.send();

		repository.save(account);
		return ResponseEntity.ok(account);

	}
}
