package com.example.teste3.client;

import java.io.IOException;
import java.util.UUID;

import com.example.teste3.ApplicationManager;
import com.example.teste3.ConnectionBluetooth;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * ClientBluetooth
 * Extends Client in order to instantiate the ConnectionBluetooth. 
 * 
 * @author Jonathan Perichon <jonathan.perichon@gmail.com>
 * @author Lucas Gerbeaux <lucas.gerbeaux@gmail.com>
 *
 */
public class ClientBluetooth extends Client {
	
	private final BluetoothAdapter adapter;

	public ClientBluetooth(String id) throws Exception {
		super(id);
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null) {
			throw new Exception("The device does not support Bluetooth");
		}
	}
	
	public void connect(String address) throws IOException {
		BluetoothSocket socket = null;
		BluetoothDevice device = adapter.getRemoteDevice(address);
		
		boolean socketCreated = false;
    	for (UUID uuid : ApplicationManager.getInstance().getUUIDs()) {
    		try {
        		socket = device.createRfcommSocketToServiceRecord(uuid);        	
        		socketCreated = true;
        		break;
    		} catch (IOException e) {
            	Log.e("ClientBluetooth", e.getMessage());
    		}
        }
        
    	if (!socketCreated) {
        	Log.e("ClientBluetooth", "socket not created");
    		throw new IOException("Error during the creation of the socket");
    	}
    	
    	socket.connect();
    	
        connection = new ConnectionBluetooth(this.handler, socket);
        new Thread(this.connection).start();
        authenticate();
	}
}