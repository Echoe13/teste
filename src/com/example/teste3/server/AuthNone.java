package com.example.teste3.server;

/**
 * AuthNone
 * Extends AuthMethod.
 * 
 * Accepts every new clients if the limit of connected client is not reached.
 * 
 * @author Jonathan Perichon <jonathan.perichon@gmail.com>
 * @author Lucas Gerbeaux <lucas.gerbeaux@gmail.com>
 *
 */
public class AuthNone implements AuthMethod {

	@Override
	public boolean isAuthorized(String value) {
		return true;
	}

}
