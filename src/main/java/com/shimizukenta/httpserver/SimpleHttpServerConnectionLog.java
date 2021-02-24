package com.shimizukenta.httpserver;

import java.net.SocketAddress;

public final class SimpleHttpServerConnectionLog extends AbstractHttpServerConnectionLog {
	
	private static final long serialVersionUID = -3979730973207183056L;

	private SimpleHttpServerConnectionLog(
			CharSequence subject,
			SocketAddress server,
			SocketAddress client,
			boolean connecting) {
		
		super(subject, server, client, connecting);
	}
	
	private static final String commonAccept = "Http-Server connection accept";
	private static final String commonClosed = "Http-Server connection closed";
	
	public static SimpleHttpServerConnectionLog accept(SocketAddress server, SocketAddress client) {
		return new SimpleHttpServerConnectionLog(commonAccept, server, client, true);
	}
	
	public static SimpleHttpServerConnectionLog closed(SocketAddress server, SocketAddress client) {
		return new SimpleHttpServerConnectionLog(commonClosed, server, client, false);
	}
	
}
