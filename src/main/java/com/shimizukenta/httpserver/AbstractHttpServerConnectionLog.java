package com.shimizukenta.httpserver;

import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractHttpServerConnectionLog extends AbstractHttpServerLog implements HttpServerConnectionLog {
	
	private static final long serialVersionUID = -5313961180568482082L;
	
	private final SocketAddress server;
	private final SocketAddress client;
	private final boolean connecting;
	
	public AbstractHttpServerConnectionLog(CharSequence subject, LocalDateTime timestamp, SocketAddress server, SocketAddress client, boolean connecting) {
		super(subject, timestamp);
		this.server = server;
		this.client = client;
		this.connecting = connecting;
	}
	
	public AbstractHttpServerConnectionLog(CharSequence subject, SocketAddress server, SocketAddress client, boolean connecting) {
		super(subject);
		this.server = server;
		this.client = client;
		this.connecting = connecting;
	}
	
	@Override
	public Optional<String> optionalValueString() {
		return Optional.of("{\"server\":\"" + Objects.toString(this.server) + "\",\"client\":\"" + Objects.toString(this.client) + "\",\"connecting\":" + this.connecting + "}");
	}
	
	@Override
	public SocketAddress client() {
		return this.client;
	}

	@Override
	public SocketAddress server() {
		return this.server;
	}

	@Override
	public boolean connecting() {
		return this.connecting;
	}
	
}
