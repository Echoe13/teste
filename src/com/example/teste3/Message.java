package com.example.teste3;

import java.io.Serializable;
import java.util.Date;

/**
 * Message
 * Class made abstract to allow new message types in the future (eg: MMS).
 * All the subclasses has to implement getTextContent(), method used to
 * render the message in the messages list.
 * 
 * A message contains only the data on the message (status, type, date, content) but no data about the recipient
 * (it is stored at the Conversation level).
 * Implements Comparable to sort two messages according to their dates.
 * 
 * @author Jonathan Perichon <jonathan.perichon@gmail.com>
 * @author Lucas Gerbeaux <lucas.gerbeaux@gmail.com>
 *
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
