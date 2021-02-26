package com.shimizukenta.httpserver;

import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractHttpServerBindLog extends AbstractHttpServerLog implements HttpServerBindLog {
	
	private static final long serialVersionUID = -7390341348613331893L;
	
	private final SocketAddress bindSocketAddress;
	private final boolean binding;
	
	public AbstractHttpServerBindLog(CharSequence subject, LocalDateTime timestamp, SocketAddress socketAddress, boolean binding) {
		super(subject, timestamp, socketAddress);
		this.bindSocketAddress = socketAddress;
		this.binding = binding;
	}
	
	public AbstractHttpServerBindLog(CharSequence subject, SocketAddress socketAddress, boolean binding) {
		super(subject, socketAddress);
		this.bindSocketAddress = socketAddress;
		this.binding = binding;
	}
	
	@Override
	public Optional<String> optionalValueString() {
		return Optional.of(
				"{\"socketAddress\":\""
		+ Objects.toString(bindSocketAddress)
		+ "\",\"binding\":"
		+ binding + "}");
	}
	
	@Override
	public SocketAddress socketAddress() {
		return this.bindSocketAddress;
	}

	@Override
	public boolean binding() {
		return this.binding;
	}

}
