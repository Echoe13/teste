package com.example.teste3.server;

import java.util.HashSet;
import java.util.Set;

/**
 * AuthByID
 * Extends AuthMethod.
 * 
 * Checks if an username is allowed or not.
 * The list of allowed usernames is stored as preferences by the ApplicationManager.
 * 
 * @author Jonathan Perichon <jonathan.perichon@gmail.com>
 * @author Lucas Gerbeaux <lucas.gerbeaux@gmail.com>
 *
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
