package com.example.teste3.activity;

import com.example.teste3.ApplicationManager;
import com.example.teste3.ApplicationManager.Authentication;
import com.example.teste3.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.view.View.OnClickListener;

/*
 * Clasa ServerAdvancedSettings este o Activitate care manipuleaza
 * setarile avansate pentru conexiuni.
 */
public class ServerAdvancedSettings extends Activity {
	
	/* Se defineste "adapter" de tipul definit de clasa din AuthorizedUsernamesAdapter.java
	 * cu scopul de a verifica userii autorizati.
	 */
	private AuthorizedUsernamesAdapter adapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_options);
        
        // Numarul maxim de mesaje permise.
        final EditText maxMess = (EditText) findViewById(R.id.nbMaxMessEdit);
        maxMess.setText(""+ApplicationManager.getInstance().getNbMaxMessages());
		
        // Numarul maxim de clienti permisi.
        final EditText maxClients = (EditText) findViewById(R.id.nbMaxClientsEdit);
        maxClients.setText(""+ApplicationManager.getInstance().getNbMaxClients());
        
        // Lista de ID-uri permise.
        final ListView list = (ListView) findViewById(R.id.allowedIDsList);

        // Defineste si aloca onClickListener() pe butonul "Done" al serverului.
        Button btnDone = (Button) findViewById(R.id.serverOptionDoneBtn);
        btnDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ApplicationManager.getInstance().saveAuthorizedUsernames();
				ApplicationManager.getInstance().setNbMaxClients(Integer.parseInt(maxClients.getText().toString()));
				ApplicationManager.getInstance().setNbMaxMessages(Integer.parseInt(maxMess.getText().toString()));
				finish();
			}
		});
        
     // Defineste si aloca onClickListener() pe butonul "Options" al serverului.
		final Button btnAdd = (Button) findViewById(R.id.serverOptionAddBtn);
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopUpUsername();
			}
		});
		
		// Defineste si aloca onClickListener() pe butonul de parola al serverului.
		final Button btnPassword = (Button) findViewById(R.id.serverOptionPasswordBtn);
		btnPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopUpPassword();
			}
		});
		
		// Variabila text, care e folosita pentru listarea ID-urilor permise.
		final TextView textUsernames = (TextView) findViewById(R.id.listAllowedIdsTitle);
		
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.authentificationTypeSelector);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				btnPassword.setVisibility(android.view.View.INVISIBLE);
				btnAdd.setVisibility(android.view.View.INVISIBLE);
				list.setVisibility(android.view.View.INVISIBLE);
				textUsernames.setVisibility(android.view.View.INVISIBLE);
				switch (checkedId) {
				case R.id.idAuthentificationRadioBtn:
					btnAdd.setVisibility(android.view.View.VISIBLE);
					list.setVisibility(android.view.View.VISIBLE);
					textUsernames.setVisibility(android.view.View.VISIBLE);
					ApplicationManager.getInstance().setAuthenticationMode(Authentication.ID);
					break;
				case R.id.passwordAuthentificationRadioBtn:
					btnPassword.setVisibility(android.view.View.VISIBLE);
					ApplicationManager.getInstance().setAuthenticationMode(Authentication.Password);
					break;
				case R.id.noAuthentificationRadioBtn:
					ApplicationManager.getInstance().setAuthenticationMode(Authentication.None);
					break;
				}
			}
		});
		
		adapter = new AuthorizedUsernamesAdapter(this);
		list.setAdapter(adapter);
	}

	// Functia care afiseaza o fereastra pop-up care alerteaza userul sa seteze o parola.
	private void showPopUpPassword() {
		// Construieste pup-up-ul.
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle("Set password");

		// Seteaza un "input" pe fereastra.
		final EditText input = new EditText(this);
		input.setTransformationMethod(new PasswordTransformationMethod());
		input.setSingleLine();
		input.setText("");
		helpBuilder.setView(input);

		// Seteaza actiunea pe butonul pozitiv (OK,YES,ACCEPT etc).
		helpBuilder.setPositiveButton("Set password", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ApplicationManager.getInstance().setPassword(input.getText().toString());
				adapter.notifyDataSetChanged();
			}
		});

		// Seteaza actiunea pe butonul negativ (NO,CANCEL,DISMISS etc).
		helpBuilder.setNegativeButton("Cancel", null);

		AlertDialog helpDialog = helpBuilder.create();
		helpDialog.show();
	}
	
	// Functia care afiseaza o fereastra pop-up care alerteaza userul sa seteze un username.
	private void showPopUpUsername() {
		// Construieste pup-up-ul.
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle("Set authorized usernames");

		// Seteaza un "input" pe fereastra.
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setText("");
		helpBuilder.setView(input);

		// Seteaza actiunea pe butonul pozitiv (OK,YES,ACCEPT etc).
		helpBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ApplicationManager.getInstance().addAuthozizedUsername(input.getText().toString());
				adapter.notifyDataSetChanged();
			}
		});

		// Seteaza actiunea pe butonul negativ (NO,CANCEL,DISMISS etc).
		helpBuilder.setNegativeButton("Cancel", null);

		AlertDialog helpDialog = helpBuilder.create();
		helpDialog.show();
	}
}