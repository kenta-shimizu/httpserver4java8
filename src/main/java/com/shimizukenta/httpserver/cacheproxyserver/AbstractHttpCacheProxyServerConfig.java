package com.shimizukenta.httpserver.cacheproxyserver;

import com.shimizukenta.httpserver.AbstractHttpServerConfig;

public abstract class AbstractHttpCacheProxyServerConfig extends AbstractHttpServerConfig {
	
	private static final long serialVersionUID = 2376343060491395282L;
	
	private static final long defaultCacheAgeSeconds = 86400L;
	
	private long cacheAge;
	
	public AbstractHttpCacheProxyServerConfig() {
		this.cacheAge = defaultCacheAgeSeconds;
	}
	
	public void cacheAge(long seconds) {
		synchronized ( this ) {
			this.cacheAge = seconds;
		}
	}
	
	public long cacheAge() {
		synchronized ( this ) {
			return this.cacheAge;
		}
	}
	
}
