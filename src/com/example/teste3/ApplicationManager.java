package com.example.teste3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Pattern;

import com.example.teste3.client.Client;
import com.example.teste3.client.ClientBluetooth;
import com.example.teste3.client.ClientWifi;
import com.example.teste3.server.AuthByID;
import com.example.teste3.server.AuthByPassword;
import com.example.teste3.server.AuthNone;
import com.example.teste3.server.Server;
import com.example.teste3.server.ServerBluetooth;
import com.example.teste3.server.ServerWifi;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Application Manager
 * Extinde clasa Application din SDK-ul Android.
 * Clasa e instantiata automat la pornirea Activitatii.
 * Aceasta implementare urmareste modelul de design Singleton, restrictionand
 * instantierea clasei la un singur obiect.
 * 
 * Vezi: http://en.wikipedia.org/wiki/Singleton_pattern
 * 
 * Instantiaza partea de Client sau Server a aplicatiei.
 * Administreaza preferintele, setari care sunt stocate intr-un fisier.
 * Declara BroadcastReceivers si handler-ul cerute la nivel global de catre aplicatie.
 */
public class ApplicationManager extends Application {
                       
	public static final int NEW_CLIENT_CONNECTED = 1;
	public static final int NEW_CLIENT_ACCEPTED = 2;
	public static final int CLIENT_DISCONNECTED = 3;
	public static final int SERVER_DISCONNECTED = 4;
	public static final int NETWORK_DISCONNECTED = 5;
	private static ApplicationManager instance;
	
	private Client client = null;
	private Server server =  null; 
	
	private List<UUID> uuids;
	private SharedPreferences prefs;
	public enum Authentication { Password, ID, None };
	public enum ConnectionMode { Wifi, Bluetooth };
	public enum ProfileType { Client, Server };
	
	private Set<String> authorizedUsernames;
	private int nbAuthorizedUsernames;
	private RefreshHandler handler = new RefreshHandler();
	private WifiBroadcastReceiver receiver;
	private BluetoothBroadcastReceiver btReceiver;

	public static ApplicationManager getInstance() {
		return instance;
	}

	public Client getClient() throws Exception {
		if (client == null) {
			throw new Exception("The application doesn't run as a Client");
		}
		return client;
	}

	public Server getServer() throws Exception {
		if (server == null) {
			throw new Exception("The application doesn't run as a Server");
		}
		return server;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		instance = this;
		uuids = new ArrayList<UUID>();
		uuids.add(UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc"));
		uuids.add(UUID.fromString("2d64189d-5a2c-4511-a074-77f199fd0834"));
		uuids.add(UUID.fromString("e442e09a-51f3-4a7b-91cb-f638491d1412"));
		uuids.add(UUID.fromString("a81d6504-4536-49ee-a475-7d96d09439e4"));
		uuids.add(UUID.fromString("aa91eab1-d8ad-448e-abdb-95ebba4a9b55"));
		uuids.add(UUID.fromString("4d34da73-d0a4-4f40-ac38-917e0a9dee97"));
		uuids.add(UUID.fromString("5e14d4df-9c8a-4db7-81e4-c937564c86e0"));

		authorizedUsernames = new TreeSet<String>();
		nbAuthorizedUsernames = prefs.getInt("nb", 0);
		int j = 0;
		while (j < nbAuthorizedUsernames) {
			authorizedUsernames.add(prefs.getString("id" + j, ""));
			j++;
		}
	}

	public List<UUID> getUUIDs() {
		return uuids;
	}

	public Set<String> getAuthorizedUsernames() {
		return authorizedUsernames;
	}

	public void saveAuthorizedUsernames() {
		Editor ed = prefs.edit();
		int j = 0;
		while (j < nbAuthorizedUsernames) {
			ed.remove("id"+j);
			j++;
		}
		j = 0;

		Iterator<String> it = authorizedUsernames.iterator();
		while (it.hasNext()) {
			ed.putString("id"+j, it.next());
			j++;
		}
		ed.commit();
	}

	public boolean addAuthozizedUsername(String username) {
		appendLog("AuthorizedUsr", username + " has been add to athorized list");
		return authorizedUsernames.add(username);
	}


	public boolean removeAuthozizedUsername(String username) {
		appendLog("AuthorizedUsr", username + " has been deleted from athorized list");
		return authorizedUsernames.remove(username);
	}
	
	public Authentication getAuthenticationMode() {
		return Authentication.valueOf(prefs.getString("authenticationMode", Authentication.None.name()));
	}

	public void setAuthenticationMode(Authentication authenticationMode) {
		Editor ed = prefs.edit();
		ed.putString("authenticationMode", authenticationMode.name());
		ed.commit();
		appendLog("AuthenticationMode", "Authentication Mode set to " + authenticationMode.name());
	}

	private void setIPAddress(String ipAddress) {
		Editor ed = prefs.edit();
		ed.putString("ip", ipAddress);
		ed.commit();
		appendLog("IP", "IP adress set to " + ipAddress);
	}

	public String getIPAddress() {
		return prefs.getString("ip", "");
	}

	public String getUsername() {
		return prefs.getString("username", android.os.Build.MODEL);
	}

	private void setUsername(String username) {
		Editor ed = prefs.edit();
		ed.putString("username", username);
		ed.commit();
		appendLog("Username", "Username set to " + username);
	}
	
	public String getPassword() {
		return prefs.getString("password", "");
	}

	public void setPassword(String password) {
		Editor ed = prefs.edit();
		ed.putString("password", password);
		ed.commit();
		appendLog("Password", "Password set to " + password);
	}

	public int getPort() {
		return prefs.getInt("port", 9000);
	}

	private void setPort(int port) {
		Editor ed = prefs.edit();
		ed.putInt("port", port);
		ed.commit();
		appendLog("Port", "Port set to " + port);
	}

	public ProfileType getProfilType() {
		return ProfileType.valueOf(prefs.getString("profileType", ProfileType.Client.name()));
	}

	public void setProfileType(ProfileType profileType) {
		Editor ed = prefs.edit();
		ed.putString("profileType", profileType.name());
		ed.commit();
		appendLog("ProfilType", "Profil Type set to " + profileType.name());
	}

	public ConnectionMode getConnectionMode() {
		return ConnectionMode.valueOf(prefs.getString("connectionMode", ConnectionMode.Wifi.name()));
	}

	public void setConnectionMode(ConnectionMode connectionMode) {
		Editor ed = prefs.edit();
		ed.putString("connectionMode", connectionMode.name());
		ed.commit();
		appendLog("ConnectionMode", "Connection Mode set to " + connectionMode.name());
	}

	public int getDiscoverabilityDuration() {
		return prefs.getInt("discoverabilityTime", 200);
	}

	private void setDiscoverabilityTime(int duration) {
		Editor ed = prefs.edit();
		ed.putInt("discoverabilityTime", duration);
		ed.commit();
		appendLog("DiscoTime", "Discoverability time set to " + duration);
	}

	public int getNbMaxClients() {
		ConnectionMode mode = getConnectionMode();
		if (mode == ConnectionMode.Bluetooth) {
			return prefs.getInt("nbMaxClientsBluetooth", 7);
		} else {
			return prefs.getInt("nbMaxClientsWifi", 10);
		}
	}

	public void setNbMaxClients(int nbMaxClients) {
		ConnectionMode mode = getConnectionMode();
		Editor ed = prefs.edit();
		if (mode == ConnectionMode.Bluetooth) {
			ed.putInt("nbMaxClientsBluetooth", nbMaxClients);
		} else {
			ed.putInt("nbMaxClientsWifi", nbMaxClients);
		}
		ed.commit();
		appendLog("nbMaxClient", "nbMaxClient set to " + nbMaxClients);
	}
	
	public int getNbMaxMessages() {
		return prefs.getInt("nbMaxMessages", -1);
	}

	public void setNbMaxMessages(int nbMaxMessages) {
		Editor ed = prefs.edit();
		ed.putInt("nbMaxMessages", nbMaxMessages);
		ed.commit();
		appendLog("nbMaxMessages", "nbMaxMessages set to " + nbMaxMessages);
	}

	public void createClientWifi(String username, String ipAddress, int port) throws Exception {
		final String IPADDRESS_PATTERN = 
				"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		final Pattern ipAdd= Pattern.compile(IPADDRESS_PATTERN);
		if (!ipAdd.matcher(ipAddress).matches()) {
			throw new Exception("The IP address is not valid");
		}

		setIPAddress(ipAddress);
		setUsername(username);
		setPort(port);

		client = new ClientWifi(username);
		try {
			((ClientWifi)client).connect(ipAddress, port);
		} catch (Exception e) {
			throw new Exception("Failure to connect");
		}
		receiver = new WifiBroadcastReceiver(handler, ConnectivityManager.TYPE_WIFI);
		IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(receiver, filter);
	}

	public void createClientBluetooth(String username, String address) throws Exception {
		client = new ClientBluetooth(username);
		try {
			((ClientBluetooth)client).connect(address);
		} catch (Exception e) {
			throw new Exception("Failure to connect");
		}
		btReceiver = new BluetoothBroadcastReceiver(handler);
		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(btReceiver, filter);
	}

	public void createServerBluetooth(int duration) throws Exception {
		try {
			setDiscoverabilityTime(duration);
			Authentication auth = getAuthenticationMode();
			int nbMaxClients = getNbMaxClients();
			int nbMaxMessages = getNbMaxMessages();
			
			switch (auth) {
			case ID:
				server = new ServerBluetooth(new AuthByID(getAuthorizedUsernames()), nbMaxMessages, nbMaxClients);
				break;
			case None:
				server = new ServerBluetooth(new AuthNone(), nbMaxMessages, nbMaxClients);
				break;
			case Password:
				server = new ServerBluetooth(new AuthByPassword(getPassword()), nbMaxMessages, nbMaxClients);
				break;
			default:
				server = new ServerBluetooth(new AuthNone(), nbMaxMessages, nbMaxClients);
				break;
			}
		
			new Thread(server).start();
		} catch (Exception e) {
			throw new Exception("Failure to launch the server" + ": " + e.getMessage());
		}
		
		btReceiver = new BluetoothBroadcastReceiver(handler);
		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(btReceiver, filter);
	}

	public void createServerWifi(int port) throws Exception {
		setPort(port);
		try {
			Authentication auth = getAuthenticationMode();
			int nbMaxClients = getNbMaxClients();
			int nbMaxMessages = getNbMaxMessages();
			switch (auth) {
			case ID:
				server = new ServerWifi(new AuthByID(getAuthorizedUsernames()), nbMaxMessages, nbMaxClients, port);
				break;
			case None:
				server = new ServerWifi(new AuthNone(), nbMaxMessages, nbMaxClients, port);
				break;
			case Password:
				server = new ServerWifi(new AuthByPassword(getPassword()), nbMaxMessages, nbMaxClients, port);
				break;
			default:
				server = new ServerWifi(new AuthNone(), nbMaxMessages, nbMaxClients, port);
				break;
			}
			
			new Thread(server).start();
		} catch (Exception e) {
			throw new Exception("Failure to launch the server" + ": " + e.getMessage());
		}
		receiver = new WifiBroadcastReceiver(handler, ConnectivityManager.TYPE_WIFI);
		IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(receiver, filter);
	}

	public static void appendLog(String tag, String text)
	{   
		appendLog(Log.DEBUG, tag, text);
	}

	public static void appendLog(int tag, String fullTag, String text)
	{   
		switch (tag) {
		case Log.DEBUG :
			Log.d(fullTag, text);
			break;
		case Log.ERROR :
			Log.e(fullTag, text);
			break;
		case Log.INFO :
			Log.i(fullTag, text);
			break;
		case Log.VERBOSE :
			Log.v(fullTag, text);
			break;
		default :
			Log.d(fullTag, text);
		}

		File logFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/RoodroidLog.log");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			//BufferedWriter pentru performanta, true pentru setarea flag-ului de adaugare la fisier
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			buf.append("" + DateFormat.getDateTimeInstance().format(new Date()) + "\n" + " : " + text + "\n");
			buf.newLine();
			buf.flush();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exitClient() throws Exception {
		getClient().exit();
		client = null;
	}
	
	public void exitServer() throws IOException, Exception {
		getServer().exit();
		if (server.getClass().getName().equals("com.example.teste3.server.ServerWifi")) {
			((ServerWifi)server).closeServerSocket();
		}
		else {
			((ServerBluetooth)server).closeServerSocket();
		}
		server = null;
	}

	private class WifiBroadcastReceiver extends BroadcastReceiver {
		private Handler handler;
		private int type;
	    private ConnectivityManager connectivityManager;
	    
	    public WifiBroadcastReceiver(Handler handler, int type) {
	    	this.handler = handler;
	    	this.type = type;
	    }
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				appendLog(Log.WARN, "BoradCast Receiver : ", "onReceived() called with " + intent);
				return;
			}

			boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

			if (noConnectivity) {
				handler.sendEmptyMessage(NETWORK_DISCONNECTED);
			} 
			else {
				connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo exactState = connectivityManager.getNetworkInfo(type);
				if (!exactState.isConnected()) {
					handler.sendEmptyMessage(NETWORK_DISCONNECTED);
				}
			}
		}
	}
	
	private class BluetoothBroadcastReceiver extends BroadcastReceiver {
		private Handler handler;
	    
	    public BluetoothBroadcastReceiver(Handler handler) {
	    	this.handler = handler;
	    }
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

	        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
	            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
	            switch (state) {
	            case BluetoothAdapter.STATE_OFF:
					handler.sendEmptyMessage(NETWORK_DISCONNECTED);
	                break;
	            case BluetoothAdapter.STATE_TURNING_OFF:
					handler.sendEmptyMessage(NETWORK_DISCONNECTED);
	                break;
	            }
	        }
		}
	}
	
	public class RefreshHandler extends Handler {  
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case NETWORK_DISCONNECTED:
				try {
					if (client != null) {
						exitClient();
					}
					else {
						exitServer();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break; 
			}
		}
	}

}