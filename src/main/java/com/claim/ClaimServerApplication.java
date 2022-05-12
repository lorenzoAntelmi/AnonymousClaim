package com.claim;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.claim.database.CardRepository;
import com.claim.model.Card;
import com.claim.service.CardService;
/**
 * @author Deborah Vanzin
 * Following annotations are required in order for database to work
 */

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
	
	/**
	 * Mit dem EventListener wird eine Funktion annotiert und somit aufgerufen, falls 
	 * das angegeben Event eintritt. In diesem Fall sobald die Applikation gestartet wird.
	 * Es wird geprüft ob sich Karten in der DB befinden und falls nicht werden sie über das 
	 * Cardservice generiert.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void erstelleKartenFallsNichtVorhanden() {
	    List<Card> cards = cardRepository.findAll();
	    if (!cards.isEmpty()) {
	    	return;
	    }
	    cardService.generateCards();
	    System.out.println("First call of Application. Cards were generated!");

	}
	
	/**
	 * Diese CORS-Konfiguration sollte eine URL-übergreifenden Zugriff auf die API ermöglichen
	 * z.B. falls der Client sich auf einer anderen URL befindet als die API
	 */

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}

}
