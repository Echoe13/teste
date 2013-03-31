package com.example.teste3;

import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

/**
 * ConnectionBluetooth
 * Extends the class Connection.
 * 
 * It defines the specific connection layer for a bluetooth connection,
 * which consists in the management of a BluetoothSocket.
 * 
 * @author Jonathan Perichon <jonathan.perichon@gmail.com>
 * @author Lucas Gerbeaux <lucas.gerbeaux@gmail.com>
 *
 */
public class ConnectionBluetooth extends Connection {
	private static final long serialVersionUID = -6945126230070252386L;
	private final BluetoothSocket socket;
    
    public ConnectionBluetooth(Handler handler, BluetoothSocket socket) {
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
			socket.close();
			if (ApplicationManager.getInstance().getClient() != null) {
				ApplicationManager.getInstance().exitClient();
			}
			else {
				ApplicationManager.getInstance().exitServer();
			}
		} catch (IOException e) {
        	ApplicationManager.appendLog(Log.ERROR, "Connection", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getAddressSource() {
		return socket.getRemoteDevice().getAddress();
	}
}
