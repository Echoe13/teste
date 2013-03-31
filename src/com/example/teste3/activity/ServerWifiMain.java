package com.example.teste3.activity;

import java.io.File;
import java.io.IOException;

import com.example.teste3.ApplicationManager;
import com.example.teste3.R;
import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

// Clasa pentru setarile principale ale Serverului in conexiune Wifi.
public class ServerWifiMain  extends Activity {
	private TextView ipAdressShow;
	private TextView portServerShow;
	private TextView nbMaxClientShow;
	private TextView nbAwaitingClientShow;
	private RefreshHandler redrawHandler = new RefreshHandler();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_main);
		// ????????????? -- urmeaza sa vad ce naiba face asta.
		try {
			ApplicationManager.getInstance().getServer().setRedrawHandler(redrawHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Stabileste portul prin care se va realiza comunicatia Wifi.
	@Override
	public void onStart() {
		super.onStart();
		portServerShow = (TextView)findViewById(R.id.portServ);
		portServerShow.setText("" + ApplicationManager.getInstance().getPort());
		
		manadgeTextviews();
	}
	
	// Manager pentru mesajele care apar in cazul acestei conexiuni.
	private void manadgeTextviews() {
		nbMaxClientShow = (TextView)findViewById(R.id.nbMaxClient);
		nbAwaitingClientShow = (TextView)findViewById(R.id.nbAwaitingClient);

		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		ipAdressShow = (TextView)findViewById(R.id.ipServ);
		ipAdressShow.setText(android.text.format.Formatter.formatIpAddress(ipAddress));
		try {
			nbMaxClientShow.setText("" + ApplicationManager.getInstance().getServer().getNbTotalClients() + " / " + ApplicationManager.getInstance().getServer().getMaxNbClient());
			nbAwaitingClientShow.setText("" + ApplicationManager.getInstance().getServer().getNbAwaitingClients());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Inflater pentru meniu (cele 3 puncte din dreapta sus).
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_server, menu);
        return true;
    }

	// Gestionarea mesajelor care pot veni in timpul operatiunilor.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.disconnectS:
        	Toast.makeText(this, "Server deconnection", Toast.LENGTH_SHORT).show();
        	try {
				ApplicationManager.getInstance().exitServer();
			} catch (IOException e) {
				ApplicationManager.appendLog(Log.ERROR, "Exiting from server view", "Error while exiting server");
				e.printStackTrace();
			} catch (Exception e) {
				ApplicationManager.appendLog(Log.ERROR, "Exiting from server view", "Error while exiting server");
				e.printStackTrace();
			}
        	startActivity(new Intent(this, ProfileTypeChooser.class));
            return true;
        case R.id.showLogS:
        	startActivity(new Intent(this, LogPage.class));
            return true;
        case R.id.deletelogS:
    		File sdcard = Environment.getExternalStorageDirectory();
        	File file = new File(sdcard,"/RoodroidLog.log");
        	boolean deleted = file.delete();
        	if(deleted == false) {
        		ApplicationManager.appendLog("Log deleting", "log deleting failed");
        		Toast.makeText(this, "Log deleting failed", Toast.LENGTH_SHORT).show();
        	}
        	return true;
        }        
        return false;
    }
    
    // Handler pentru mesajele ce pot veni in timpul operatiilor de conectare.
    public class RefreshHandler extends Handler {  
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ApplicationManager.NEW_CLIENT_CONNECTED:
				manadgeTextviews();
				break;
			case ApplicationManager.NEW_CLIENT_ACCEPTED:
				manadgeTextviews();
				break;
			case ApplicationManager.CLIENT_DISCONNECTED:
				manadgeTextviews();
				break;
			case ApplicationManager.SERVER_DISCONNECTED:
        		Toast.makeText(ApplicationManager.getInstance().getApplicationContext(), "Server connection timed out", Toast.LENGTH_LONG).show();
        		try {
					ApplicationManager.getInstance().exitServer();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				finish();
				break;
			}
		}
    }
}
