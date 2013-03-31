package com.example.teste3;
/**
 * TCPCommandType
 * Enumeratie folosita in Packet pentru ca destinatarul sa stie
 * cum trebuie sa proceseze informatia.
 */
public enum TCPCommandType {
	SEND_ID,
	REQUEST_PASSWORD,
	SEND_PASSWORD,
	AUTH_FAILED,
	AUTH_SUCCESS,
	ACK_AUTH,
	MSG_SENT,
	REQUEST_HAS_CONTACT,
	HAS_CONTACT,
	HAS_NOT_CONTACT,
	MSG_RECEIVED,
	MSG_DELIVERED,
	SERVER_MAX_MSG_REACHED,
	SERVER_EXIT,
	CLIENT_EXIT,
	CONNECTION_EXIT
}
