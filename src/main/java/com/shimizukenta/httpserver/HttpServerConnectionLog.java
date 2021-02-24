package com.shimizukenta.httpserver;

import java.net.SocketAddress;

/**
 * This interface is HTTP-Server-Connection-Log.
 * 
 * @author kenta-shimizu
 *
 */
public interface HttpServerConnectionLog extends HttpServerLog {
	
	/**
	 * Returns Connection-Client-Socket-Address.
	 * 
	 * @return Connection-Client-Socket-Address
	 */
	public SocketAddress client();
	
	/**
	 * Returns Connection-Server-Socket-Address.
	 * 
	 * @return Connection-Server-Socket-Address
	 */
	public SocketAddress server();
	
	/**
	 * Returns {@code true} if connecting.
	 * 
	 * @return {@code true} if connecting
	 */
	public boolean connecting();
	
}
