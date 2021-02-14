package com.shimizukenta.httpserver;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractHttpServerConfig {
	
	private final Set<SocketAddress> serverAddresses = new CopyOnWriteArraySet<>();
	private String serverName;
	
	public AbstractHttpServerConfig() {
		this.serverName = "HTTPSERVER";
	}
	
	/**
	 * Returns Server-Addresses.
	 * 
	 * @return Server-Addresses
	 */
	public Set<SocketAddress> serverAddresses() {
		return Collections.unmodifiableSet(serverAddresses);
	}
	
	/**
	 * Add Server-Address.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param address
	 * @return {@code true} if add success
	 */
	public boolean addServerAddress(SocketAddress address) {
		return serverAddresses.add(Objects.requireNonNull(address));
	}
	
	/**
	 * Remove Server-Address.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param address
	 * @return {@code true} if remove success
	 */
	public boolean removeServerAddress(SocketAddress address) {
		return serverAddresses.remove(Objects.requireNonNull(address));
	}
	
	/**
	 * Server-Name getter.
	 * 
	 * @return Server-Name
	 */
	public String serverName() {
		synchronized ( this ) {
			return this.serverName;
		}
	}
	
	/**
	 * Server-Name setter.
	 * 
	 * <p>
	 * Not accept {@code null}.<br />
	 * </p>
	 * 
	 * @param name
	 */
	public void serverName(CharSequence name) {
		synchronized ( this ) {
			this.serverName = Objects.requireNonNull(name).toString();
		}
	}
	
}
