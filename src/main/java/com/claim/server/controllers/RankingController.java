
/**
 *  author: Hanna Kropf
 */

package com.claim.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claim.model.Ranking;
import com.claim.service.RankingService;

@RestController
@CrossOrigin(origins = "*")
public class RankingController {

	@Autowired
	private RankingService rankingService;

		
	@GetMapping("/ranking")
	public List<Ranking> getRankingList() {
		return rankingService.getRankingList();
	}
}
