package com.claim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** @author Deborah Vanzin
 * Following annotations are required in order for database to work*/

@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class })
@EntityScan(basePackages={"com.claim.model"})
@EnableJpaRepositories(basePackages={"com.claim.database"})
public class ClaimServerApplication {
	


	public static void main(String[] args) {
		SpringApplication.run(ClaimServerApplication.class, args);
	}
	
	/** Diese CORS-Konfiguration sollte eine URL-übergreifenden Zugriff auf die API ermöglichen
	 * z.B. falls der Client sich auf einer anderen URL befindet als die API */
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