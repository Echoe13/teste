package com.example.teste3;

import java.io.IOException;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

/**
 * ConnectionWifi
 * Extinde clasa Connection.
 * Defineste stratul de conexiune specific, pentru o conexiune generica la internet,
 * care consta in gestionarea unui Socket.
 */
public class ConnectionWifi extends Connection {
	private static final long serialVersionUID = 3814390935633064620L;
	private final Socket socket;
	
	public ConnectionWifi(Handler handler, Socket socket) {
    	super(handler);
    	this.socket = socket;
    	try {
            this.inStream = socket.getInputStream();
            this.outStream = socket.getOutputStream();
        } catch (IOException e) {
        	ApplicationManager.appendLog(Log.ERROR, "Connection", e.getMessage());
        	e.printStackTrace();
        }
	}
	
    @Override
	public synchronized void disconnect() {
    	isRunning = false;
        try {
			this.socket.close();
			if (ApplicationManager.getInstance().getServer() != null) {
				if (ApplicationManager.getInstance().getServer().isWifiOk()) {
					ApplicationManager.getInstance().getServer().onClientExit(this);
				}
				else {
					ApplicationManager.getInstance().exitServer();
				}
			}
			else {
				ApplicationManager.getInstance().getClient().exit();
			}
		} catch (IOException e) {
        	ApplicationManager.appendLog(Log.ERROR, "Connection", e.getMessage());
        	e.printStackTrace();
		} catch (Exception e) {
        	ApplicationManager.appendLog(Log.ERROR, "Connection", e.getMessage());
        	e.printStackTrace();
		}
	}

	@Override
	public String getAddressSource() {
		return socket.getRemoteSocketAddress().toString();
	}
}
