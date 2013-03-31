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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class ServerWifiSettings extends Activity {
	
	WifiLock wifiLock;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_wifi);
		checkWifiState();

		final EditText port = (EditText) findViewById(R.id.portServerWifiEdit);
		port.setText(""+ApplicationManager.getInstance().getPort());

		Button btnBack = (Button) findViewById(R.id.wifiServerBackBtn);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(v.getContext(), ProfileTypeChooser.class));
			}
		});

		Button btnConnect = (Button) findViewById(R.id.connectionServerWifiBtn);
		btnConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WifiManager wifiManager = (WifiManager) ApplicationManager.getInstance().getSystemService(Context.WIFI_SERVICE);
				if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
					try {
						ApplicationManager.getInstance().createServerWifi(Integer.parseInt(port.getText().toString()));
					} catch (Exception e) {
						ApplicationManager.appendLog(Log.ERROR, "ServerWifi", e.getMessage());
						e.printStackTrace();
						Toast.makeText(v.getContext(), "Connection error.", Toast.LENGTH_LONG).show();
						return;
					}
					ApplicationManager.appendLog("ServerWifi", "Server created");
					startActivity(new Intent(v.getContext(), ServerWifiMain.class));
				}
				else {
					Toast.makeText(v.getContext(), "Connection error, check your network options.", Toast.LENGTH_LONG).show();
				}
			}
		});


		Button btnAdvanced = (Button) findViewById(R.id.advancedSettingsServerWifiBtn);
		btnAdvanced.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent myIntent = new  Intent(v.getContext(), ServerAdvancedSettings.class);
				startActivity(myIntent);
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
			//wifiManager.setWifiEnabled(true);
			Toast.makeText(getApplicationContext(), "Enable your wifi conection please.", Toast.LENGTH_LONG).show();
			ApplicationManager.appendLog("Wifi", "Wifi enabled");
			
			Intent discoverableIntent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
			startActivity(discoverableIntent);
			
		}
		wifiLock = wifiManager.createWifiLock("wifilock");
		wifiLock.acquire();
	}
}