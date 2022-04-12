package com.claim.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// Following annotations are required in order for database to work
@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class })
@EntityScan(basePackages={"com.claim.model"}) 
@EnableJpaRepositories(basePackages={"com.claim.database"}) 
public class ClaimServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClaimServerApplication.class, args);
	}

}
