package com.example.teste3.activity;

import java.util.Iterator;
import com.example.teste3.ApplicationManager;
import com.example.teste3.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AuthorizedUsernamesAdapter extends BaseAdapter implements OnClickListener {
	
	private Context context;

	public AuthorizedUsernamesAdapter(Context context) {
		this.context = context;
	}

	// Ia numarul de useri autorizati.
	public int getCount() {
		return ApplicationManager.getInstance().getAuthorizedUsernames().size();
	}

	// Preia obiect de tip Object
	public Object getItem(int position) {
		int i = 0;
		Iterator<String> it = ApplicationManager.getInstance().getAuthorizedUsernames().iterator();
		while (i < position && it.hasNext()) {
			it.next();
			i++;
		}
		return it.next();
	}

	// Preia ID-ul obiectului
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		int i = 0;
		Iterator<String> it = ApplicationManager.getInstance().getAuthorizedUsernames().iterator();
		while (i < position && it.hasNext()) {
			it.next();
			i++;
		}
		
		String entry = it.next();
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.username_row, null);
		}
		TextView username = (TextView) convertView.findViewById(R.id.username);
		username.setText(entry);

		// Seteaza onClick Listener pe acest buton
		Button btnRemove = (Button) convertView.findViewById(R.id.btnRemove);
		btnRemove.setFocusableInTouchMode(false);
		btnRemove.setFocusable(false);
		btnRemove.setOnClickListener(this);
		btnRemove.setTag(entry);
		
		return convertView;
	}

	// Pentru sters intrari in lista de useri autorizati
	@Override
	public void onClick(View view) {
		String entry = (String) view.getTag();
		ApplicationManager.getInstance().removeAuthozizedUsername(entry);
		notifyDataSetChanged();
	}
}
