package com.example.teste3;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.Runnable;

import android.os.Handler;
import android.util.Log;

/**
 * Connection
 * Clasa abstracta care defineste comportamentul general al unei conexiuni.
 * Clasa e folosita de toate tipurile de conexiune (Wifi & Bluetooth)
 * si de ambele moduri (Client & Server).
 * 
 * Are metode pentru scrierea de pachete in conexiunea.
 * Implementeaza Runnable pentru a avea propriul thread de citire a conexiunilor care intra,
 * pentru ca mai apoi sa routeze pachetele citite catre un handler.
 */
public abstract class Connection implements Runnable, Serializable {
	private static final long serialVersionUID = 6234567955793245005L;
	
	protected InputStream inStream;
    protected OutputStream outStream;
    private final Handler handler;
    protected boolean isRunning;
    
    public Connection(Handler handler) {
    	this.isRunning = true;
        this.inStream = null;
        this.outStream = null;
    	this.handler = handler;
    }
    
    public abstract String getAddressSource();
    public abstract void disconnect();
	
	protected synchronized boolean isRunning() {
		return isRunning;
	}
	
    public void write(String id, TCPCommandType commandType, Serializable obj) throws IOException {
    	this.write(new PacketClient(id, commandType, obj));
    }
    
    public void write(TCPCommandType commandType, Serializable obj) throws IOException {
    	this.write(new Packet(commandType, obj));
    }
    
    public void write(TCPCommandType commandType) throws IOException {
    	this.write(new Packet(commandType, null));
    }

    public void write(String id, TCPCommandType commandType) throws IOException {
		this.write(new PacketClient(id, commandType, null));
	}
    
	private void write(Packet packet) throws IOException {
		ObjectOutputStream os = new ObjectOutputStream(this.outStream);
		os.writeObject(packet);
	}
	
	@Override
	public void run() {
        while (isRunning()) {
            try {
            	ObjectInputStream in = new ObjectInputStream(inStream);
				Packet p = (Packet)in.readObject();
				p.setAddressSource(getAddressSource());
                handler.obtainMessage(p.getCommandType().ordinal(), p).sendToTarget();
                ApplicationManager.appendLog(Log.INFO, "Run() Connection", "running");

        	} catch (IOException e) {
            	ApplicationManager.appendLog(Log.ERROR, "Connection", "io exception");
            	e.printStackTrace();
            	handler.obtainMessage(TCPCommandType.CONNECTION_EXIT.ordinal(), new PacketClient("", TCPCommandType.CONNECTION_EXIT, this)).sendToTarget();
            	this.disconnect();
            } catch (ClassNotFoundException ce) {
            	ApplicationManager.appendLog(Log.ERROR, "Connection", ce.getMessage());
            }
        }
	}

}