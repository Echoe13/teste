package com.example.teste3.server;

/**
 * AuthByPassword
 * Extends AuthMethod.
 * 
 * Checks if the password matches the one set.
 * The password is stored as preferences by the ApplicationManager.
 * 
 * @author Jonathan Perichon <jonathan.perichon@gmail.com>
 * @author Lucas Gerbeaux <lucas.gerbeaux@gmail.com>
 *
 */
public class AuthByPassword implements AuthMethod {
	
	private String password;

	public AuthByPassword(String password) {
		this.setPassword(password);
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public boolean isAuthorized(String value) {
		return value.equals(password);
	}

}
