package com.claim.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.claim.database.AccountRepository;
import com.claim.model.Account;
import com.claim.model.Ranking;

@Service
public class RankingService {
	
	@Autowired
	private AccountRepository accountRepository;

	//holt alle Rankings aus der DB, sortiert nach score und limitiert sie auf top10//
	//source: https://stackoverflow.com/questions/66008858/how-to-get-the-top-5-max-values-from-a-list-using-java-stream/
	public List<Ranking> getRankingList () {
		final List<Account> topTenAccount = accountRepository.findAll().stream()
				//.sorted((o1, o2) -> o1.getScore().compareTo(o2.getScore())) 
				.sorted(Comparator.comparing(Account::getScore).reversed())
				.limit(10)
				.collect(Collectors.toList()); 
		final List<Ranking> rankingList=new ArrayList<>();
		topTenAccount.forEach((account) -> 
			rankingList.add(mapAccountToRanking(account)));
		for (int i = 0; i < 10; i++ ) {rankingList.get(i).setRank(i+1);	}
		return rankingList;
	}
	//Daten vom Account, die für Ranking verwendet werden, sollen in Objekt Ranking abgefüllt werden/
	private Ranking mapAccountToRanking(Account account) {
		final Ranking ranking=new Ranking();
		ranking.setUsername(account.getUsername());
		ranking.setScore(account.getScore());
		return ranking;
	}
}
