package com.example.teste3.server;

/**
 * AuthByPassword
 * Extinde AuthMethod.
 * 
 * Verifica daca parola este identica celei introduse.
 * Parola este stocata ca preferinta de catre ApplicationManager.
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
