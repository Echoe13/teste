package com.example.teste3.activity;

import java.util.Set;

import com.example.teste3.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BluetoothDiscovery extends Activity {

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);
        
        // Seteaza rezultatul CANCELED, daca userul da inapoi 
        setResult(Activity.RESULT_CANCELED);

        // Preia adaptorul local de Bluetooth
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // Initializeaza butonul pentru descoperire (vizibilitate)
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });
        
        // Initializeaza multimea de adaptoare. Una pentru dispozitive conectate anterior
        // si una pentru dispozitive nou descoperite
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        
        // Gaseste si seteaza ListView pentru dispozitive conectate anterior
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);
        
        // Gaseste si seteaza ListView pentru dispozitive nou descoperite
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
        
        // Inregistreaza pentru Broadcast cand un dispozitiv e descoperit
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Inregistreaza pentru Broadcast cand descoperirea s-a incheiat
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);


        // Ia setul de dispozitive conectate in acest moment
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Daca exista dispozitive conectate, adauga fiecare in ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = "no devices availables";
            mPairedDevicesArrayAdapter.add(noDevices);
        }
        
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Se asigura ca nu mai au loc descoperiri
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Incheie inregistrarea de Broadcast Listeners
        this.unregisterReceiver(mReceiver);
    }

	
	private void doDiscovery() {

        // Indica scanarea in titlu
        setProgressBarIndeterminateVisibility(true);
        setTitle("scanning");

        // Porneste sub-titlu pentru dispozitive noi
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // Daca deja se executa descoperirea, opreste
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Cere descoperire de la BluetoothAdapter
        mBtAdapter.startDiscovery();
    }
	
    // On-click listener pentru toate dispozitivele din ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Opreste descoperirea pentru a economisi resurse; oricum urmeaza sa execute conexiunea
            mBtAdapter.cancelDiscovery();

            // Ia adresa MAC a dispozitivului, ce reprezinta ultimele 17 caractere din View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Creeaza rezultatul, ca Intent, si include adresa MAC
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
    
    // BroadcastReceiver care asculta dupa dispozitive recent descoperite si
    // schimba titlul cand s-a incheiat descoperirea
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // Cand gaseste un dispozitiv
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Ia obiectul de BluetoothDevice din Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Daca sunt deja conectate, treci peste, pentru ca se afla deja pe lista
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // Cand descoperirea e terminata, schimba titlul activitatii
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle("select device");
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = "no device found";
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };
	
}
