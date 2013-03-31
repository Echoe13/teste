package com.example.teste3.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import com.example.teste3.Conversation;
import com.example.teste3.Message;
import com.example.teste3.TextMessage;
import com.example.teste3.Message.MessageStatus;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * ConversationsDataSource
 * Manage the data source for Conversations.
 * 
 * It has public methods that interact directly with the data source 
 * (eg: to add a new conversation).
 * 
 * @author Jonathan Perichon <jonathan.perichon@gmail.com>
 * @author Lucas Gerbeaux <lucas.gerbeaux@gmail.com>
 *
 */
public class ConversationsDataSource {

	private SQLiteDatabase database;
	private ConversationsHelper dbHelper;
	private String[] columnsConversations = { 
			ConversationsHelper.COL_CONVID,
			ConversationsHelper.COL_CONVCONTACTPHONENUMBER,
			ConversationsHelper.COL_CONVCONTACTNAME
	};
	
	private String[] columnsMessages = {
			ConversationsHelper.COL_MSGIDCONV,
			ConversationsHelper.COL_MSGSTATUS,
			ConversationsHelper.COL_MSGDATE,
			ConversationsHelper.COL_MSGCONTENT,
			ConversationsHelper.COL_MSGTYPE
	};

	public ConversationsDataSource() {
		dbHelper = new ConversationsHelper();
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		dbHelper.close();
	}

	public boolean addConversation(Conversation convo) {
		open();
		Cursor cursor = database.query(ConversationsHelper.TAB_CONV, new String[] {ConversationsHelper.COL_CONVID}, ConversationsHelper.COL_CONVCONTACTPHONENUMBER + "= ?", new String[] { convo.getContactPhoneNumber() }, null, null, null);
		cursor.moveToFirst();
		long convId;
		boolean isNew; 
		if (isNew = cursor.isAfterLast()) {
			ContentValues values = new ContentValues();
			values.put(ConversationsHelper.COL_CONVCONTACTPHONENUMBER, convo.getContactPhoneNumber());
			values.put(ConversationsHelper.COL_CONVCONTACTNAME, convo.getContactName());
			convId = database.insert(ConversationsHelper.TAB_CONV, null, values);
		} else {
			convId = cursor.getLong(0);
		}
		cursor.close();
		
		for (Message m : convo.getMessages()) {
			ContentValues v = new ContentValues();
			v.put(ConversationsHelper.COL_MSGIDCONV, convId);
			v.put(ConversationsHelper.COL_MSGDATE, m.getDate().getTime());
			v.put(ConversationsHelper.COL_MSGSTATUS, m.getMessageStatus().ordinal());
			v.put(ConversationsHelper.COL_MSGTYPE, m.getMessageType().ordinal());
			v.put(ConversationsHelper.COL_MSGCONTENT, m.getTextContent());
			
			database.insert(ConversationsHelper.TAB_MSG, null, v);
		}
		close();
		return isNew;
	}
	
	public void deleteConversation(String contactPhoneNumber) {
		open();
		database.execSQL("PRAGMA foreign_keys=ON;");
		database.delete(ConversationsHelper.TAB_CONV, ConversationsHelper.COL_CONVCONTACTPHONENUMBER + " = ?", new String[] { contactPhoneNumber });
		close();
	}
	
	public void deleteMessage(String contactPhoneNumber, Message msg) {
		open();
		Cursor cursor = database.query(ConversationsHelper.TAB_CONV, new String[] {ConversationsHelper.COL_CONVID}, ConversationsHelper.COL_CONVCONTACTPHONENUMBER + "= ?", new String[] { contactPhoneNumber }, null, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			long convId = cursor.getLong(0);
			database.delete(ConversationsHelper.TAB_MSG, ConversationsHelper.COL_MSGIDCONV + " = ? AND " + ConversationsHelper.COL_MSGCONTENT + " = ? AND " + ConversationsHelper.COL_MSGDATE + " = ?", new String[] { convId+"", msg.getTextContent(), msg.getDate().getTime()+"" });
		}
		cursor.close();
		close();
	}

	public HashSet<Conversation> getAllConversations() {
		open();
		HashSet<Conversation> conversations = new HashSet<Conversation>();
		Cursor cursor = database.query(ConversationsHelper.TAB_CONV, columnsConversations, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			conversations.add(cursorToConversation(cursor));
			cursor.moveToNext();
		}
		cursor.close();
		close();
		return conversations;
	}
	
	private Conversation cursorToConversation(Cursor cursor) {
		long id = cursor.getLong(0);
		String contactPhoneNumber = cursor.getString(1);
		String contactName = cursor.getString(2);
		
		List<Message> messages = new ArrayList<Message>();
		Cursor cursorMessages = database.query(ConversationsHelper.TAB_MSG, columnsMessages, ConversationsHelper.COL_MSGIDCONV + "=" + id, null, null, null, null);
		cursorMessages.moveToFirst();
		while (!cursorMessages.isAfterLast()) {
			Message m = cursorToMessage(cursorMessages);
			messages.add(m);
			cursorMessages.moveToNext();
		}
		
		return new Conversation(contactPhoneNumber, contactName, messages);
	}
	
	private Message cursorToMessage(Cursor cursor) {
		Date date = new Date(cursor.getLong(2));
		String content = cursor.getString(3);
		MessageStatus messageStatus = MessageStatus.RECEIVED;
		return new TextMessage(date, messageStatus, content);
	}
}