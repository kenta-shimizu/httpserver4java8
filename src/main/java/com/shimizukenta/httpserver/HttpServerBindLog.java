package com.shimizukenta.httpserver;

import java.net.SocketAddress;

/**
 * This interface is HTTP-Server-Bind-Log.
 * 
 * @author kenta-shimizu
 *
 */
public interface HttpServerBindLog extends HttpServerLog {
	
	/**
	 * Returns Bind-Socket-Address.
	 * 
	 * @return Bind-Socket-Address
	 */
	public SocketAddress socketAddress();
	
	/**
	 * Returns {@code true} if binding.
	 * 
	 * @return {@code true} if binding
	 */
	public boolean binding();
	
}
