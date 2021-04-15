package de.htwsaar.pib.zms.server.model;

/**
 * Dadurch das im User die JSonIgnore-Annotation benutzt wird(sensible Date), kann man bei 
 * Fällen in denen man das Passwort mitgeben muss, nicht das Userobject überliefern.
 * In diesen Fällen arbeitet man mit dieser Klasse
 */

public class UserPasswordStruct {
	
	public User user;
	public String password;
	
	public UserPasswordStruct(User u, String pw) {
		this.user = u;
		this.password = pw;
	}
	
	public UserPasswordStruct() {
		
	}
}
