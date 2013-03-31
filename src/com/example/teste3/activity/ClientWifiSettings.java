package com.example.teste3.activity;

import com.example.teste3.ApplicationManager;
import com.example.teste3.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

public class ClientWifiSettings extends Activity {

	WifiLock wifiLock;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_wifi);
		checkWifiState();

		final EditText textUsername = (EditText) findViewById(R.id.usernameEdit);
		textUsername.setText(ApplicationManager.getInstance().getUsername());

		final EditText textPort = (EditText) findViewById(R.id.serverPortEdit);
		textPort.setText(""+ApplicationManager.getInstance().getPort());

		final EditText textIp = (EditText) findViewById(R.id.serverIpEdit);
		textIp.setText(ApplicationManager.getInstance().getIPAddress());

		Button btnBack = (Button) findViewById(R.id.backBtn);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), ProfileTypeChooser.class);
				startActivityForResult(myIntent, 0);
			}
		});

		Button btnConnect = (Button) findViewById(R.id.connectionBtn);
		btnConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String ipAddress = textIp.getText().toString();
				int port = Integer.parseInt(textPort.getText().toString());
				String username = textUsername.getText().toString();
				WifiManager wifiManager = (WifiManager) ApplicationManager.getInstance().getSystemService(Context.WIFI_SERVICE);
				if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
					try {
						ApplicationManager.getInstance().createClientWifi(username, ipAddress, port);
					} catch (Exception e) {
						Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
						return;
					}
					startActivity(new Intent(v.getContext(), ConversationsList.class));
				}
				else {
					Toast.makeText(v.getContext(), "Connection error, check your network options.", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        wifiLock.release();
    }

	private void checkWifiState() {
		WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);  
		if(!wifiManager.isWifiEnabled()){  
			wifiManager.setWifiEnabled(true);  
			Toast.makeText(getApplicationContext(), "Wifi have been enabled", Toast.LENGTH_SHORT).show();
		}
		wifiLock = wifiManager.createWifiLock("wifilock");
		wifiLock.acquire();
	}

}