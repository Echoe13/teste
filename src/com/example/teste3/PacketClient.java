package com.example.teste3;

import java.io.Serializable;

/**
 * PacketClient
 * Extends the class Packet in order to add the client username.
 * 
 * @author Jonathan Perichon <jonathan.perichon@gmail.com>
 * @author Lucas Gerbeaux <lucas.gerbeaux@gmail.com>
 *
 */
public class PacketClient extends Packet {

	private static final long serialVersionUID = 7377325024375188535L;
	private String id;
	
	public PacketClient(String id, TCPCommandType commandType, Serializable o) {
		super(commandType, o);
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
}
