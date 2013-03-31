package com.example.teste3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Conversation
 * O conversatie reprezinta mesajele schimbate cu acelasi recipient.
 * Implementeaza Comparable pentru a sorta doua conversatii dupa ultimul mesaj (mai exact, data lui).
 */
public class Conversation implements Comparable<Conversation>, Serializable {
	
	private static final long serialVersionUID = -5711653949793208241L;
	private String contactPhoneNumber;
	private String contactName;
	private List<Message> messages;
	
	public Conversation(String contactPhoneNumber, String contactName, Message firstMessage) {
		this.contactPhoneNumber = contactPhoneNumber;
		this.contactName = contactName;
		this.messages = new ArrayList<Message>(1);
		this.messages.add(firstMessage);
	}
	
	public Conversation(String contactPhoneNumber, String contactName, List<Message> messages) {
		this.contactPhoneNumber = contactPhoneNumber;
		this.contactName = contactName;
		this.messages = new ArrayList<Message>(messages);
	}
	
	public String getContactPhoneNumber() {
		return contactPhoneNumber;
	}

	public String getContactName() {
		return contactName;
	}

	public List<Message> getMessages() {
		return messages;
	}
	
	@Override
	public int compareTo(Conversation another) {
		return another.messages.get(another.messages.size() - 1).compareTo(messages.get(messages.size() - 1));
	}
}
