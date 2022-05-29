package com.claim.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.claim.database.PlayerRepository;
import com.claim.model.Card;
import com.claim.model.Player;

/** Provides player-related endpoints.
 * @author Rocco Saracino*/

@RestController
public class PlayerController {


		@Autowired
		private PlayerRepository playerRepository;
		
		/** Gets the players handCards*/
		@GetMapping("/getHand")
		public List<Card> getHand(@AuthenticationPrincipal UserDetails account) {
			return playerRepository
					.findByAccount_Email(account.getUsername())
					.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND))
					.getHand();
		}
		
		@GetMapping("/getPlayer")
		public Player getPlayer(@AuthenticationPrincipal UserDetails account) {
			return playerRepository
					.findByAccount_Email(account.getUsername())
					.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
		}

}
