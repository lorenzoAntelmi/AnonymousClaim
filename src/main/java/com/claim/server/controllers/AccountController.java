package com.claim.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.claim.database.AccountRepository;
import com.claim.model.Account;

/** Provides account-related endpoints.
 * @author Deborah Vanzin
*/
@Controller
public class AccountController {
	
	  @Autowired
	  public AccountRepository repository;

	  @PostMapping("/account")
	  @ResponseBody
	  public Account registerAccount(@RequestBody(required=true) Account account) {
		  if(repository.existsByEmail(account.getEmail())) {
			  throw new ResponseStatusException(HttpStatus.CONFLICT, "This email has already been registered in the system!");
		  }
		  
		  if(repository.existsByUsername(account.getUsername())) {
			  throw new ResponseStatusException(HttpStatus.CONFLICT, "This username has already been registered in the system!");
		  }
		  
		  repository.save(account);
		  return account;
	  }
}

	
