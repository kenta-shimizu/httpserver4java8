package com.shimizukenta.httpserver.cacheproxyserver;

import com.shimizukenta.httpserver.AbstractHttpServerConfig;

public abstract class AbstractHttpCacheProxyServerConfig extends AbstractHttpServerConfig {
	
	private static final long serialVersionUID = 2376343060491395282L;
	
	private static final long defaultCacheAgeSeconds = 24L * 60L * 60L;
	
	private long cacheAge;
	private long cacheAgeMilli;
	
	public AbstractHttpCacheProxyServerConfig() {
		cacheAge(defaultCacheAgeSeconds);
	}
	
	/**
	 * Cache-Age setter.
	 * 
	 * @param seconds
	 */
	public void cacheAge(long seconds) {
		synchronized ( this ) {
			this.cacheAge = seconds;
			this.cacheAgeMilli = seconds * 1000L;
		}
	}
	
	/**
	 * Returns Cache-Age Seconds.
	 * 
	 * @return Cache-Age Seconds
	 */
	public long cacheAgeSeconds() {
		synchronized ( this ) {
			return this.cacheAge;
		}
	}
	
	/**
	 * Returns Cache-Age Milli-Seconds.
	 * 
	 * @return Cache-Age Milli-Seconds
	 */
	public long cacheAgeMilliSeconds() {
		synchronized ( this ) {
			return this.cacheAgeMilli;
		}
	}
	
}
