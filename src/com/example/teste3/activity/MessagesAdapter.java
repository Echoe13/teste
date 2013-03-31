package com.example.teste3.activity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.example.teste3.Message;
import com.example.teste3.Message.MessageStatus;
import com.example.teste3.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessagesAdapter extends BaseAdapter implements Serializable {
	private static final long serialVersionUID = 8243606670808527527L;
	private Context context;
	private String contactName;
	private int id;
	private List<Message> messages;

	public MessagesAdapter(Context context, List<Message> messages, String contact, int id) {
		this.context = context;
		this.contactName = contact;
		this.id = id;
		updateMessages(messages);
	}
	
	public List<Message> getMessages() {
		return messages;
	}
	
	public void updateMessages(List<Message> messages) {
		this.messages = new ArrayList<Message>(messages);
		Collections.sort(this.messages);
	}
	
	public int getId() {
		return id;
	}

	public int getCount() {
		return messages.size();
	}

	public Object getItem(int position) {
		if (position >= messages.size()) {
			throw new IndexOutOfBoundsException();
		}
		return messages.get(position);
	}

	public long getItemId(int position) {
		if (position >= messages.size()) {
			throw new IndexOutOfBoundsException();
		}
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		if (position >= messages.size()) {
			throw new IndexOutOfBoundsException();
		}
		
		Message entry = messages.get(position);
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.messages_row, null);
		}
		
		TextView contact = (TextView) convertView.findViewById(R.id.contactName);
		if (entry.getMessageStatus() == MessageStatus.SENT) {
			contact.setText("Me : ");
		}
		else {
			contact.setText(contactName + " : ");
		}
			
		TextView content = (TextView) convertView.findViewById(R.id.content);
		content.setText(entry.getTextContent());
		
		
		TextView date = (TextView) convertView.findViewById(R.id.date);
		Date now = new Date();
		String messageDate = "";
		if (entry.getDate().getDate() == now.getDate() && entry.getDate().getMonth() == now.getMonth() && entry.getDate().getYear() == now.getYear()) {
			messageDate += new SimpleDateFormat("HH:mm").format(entry.getDate());
		}
		else if (entry.getDate().getDate() == now.getDate() -1 && entry.getDate().getMonth() == now.getMonth() && entry.getDate().getYear() == now.getYear()) {
			messageDate += "Yest";
		}
		else {
			messageDate += new SimpleDateFormat("dd/MM").format(entry.getDate());
		}
		date.setText(messageDate);
		
		return convertView;
	}

}