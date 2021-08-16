package de.dragon.UsefulThings.misc;

import java.util.Random;

/**
 * Part of UsefulThings project
 *
 * @author Dragon777/Darkness4191
 **/

public class Token {

	private String token;
	
	public Token(int length) {
		Random random = new Random();
		String r = "";
		for(int i = 0; i < length; i++) {
			r += random.nextInt(10);
		}
		token = r;
	}
	
	public Token(String token) {
		this.token = token;
	}
	
	public String encode() {
		return token;
	}

}
