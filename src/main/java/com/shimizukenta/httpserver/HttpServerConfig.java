package com.shimizukenta.httpserver;

import java.net.SocketAddress;
import java.util.Set;

/**
 * This interface is getter of HttpServer config
 * 
 * @author kenta-shimizu
 *
 */
public interface HttpServerConfig {
	
	/**
	 * Returns Server-Addresses.
	 * 
	 * @return Server-Addresses
	 */
	public Set<SocketAddress> serverAddresses();
	
	/**
	 * Return Connection-Timeout seconds.
	 * 
	 * @return Connection-Timeout seconds
	 */
	public long connectionTimeout();
	
	/**
	 * Returns Server-Name.
	 * 
	 * @return Server-Name
	 */
	public String serverName();
	
	/**
	 * Returns Accept-Host-Names.
	 * 
	 * @return Accept-Host-Names
	 */
	public Set<String> acceptHostNames();
	
	/**
	 * Keep-Alive timeout getter.
	 * 
	 * @return Keep-Alive timeout
	 */
	public long keepAliveTimeout();
	
	/**
	 * Keep-Alive max getter.
	 * 
	 * @return Keep-Alive max
	 */
	public int keepAliveMax();
	
}
