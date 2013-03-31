package com.example.teste3;

import java.io.Serializable;
import java.util.Date;

/**
 * Message
 * Clasa abstracta ce ar trebui sa poata sustine alte tipuri de mesaj in viitor (de ex: MMS).
 * Toate subclasele trebuie sa implementeze getTextContent(), metoda folosita pentru a
 * afisa mesajul in lista de conversatii.
 * 
 * Un mesaj contine doar datele din acel mesaj 
 * (status, tip, data, continut) dar nicio informatie despre recipient
 * (este stocata la nivelul Conversation).
 * Implementeaza Comparable pentru a sorta mesajele dupa data.
 */
public abstract class Message implements Serializable, Comparable<Message> {
	
	public enum MessageStatus { RECEIVED, SENT, FAILED, TOSEND};
	public enum MessageType { SMS, MMS }
	
	private static final long serialVersionUID = 4927680528209799238L;
	private Date date;
	private MessageStatus messageStatus;
	private MessageType messageType;
	
	public abstract String getTextContent();

	public Message(Date date, MessageStatus messageStatus, MessageType messageType) {
		this.date = date;
		this.messageStatus = messageStatus;
		this.messageType = messageType;
	}
	
	public MessageType getMessageType() {
		return messageType;
	}

	public Date getDate() {
		return date;
	}

	public MessageStatus getMessageStatus() {
		return messageStatus;
	}

	@Override
	public int compareTo(Message m) {
		return date.compareTo(m.date);
	}
}
