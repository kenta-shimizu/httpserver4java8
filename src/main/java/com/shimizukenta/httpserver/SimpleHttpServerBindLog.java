package com.shimizukenta.httpserver;

import java.net.SocketAddress;

public final class SimpleHttpServerBindLog extends AbstractHttpServerBindLog {
	
	private static final long serialVersionUID = 7261574207749884668L;

	private SimpleHttpServerBindLog(CharSequence subject, SocketAddress socketAddress, boolean binding) {
		super(subject, socketAddress, binding);
	}
	
	
	private static final String commonTryBind = "HTTP-Server try-bind";
	private static final String commonBinded = "HTTP-Server binded";
	private static final String commonClosed = "HTTP-Server closed";
	
	public static SimpleHttpServerBindLog tryBind(SocketAddress socketAddress) {
		return new SimpleHttpServerBindLog(commonTryBind, socketAddress, false);
	}
	
	public static SimpleHttpServerBindLog binded(SocketAddress socketAddress) {
		return new SimpleHttpServerBindLog(commonBinded, socketAddress, true);
	}
	
	public static SimpleHttpServerBindLog closed(SocketAddress socketAddress) {
		return new SimpleHttpServerBindLog(commonClosed, socketAddress, false);
	}
	
}
