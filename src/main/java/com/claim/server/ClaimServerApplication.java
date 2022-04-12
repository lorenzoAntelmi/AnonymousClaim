package com.claim.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class })
public class ClaimServerApplication {

	public static void main(String[] args) {
		
		
		
		
		SpringApplication.run(ClaimServerApplication.class, args);
	}

}
