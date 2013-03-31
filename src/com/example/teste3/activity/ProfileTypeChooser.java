package com.example.teste3.activity;

import com.example.teste3.ApplicationManager;
import com.example.teste3.ApplicationManager.ConnectionMode;
import com.example.teste3.ApplicationManager.ProfileType;
import com.example.teste3.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

/*
 * Clasa ProfileTypeChooser este Activitatea care este lansata initial,
 * cu scopul de a trece userul prin setarea initiala, cu optiuni de
 * mod de conectare -- Server/Client -- si conexiune -- Wifi/Bluetooth.
 */
public class ProfileTypeChooser extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /* 
         * Seteaza un set de butoane tip "Radio", care sunt potrivite pentru
         * seturi de optiuni care se exclud reciproc.
         * Aici alegem de exemplu "Server", deci se exclude automat "Client",
         * la fel si pentru conexiuni "Wi-Fi"/"Bluetooth".
         */
		RadioButton rbServer = (RadioButton) findViewById(R.id.serverRadioBtn);
		RadioButton rbClient = (RadioButton) findViewById(R.id.clientRadioBtn);
		RadioButton rbWifi = (RadioButton) findViewById(R.id.wifiRadioBtn);
		RadioButton rbBluetooth = (RadioButton) findViewById(R.id.BtRadioBtn);
		
		/*
		 * Testeaza daca profilul este de Client. Daca da, Client este marcat,
		 * iar Server nemarcat, altfel e viceversa.
		 */
		if (ApplicationManager.getInstance().getProfilType() == ProfileType.Client) {
			rbClient.setChecked(true);
			rbServer.setChecked(false);
		} else {
			rbServer.setChecked(true);
			rbClient.setChecked(false);
		}
		
		/*
		 * Testeaza daca conexiunea este de WiFi. Daca da, WiFi este marcat,
		 * iar Bluetooth nemarcat, altfel e viceversa.
		 */
		if (ApplicationManager.getInstance().getConnectionMode() == ConnectionMode.Wifi) {
			rbWifi.setChecked(true);
			rbBluetooth.setChecked(false);
		} else {
			rbBluetooth.setChecked(true);
			rbWifi.setChecked(false);
		}
		
		/*
		 * Creeaza si selecteaza un buton de Next, apoi ii atribuie un
		 * onClickListerer() care ia o decizie ca urmare a unui click pe buton.
		 */
        Button next = (Button)findViewById(R.id.nextBtn);
        next.setOnClickListener(new OnClickListener() {
			
        	/*
        	 * Functia onClick(View view), care ia deciziile bazate pe un
        	 * eveniment de click, in functie de butonul selectat.
        	 */
			@Override
			public void onClick(View v) {
				// Seteaza butoanele radio pentru Client si Wifi.
				RadioButton rbClient = (RadioButton) findViewById(R.id.clientRadioBtn);
				RadioButton rbWifi = (RadioButton) findViewById(R.id.wifiRadioBtn);
				Intent myIntent;
				Context context = v.getContext();
				// Daca este selectat Client, seteaza profilul Client.
				if (rbClient.isChecked()) {
					ApplicationManager.getInstance().setProfileType(ProfileType.Client);
					// Daca este selectat WiFi, conexiunea e setata pe WiFi.
					if (rbWifi.isChecked()) {
						ApplicationManager.getInstance().setConnectionMode(ConnectionMode.Wifi);
						myIntent = new Intent(context, ClientWifiSettings.class);
					// Altfel seteaza conexiunea Bluetooth.
					} else {
						ApplicationManager.getInstance().setConnectionMode(ConnectionMode.Bluetooth);
						myIntent = new Intent(context, ClientBluetoothSettings.class);
					}
				// Altfel seteaza profilul Server.
				} else {
					ApplicationManager.getInstance().setProfileType(ProfileType.Server);
					// Daca este selectat WiFi, conexiunea e setata pe WiFi.
					if (rbWifi.isChecked()) {
						ApplicationManager.getInstance().setConnectionMode(ConnectionMode.Wifi);
						myIntent = new Intent(context, ServerWifiSettings.class);
					// Altfel seteaza conexiunea Bluetooth.
					} else {
						ApplicationManager.getInstance().setConnectionMode(ConnectionMode.Bluetooth);
						myIntent = new Intent(context, ServerBluetoothSettings.class);
					}
				}
				// Lanseaza Activitate in functie de valoarea lui "myIntent".
				// Valori posibile: ClientWifiSettings.class, ClientBluetoothSettings.class,
				// ServerWifiSettings.class, ServerBluetoothSettings.class ;
                startActivity(myIntent);
			}
		});
    }

}