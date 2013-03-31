package com.example.teste3.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.example.teste3.ConnectionWifi;

/**
 * ClientWifi
 * Extinde Client pentru a instantia ConnectionWifi.
 */
public class ClientWifi extends Client {

	public ClientWifi(String id) {
		super(id);
	}
	
	public void connect(String ipAddress, int port) throws UnknownHostException, IOException { 
		this.connection = new ConnectionWifi(this.handler, new Socket(ipAddress, port));
        new Thread(this.connection).start();
        this.authenticate();
	}
}