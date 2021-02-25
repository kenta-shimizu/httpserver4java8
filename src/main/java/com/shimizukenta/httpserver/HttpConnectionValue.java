package com.shimizukenta.httpserver;

public class HttpConnectionValue {
	
	public long keepAliveTimeout;
	public int keepAliveMax;
	
	public HttpConnectionValue(long keepAliveTimeout, int keepAliveMax) {
		this.keepAliveTimeout = keepAliveTimeout;
		this.keepAliveMax = keepAliveMax;
	}
	
	public long keepAliveTimeout() {
		synchronized ( this ) {
			return this.keepAliveTimeout;
		}
	}
	
	public int keepAliveMax() {
		synchronized ( this ) {
			return this.keepAliveMax;
		}
	}
	
	public int decreaseKeepAliveMax() {
		synchronized ( this ) {
			-- this.keepAliveMax;
			return this.keepAliveMax;
		}
	}

}
