package com.claim.server.controllers;

import java.sql.Date;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.claim.database.AccountRepository;
import com.claim.model.Account;

/** Provides game-related endpoints.
 * @author Deborah Vanzin
*/
@Controller
public class GameController {
	  @Autowired
	  public AccountRepository repository;

	  @GetMapping("/game")
	  @ResponseBody
	  public Account sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
		  Account account = new Account(0, "Dummy", "dummy@bestengineering.ch", "zxzxzx", Date.valueOf(LocalDate.of(1998, 2, 11)), 999);
		  repository.save(account);
		  return account;
	  }
}
