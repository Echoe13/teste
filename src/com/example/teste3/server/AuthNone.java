package com.example.teste3.server;

/**
 * AuthNone
 * Extinde AuthMethod.
 * Accepta orice client now, in limita numarului maxim de conexiuni acceptate.
 */
public class AuthNone implements AuthMethod {

	@Override
	public boolean isAuthorized(String value) {
		return true;
	}

}
