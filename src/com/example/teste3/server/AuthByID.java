package com.example.teste3.server;

import java.util.HashSet;
import java.util.Set;

/**
 * AuthByID
 * Extinde AuthMethod.
 * 
 * Verifica daca userul e acceptat sau nu.
 * Lista de useri acceptati este stocata ca preferinta de catre ApplicationManager.
 */
public class AuthByID implements AuthMethod {
	
	private Set<String> authorizedIds;
	
	public AuthByID(Set<String> ids) {
		authorizedIds = new HashSet<String>(ids);
	}
	
	@Override
	public boolean isAuthorized(String value) {
		return authorizedIds.contains(value);
	}
}
