package com.example.teste3.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.example.teste3.Conversation;
import com.example.teste3.Message;
import com.example.teste3.R;
import com.example.teste3.TextMessage;
import com.example.teste3.client.Client;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConversationsAdapter extends BaseAdapter implements OnClickListener {
	private static final int PREVIEWLENGTH = 25;
	private Context context;
	private List<Conversation> conversations;
	private MessagesAdapter messAdapter = null;

	public ConversationsAdapter(Context context, HashSet<Conversation> conversations) {
		this.context = context;
		updateConversations(conversations);
	}

	public List<Conversation> getConversations() {
		return conversations;
	}

	public void updateConversations(HashSet<Conversation> conversations) {
		if (conversations.size() == 0) {
			HashSet<Conversation> noConversation = new HashSet<Conversation>();
			noConversation.add(new Conversation("", "", new TextMessage(new Date(), null, "No conversations")));
			this.conversations = new ArrayList<Conversation>(noConversation);
			notifyDataSetChanged();
		}
		else {
			this.conversations = new ArrayList<Conversation>(conversations);
			Collections.sort(this.conversations);
			notifyDataSetChanged();
			if(messAdapter != null && this.conversations.contains(messAdapter.getId())) {
				messAdapter.updateMessages(this.conversations.get(messAdapter.getId()).getMessages());
				messAdapter.notifyDataSetChanged();
			}
		}
	}

	public int getCount() {
		return conversations.size();
	}

	public Object getItem(int position) {
		if (position >= conversations.size()) {
			throw new IndexOutOfBoundsException();
		}
		return conversations.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public MessagesAdapter getMessAdapter() {
		return messAdapter;
	}

	public void setMessAdapter(MessagesAdapter messAdapter) {
		this.messAdapter = messAdapter;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		long nbUnreadMessages = Client.getUnreadMessages();
		if (nbUnreadMessages > 0) {
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(ns);
			int icon = android.R.drawable.sym_action_call;
			CharSequence tickerText = "New message";
			long when = System.currentTimeMillis();
			Notification notification = new Notification(icon, tickerText, when);
			CharSequence contentTitle = "Roodroid";
			CharSequence contentText = nbUnreadMessages + " unread messages";
			Intent notificationIntent = new Intent(viewGroup.getContext(), ConversationsList.class);
			PendingIntent contentIntent = PendingIntent.getActivity(viewGroup.getContext(), 0, notificationIntent, 0);
			notification.setLatestEventInfo(viewGroup.getContext(), contentTitle, contentText, contentIntent);
			mNotificationManager.notify(1, notification);
		}

		if (position >= conversations.size()) {
			throw new IndexOutOfBoundsException();
		}

		Conversation entry = conversations.get(position);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.conversation_row, null);
		}

		TextView contact = (TextView) convertView.findViewById(R.id.contact);
		contact.setText(entry.getContactName());

		Message lastMessage = entry.getMessages().get(entry.getMessages().size() - 1);

		TextView preview = (TextView) convertView.findViewById(R.id.preview);
		String content = lastMessage.getTextContent();
		if(content.length() > PREVIEWLENGTH) {
			content = content.substring(0, PREVIEWLENGTH);
			content += "...";
		}
		preview.setText(content);


		TextView date = (TextView) convertView.findViewById(R.id.date);
		Date now = new Date();
		String messageDate = "";
		if (lastMessage.getDate().getDate() == now.getDate() && lastMessage.getDate().getMonth() == now.getMonth() && lastMessage.getDate().getYear() == now.getYear()) {
			messageDate += new SimpleDateFormat("HH:mm").format(lastMessage.getDate());
		}
		else if (lastMessage.getDate().getDate() == now.getDate() -1 && lastMessage.getDate().getMonth() == now.getMonth() && lastMessage.getDate().getYear() == now.getYear()) {
			messageDate += "Yest";
		}
		else {
			messageDate += new SimpleDateFormat("dd/MM").format(lastMessage.getDate());
		}
		date.setText(messageDate);

		return convertView;
	}

	@Override
	public void onClick(View view) {
		notifyDataSetChanged();
	}

}