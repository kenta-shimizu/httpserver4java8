package com.shimizukenta.httpserver.cacheproxyserver;

import com.shimizukenta.httpserver.AbstractHttpProxyServer;
import com.shimizukenta.httpserver.AbstractHttpServerConfig;
import com.shimizukenta.httpserver.HttpServer;

public abstract class AbstractHttpCacheProxyServer extends AbstractHttpProxyServer {
	
	private final AbstractHttpCacheProxyServerConfig config;
	
	public AbstractHttpCacheProxyServer(HttpServer in, AbstractHttpCacheProxyServerConfig config) {
		super(in, config);
		this.config = config;
	}
	
	
}
