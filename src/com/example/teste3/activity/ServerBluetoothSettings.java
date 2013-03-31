package com.example.teste3.activity;

import com.example.teste3.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

// Setarile pentru Bluetooth.
public class ServerBluetoothSettings extends Activity {

    private BluetoothAdapter bluetoothAdapter = null;
	private static final int REQUEST_ENABLE_BT = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_bluetooth);


		// Variabila care ia referinta catre adaptorul de Bluetooth.
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Daca adaptorul nu exista -- variabila == null -- , afiseaza mesaj catre user.
        if (bluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_LONG).show();
        }
        // Daca Bluetooth exista, cere pornirea sa -- va aparea prompt pentru user, din OS.
        else if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        // Seteaza buton pentru Inapoi.
		Button btnBack = (Button) findViewById(R.id.backBTServerBtn);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(v.getContext(), ProfileTypeChooser.class));
			}
		});

		// Seteaza buton pentru Inainte.
		Button btnConnect = (Button) findViewById(R.id.nextBTServerBtn);
		btnConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(v.getContext(), ServerBluetoothMain.class));
			}
		});

		// Seteaza buton pentru Avansat.
		Button btnAdvanced = (Button) findViewById(R.id.advancedSettingsBtn);
		btnAdvanced.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(v.getContext(), ServerAdvancedSettings.class));
			}
		});
	}
	
	// Returneaza rezultatele in urma rularii activitatii.
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (requestCode == REQUEST_ENABLE_BT) {
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
			} else {
				// User did not enable Bluetooth or an error occured
				Toast.makeText(this, "Error while connection bluetooth", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}
}