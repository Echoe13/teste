package com.example.teste3;

import java.io.Serializable;

/**
 * PacketClient
 * Extinde clasa Packet pentru a adauga numele userului.
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
