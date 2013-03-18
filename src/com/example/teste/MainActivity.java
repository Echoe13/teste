package com.example.teste;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	//Zona de declarare constante
	TextView mTextView;
	public final static String EXTRA_MESSAGE = "com.example.teste.MESSAGE";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//mTextView = (TextView) findViewById(R.id.text_message);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//Verifica daca versiune_android > versiune_homeycomb (3.0)
			//si se asigura ca icoana aplicatiei din bara nu se comporta
			//ca un buton.
			ActionBar actionBar = getActionBar();
			actionBar.setHomeButtonEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void sendMessage (View view) {
		//Intentie; pentru a lansa orice noua activitate, trebuie lansata o intentie.
		Intent intent = new Intent (this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	public void onDestroy() {
		//Mereu se va apela clasa onDestroy()
		super.onDestroy();
		
		//Opreste urmarirea metodei pornite de onCreate()
		android.os.Debug.stopMethodTracing();
	}
}