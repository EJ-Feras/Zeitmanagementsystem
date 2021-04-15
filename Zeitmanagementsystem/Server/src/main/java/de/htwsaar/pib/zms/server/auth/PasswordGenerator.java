package de.htwsaar.pib.zms.server.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
	
	public static String generatePW(String pw) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = pw;
		String encodedPassword = encoder.encode(rawPassword);
		
		return encodedPassword;
	}
}
