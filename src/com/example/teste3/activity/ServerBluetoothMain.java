package com.example.teste3.activity;

import java.io.File;
import java.io.IOException;

import com.example.teste3.ApplicationManager;
import com.example.teste3.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Clasa ServerBluetoothMain stabileste care sunt principalele optiuni
 * pentru conexiunea prin Bluetooth, in mod Server.
 */

public class ServerBluetoothMain  extends Activity {
	
	// Se definesc: cerere de descoperire, port, maxim clienti, asteptare client,
	// descoperire client si handler pt redraw.
    private static final int REQUEST_DISCOVERABILITY = 2;
	private TextView portServerShow;
	private TextView nbMaxClientShow;
	private TextView nbAwaitingClientShow;
	private EditText discoverEditText;
	private RefreshHandler redrawHandler = new RefreshHandler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_main_bt);
	}
	
	// In onStart() se definesc afisarile de port, descoperire si conexiune Bluetooth.
	@Override
	public void onStart() {
		super.onStart();
		
		portServerShow = (TextView)findViewById(R.id.portServ);
		portServerShow.setText("" + ApplicationManager.getInstance().getPort());
		
		manadgeTextviews();
		
		discoverEditText = (EditText) findViewById(R.id.discovTime);
		discoverEditText.setText("" + ApplicationManager.getInstance().getDiscoverabilityDuration());
		
        Button btnConnect = (Button) findViewById(R.id.BtConnection);
        btnConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, ApplicationManager.getInstance().getDiscoverabilityDuration());
				startActivityForResult(discoverableIntent, REQUEST_DISCOVERABILITY);
			}
		});
	}
	
	// Manager pentru texte.
	private void manadgeTextviews() {
		nbMaxClientShow = (TextView)findViewById(R.id.nbMaxClient);
		nbAwaitingClientShow = (TextView)findViewById(R.id.nbAwaitingClient);
		try {
			nbMaxClientShow.setText("" + ApplicationManager.getInstance().getServer().getNbTotalClients() + " / " + ApplicationManager.getInstance().getServer().getMaxNbClient());
			nbAwaitingClientShow.setText("" + ApplicationManager.getInstance().getServer().getNbAwaitingClients());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_DISCOVERABILITY:
			if (resultCode == ApplicationManager.getInstance().getDiscoverabilityDuration()) {
				try {
					ApplicationManager.getInstance().createServerBluetooth(Integer.parseInt(discoverEditText.getText().toString()));
					ApplicationManager.getInstance().getServer().setRedrawHandler(redrawHandler);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				Toast.makeText(this, "Your device cant be discoverable", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_server, menu);
        return true;
    }

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
    
    // Handlerul de mesaje catre OS.
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
