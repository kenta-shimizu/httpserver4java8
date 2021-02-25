package com.shimizukenta.httpserver;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * This abstract class is config-values of HttpServer instance.
 * 
 * @author kenta-shimizu
 *
 */
public abstract class AbstractHttpServerConfig implements Serializable, HttpServerConfig {
	
	private static final long serialVersionUID = -438846997741190044L;
	
	private static final String defaultServerName = "HTTP-SERVER-FOR-JAVA8-1.0.0";
	private static final long defaultConnectionTimeout = 180;
	
	private final Set<SocketAddress> serverAddresses = new CopyOnWriteArraySet<>();
	private long connectionTimeout;
	
	private String serverName;
	private Set<String> acceptHostNames = new CopyOnWriteArraySet<>();
	
	private long keepAliveTimeout;
	private int keepAliveMax;
	
	public AbstractHttpServerConfig() {
		this.connectionTimeout = defaultConnectionTimeout;
		this.serverName = defaultServerName;
		this.keepAliveTimeout = 5L;
		this.keepAliveMax = 100;
	}
	
	@Override
	public Set<SocketAddress> serverAddresses() {
		return Collections.unmodifiableSet(serverAddresses);
	}
	
	/**
	 * Add Server-Address.
	 * 
	 * <p>
	 * <strong>Not</strong> accept {@code null}.<br />
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
	 * <string>Not</strong> accept {@code null}.<br />
	 * </p>
	 * 
	 * @param address
	 * @return {@code true} if remove success
	 */
	public boolean removeServerAddress(SocketAddress address) {
		return serverAddresses.remove(Objects.requireNonNull(address));
	}
	
	@Override
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
	
	@Override
	public String serverName() {
		synchronized ( this ) {
			return this.serverName;
		}
	}
	
	/**
	 * Server-Name setter.
	 * 
	 * <p>
	 * <strong>Not</strong> accept {@code null}.<br />
	 * </p>
	 * 
	 * @param cs
	 */
	public void serverName(CharSequence cs) {
		synchronized ( this ) {
			this.serverName = Objects.requireNonNull(cs).toString();
		}
	}
	
	@Override
	public Set<String> acceptHostNames() {
		return Collections.unmodifiableSet(this.acceptHostNames);
	}
	
	/**
	 * Returns {@code true} if add Accept-Host-Name success.
	 * 
	 * <p>
	 * <string>Not</strong> accept {@code null}.<br />
	 * </p>
	 * 
	 * @param cs
	 * @return {@code true} if add Accept-Host-Name success
	 */
	public boolean addAcceptHostName(CharSequence cs) {
		return this.acceptHostNames.add(Objects.requireNonNull(cs).toString());
	}
	
	/**
	 * Returns {@code true} if remove Accept-Host-Name success.
	 * 
	 * <p>
	 * <string>Not</strong> accept {@code null}.<br />
	 * </p>
	 * 
	 * @param cs
	 * @return {@code true} if remove Accept-Host-Name success
	 */
	public boolean removeAcceptHostName(CharSequence cs) {
		return this.acceptHostNames.remove(Objects.requireNonNull(cs).toString());
	}
	
	@Override
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
	
	@Override
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
