package com.example.teste3.client;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import com.example.teste3.ApplicationManager;
import com.example.teste3.Connection;
import com.example.teste3.Conversation;
import com.example.teste3.Message;
import com.example.teste3.Message.MessageStatus;
import com.example.teste3.Packet;
import com.example.teste3.TCPCommandType;
import com.example.teste3.TextMessage;
import com.example.teste3.activity.ConversationsAdapter;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

/**
 * Client
 * Clasa care administreaza tipul conexiunii (Wifi, Bluetooth).
 * Pastreaza conexiunea cu serverul.
 * Stocheaza conversatiile.
 * Gestioneaza mesajele nou-venite dinspre server.
 * Are metode publice apelate de catre interfata cu userul.
 */
public abstract class Client {

	private String username;
	protected Connection connection;
	private ConversationsDataSource conversationsDataSource;
	private ConversationsAdapter adapter;
	private HashSet<Conversation> conversations;
	private static long unreadMessages;
	private Handler redrawHandler;

	protected final Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			Packet p = (Packet)msg.obj;

			switch (p.getCommandType()) {
			case REQUEST_PASSWORD:
				onServerRequestPassword();
				break;
			case AUTH_FAILED:
				onServerAuthFailed();
				break;
			case REQUEST_HAS_CONTACT:
				onServerRequestHasContact((String)p.getObj());
				break;
			case SERVER_MAX_MSG_REACHED:
				onServerMaxMessageReached();
				break;
			case MSG_RECEIVED:
				onNewConversation((Conversation)p.getObj());
				break;
			case SERVER_EXIT:
				onServerExit();
				break;
			case CONNECTION_EXIT:
				onServerExit();
				connection.disconnect();
				break;
			default:
			}
		}
	};

	public Client(String username) {
		this.username = username;
		this.conversationsDataSource = new ConversationsDataSource();
		this.conversations = conversationsDataSource.getAllConversations();

		adapter = new ConversationsAdapter(ApplicationManager.getInstance().getApplicationContext(), conversations);
	}
	
	public static long getUnreadMessages() {
		return unreadMessages;
	}
	
	public static void resetUnreadMessages() {
		unreadMessages = 0;
	}

	public HashSet<Conversation> getConversations() {
		return conversations;
	}

	public ConversationsAdapter getAdapter() {
		return adapter;
	}

	public void sendTextMessage(String msgContent, String recipientNumber) {
		try {
			Conversation convo = new Conversation(recipientNumber, "", new TextMessage(new Date(), MessageStatus.SENT, msgContent));
			connection.write(this.username, TCPCommandType.MSG_SENT, convo);
			onNewConversation(convo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onNewConversation(Conversation conv) {
		for (Message msg : conv.getMessages()) {
			if (msg.getMessageStatus() == MessageStatus.RECEIVED) {
				unreadMessages++;
			}
		}
		boolean isNew = conversationsDataSource.addConversation(conv);
		if (isNew) {
			conversations.add(conv);
		} else {
			for (Conversation tmp : conversations) {
				if (tmp.getContactPhoneNumber().equals(conv.getContactPhoneNumber())) {
					tmp.getMessages().addAll(conv.getMessages());
					break;
				}
			}
		}
		adapter.updateConversations(conversations);
		
		ApplicationManager.appendLog(Log.INFO, "Contact name", conv.getContactName());
		ApplicationManager.appendLog(Log.INFO, "Phone number", conv.getContactPhoneNumber());
		for (Message m : conv.getMessages()) {
			ApplicationManager.appendLog(Log.INFO, "Message content", m.getTextContent());
		}
	}

	public ConversationsDataSource getConversationsDataSource() {
		return conversationsDataSource;
	}

	public void authenticate() throws IOException {
		this.connection.write(username, TCPCommandType.SEND_ID);
	}

	public void exit() {
		connection.disconnect();
		redrawHandler.sendEmptyMessage(ApplicationManager.CLIENT_DISCONNECTED);
	}

	private void onServerExit() {
		connection.disconnect();
		redrawHandler.sendEmptyMessage(ApplicationManager.CLIENT_DISCONNECTED);
	}

	private void onServerRequestPassword() {

	}

	private void onServerAuthFailed() {
		Toast.makeText(ApplicationManager.getInstance().getApplicationContext(), "Server Authentication failed", Toast.LENGTH_LONG).show();
	}

	private void onServerRequestHasContact(String phoneNumber) {
	    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
	    ContentResolver contentResolver = ApplicationManager.getInstance().getContentResolver();
	    Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

	    boolean exists = false;
	    try {
	        if (contactLookup != null && contactLookup.getCount() > 0) {
	            exists = true;
	        }
	    } finally {
	        if (contactLookup != null) {
	            contactLookup.close();
	        }
	    }
	    
	    try {
		    if (exists) {
		    	connection.write(this.username, TCPCommandType.HAS_CONTACT);
		    } else {
		    	connection.write(this.username, TCPCommandType.HAS_CONTACT);
		    }
	    } catch (IOException e) { }
	}
	

	private void onServerMaxMessageReached() {
		Toast.makeText(ApplicationManager.getInstance().getApplicationContext(), "The limit of messages sent has been reached.", Toast.LENGTH_LONG).show();
	}
	
	public void deleteConversation(Conversation convo) {
		this.conversations.remove(convo);
		this.conversationsDataSource.deleteConversation(convo.getContactPhoneNumber());
		this.adapter.updateConversations(conversations);
	}
	
	public void deleteMessage(Conversation convo, Message msg) {
		if (this.conversations.contains(convo) && convo.getMessages().contains(msg)) {
			if (convo.getMessages().size() <= 1) {
				deleteConversation(convo);
			} else {
				convo.getMessages().remove(msg);
				this.conversationsDataSource.deleteMessage(convo.getContactPhoneNumber(), msg);
			}
		}
		this.adapter.updateConversations(conversations);
	}
	
	public void setRedrawHandler(Handler redrawHandler) {
		this.redrawHandler = redrawHandler;
	}
}
