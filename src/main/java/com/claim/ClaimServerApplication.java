package com.claim;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.claim.database.CardRepository;
import com.claim.model.Card;
import com.claim.service.CardService;

// Following annotations are required in order for database to work
@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class })
@EntityScan(basePackages={"com.claim.model"}) 
@EnableJpaRepositories(basePackages={"com.claim.database"}) 
public class ClaimServerApplication {
	@Autowired
	CardService cardService;
	@Autowired
	CardRepository cardRepository;

	public static void main(String[] args) {
		SpringApplication.run(ClaimServerApplication.class, args);
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void erstelleKarentFallsNichtVorhanden() {
	    List<Card> cards = cardRepository.findAll();
	    if (!cards.isEmpty()) {
	    	return;
	    }
	    cardService.generateCards();
	    System.out.println("First call of Application. Cards were generated!");
	    
	}

}
