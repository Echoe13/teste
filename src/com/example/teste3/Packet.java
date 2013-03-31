package com.example.teste3;

import java.io.Serializable;

/**
 * Packet
 * Generic class that is used by all the connection types (Wifi, Bluetooth)
 * to send serializable objects through network.
 * 
 * The recipient can behave accordingly to the command type.
 * 
 * @author Jonathan Perichon <jonathan.perichon@gmail.com>
 * @author Lucas Gerbeaux <lucas.gerbeaux@gmail.com>
 *
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
