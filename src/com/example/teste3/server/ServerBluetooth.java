package com.example.teste3.server;

import java.io.IOException;
import java.util.UUID;

import com.example.teste3.ApplicationManager;
import com.example.teste3.Connection;
import com.example.teste3.ConnectionBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * ServerBluetooth
 * Extends Server in order to instantiate the ConnectionBluetooth.
 * 
 * @author Jonathan Perichon <jonathan.perichon@gmail.com>
 * @author Lucas Gerbeaux <lucas.gerbeaux@gmail.com>
 *
 */
public class ServerBluetooth extends Server {

	private static final String NAME = "BluetoothServer";
	private final BluetoothAdapter adapter;
	private BluetoothServerSocket serverSocket = null;

	public ServerBluetooth(AuthMethod authMethod, int maxNbMsgSent, int maxNbClients) throws Exception {
		super(authMethod, maxNbMsgSent, maxNbClients);
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null) {
			throw new Exception("The device does not support Bluetooth");
		}
		
		if (adapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			throw new Exception("The device is not discoverable");
		}
	}

	@Override
	public void run() {
		BluetoothSocket socket = null;
		Connection con = null;
		Thread t = null;
		while (isRunning()) {
			try {
				for (UUID uuid : ApplicationManager.getInstance().getUUIDs()) {
					serverSocket = adapter.listenUsingRfcommWithServiceRecord(NAME, uuid);
					socket = serverSocket.accept();
					if (socket != null) {
						Log.d("okkkk", "client added");
						con = new ConnectionBluetooth(handler, socket);
						t = new Thread(con);
						t.start();
						awaitingConnections.add(con);
					}	                    
				}
			} catch (IOException e) {
	        	Log.e("ServerBluetooth", e.getMessage());
				if (con != null) {
					this.awaitingConnections.remove(con);
					this.activeConnections.values().remove(con);
					con.disconnect();
				}
				if (t != null) {
					t.interrupt();
				}
			}
		}
	}
	
	public void closeServerSocket() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}