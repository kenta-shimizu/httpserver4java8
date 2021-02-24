package com.shimizukenta.httpserver;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractHttpServerConfig implements Serializable {
	
	private static final long serialVersionUID = -438846997741190044L;
	
	private static final long defaultConnectionTimeout = 180;
	
	private final Set<SocketAddress> serverAddresses = new CopyOnWriteArraySet<>();
	private long connectionTimeout;
	
	private long keepAliveTimeout;
	private int keepAliveMax;
	
	public AbstractHttpServerConfig() {
		this.connectionTimeout = defaultConnectionTimeout;
		this.keepAliveTimeout = 5L;
		this.keepAliveMax = 100;
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
	 * Return Connection-Timeout seconds.
	 * 
	 * @return Connection-Timeout seconds
	 */
	public long connectionTimeout() {
		synchronized ( this ) {
			return this.connectionTimeout;
		}
	}
	
	/**
	 * Connection-Timeout setter.
	 * 
	 * @param seconds
	 */
	public void connectionTimeout(long seconds) {
		synchronized ( this ) {
			this.connectionTimeout = seconds;
		}
	}
	
	/**
	 * Keep-Alive timeout getter.
	 * 
	 * @return Keep-Alive timeout
	 */
	public long keepAliveTimeout() {
		synchronized ( this ) {
			return this.keepAliveTimeout;
		}
	}
	
	/**
	 * Keep-Alive timeout setter.
	 * 
	 * @param timeoutSeconds
	 */
	public void keepAliveTimeout(long timeoutSeconds) {
		synchronized ( this ) {
			this.keepAliveTimeout = timeoutSeconds;
		}
	}
	
	/**
	 * Keep-Alive max getter.
	 * 
	 * @return Keep-Alive max
	 */
	public int keepAliveMax() {
		synchronized ( this ) {
			return this.keepAliveMax;
		}
	}
	
	/**
	 * Keep-Alive max setter.
	 * 
	 * @param max
	 */
	public void keepAliveMax(int max) {
		synchronized ( this ) {
			this.keepAliveMax = max;
		}
	}
}
