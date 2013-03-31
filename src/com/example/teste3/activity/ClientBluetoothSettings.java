package com.example.teste3.activity;

import com.example.teste3.ApplicationManager;
import com.example.teste3.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;;

public class ClientBluetoothSettings extends Activity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE = 2;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.client_bluetooth);
      
        EditText username = (EditText) findViewById(R.id.usernameText);
        username.setText(ApplicationManager.getInstance().getUsername());
        
        Button btnLookup = (Button) findViewById(R.id.lookupServerBtn);
        btnLookup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
	            Intent serverIntent = new Intent(v.getContext(), BluetoothDiscovery.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	        }
		});
        
        Button btnBack = (Button) findViewById(R.id.backBtn);
        btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    }
    
    @Override
    public void onStart() {
    	super.onStart();

    	if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
    		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    	}
    	
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // Cnad DeviceListActivity returneaza un dispozitiv la care sa se conecteze
            if (resultCode == Activity.RESULT_OK) {
                String address = data.getExtras().getString(BluetoothDiscovery.EXTRA_DEVICE_ADDRESS);
               	String username = ((EditText)findViewById(R.id.usernameText)).getText().toString();
               	
               	try {
					ApplicationManager.getInstance().createClientBluetooth(username, address);
	                startActivity(new Intent(this, ConversationsList.class));
	                ApplicationManager.appendLog(Log.DEBUG, "ok", "client connected");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
            }
            break;
        case REQUEST_ENABLE_BT:
            // Cand se intoarce cererea de pornire Bluetooth
            if (resultCode == Activity.RESULT_OK) {
            } else {
                // Userul nu a pornit Bluetooth, sau a aparut o eroare
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}