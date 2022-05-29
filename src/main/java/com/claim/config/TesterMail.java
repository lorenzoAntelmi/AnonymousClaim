package com.claim.config;

import java.util.Random;


public class TesterMail {
	
	public static void main(String[] args) {
		

		
		String lower= "abcdefghijklmnopqrstuvwxyz";
		String upper= "ABCDEFGHIJKMLNOPQRSTUFWXYZ";
		String num= "0123456789";
		String specialCharacter= "!$?()&%*+-";
		String combination= lower+upper+num+specialCharacter;
		int length = 8;
		
		
		char[] password = new char[length];
		
		Random random= new Random();
		
		
		for(int i = 0; i< length; i++) {
			password[i] = combination.charAt(random.nextInt(combination.length()));
			
			if(password[i]== password[7]) {
				
				SendMail send = new SendMail("lorenzo.antelmi83@gmail.com", "New Password", "Your new Password is: " + new String(password)); //change receiver email
				
			}
			
		}
		

	}			
			//SendMail send = new SendMail("lorenzo.antelmi83@gmail.com", "New Password", "Your new Password is: " + passwordString); //change receiver email		
				
}	
		
