package com.claim.util;

import java.util.Random;

import com.claim.config.SendMail;
/**@author Lorenzo Antelmi */
public class PasswordUtil {

	public static String generatePassword(int length) {
		// variablen für Passwort
		String lower = "abcdefghijklmnopqrstuvwxyz";
		String upper = "ABCDEFGHIJKMLNOPQRSTUFWXYZ";
		String num = "0123456789";
		String specialCharacter = "!$?()&%*+-";
		String combination = lower + upper + num + specialCharacter;

		char[] password = new char[length];

		Random random = new Random();

		// Forschleife generiert ein neues Passwort
		for (int i = 0; i < length; i++) {
			password[i] = combination.charAt(random.nextInt(combination.length()));

		}
		return password.toString();
	}

}
