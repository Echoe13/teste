package com.example.teste3;

import java.io.Serializable;

/**
 * Packet
 * Clasa generica folosita de ambele tipuri de conexiune (Wifi, Bluetooth)
 * pentru a trimite obiecte serializabile prin retea.
 * Recipientul se poate comporta potrivit informatiei transmise.
 */
public class Packet implements Serializable {

	private static final long serialVersionUID = 917864027026643037L;
	
	private TCPCommandType commandType;
	private Serializable obj;
	private String addressSource;
	
	public Packet(TCPCommandType commandType, Serializable o) {
		this.commandType = commandType;
		this.obj = o;
	}
	
	public void setAddressSource(String addr) {
		this.addressSource = addr;
	}
	
	public String getAddressSource() {
		return this.addressSource;
	}

	public TCPCommandType getCommandType() {
		return commandType;
	}

	public Serializable getObj() {
		return obj;
	}
	
	public void setObj(Serializable o) {
		this.obj = o;
	}

}
