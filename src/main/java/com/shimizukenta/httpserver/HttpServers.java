package com.shimizukenta.httpserver;

import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.shimizukenta.httpserver.cacheproxyserver.AbstractHttpCacheProxyServer;
import com.shimizukenta.httpserver.cacheproxyserver.AbstractHttpCacheProxyServerConfig;
import com.shimizukenta.httpserver.cacheproxyserver.SimpleHttpCacheProxyServerConfig;
import com.shimizukenta.httpserver.generalfileapi.AbstractGeneralFileApi;
import com.shimizukenta.httpserver.jsonapi.AbstractJsonApi;
import com.shimizukenta.httpserver.preflightapi.SimplePreFlightApi;

public class HttpServers {

	protected HttpServers() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static HttpServers inst = new HttpServers();
	}
	
	protected AbstractHttpApiServer buildServer(SimpleHttpServerConfig config, List<? extends HttpApi> apis) {
		
		final AbstractHttpApiServer server = new AbstractHttpApiServer(config.apiServerConfig()) {};
		
		server.addApi(new AbstractHttpApi() {

			@Override
			public boolean accept(HttpRequestMessage request) {
				
			switch ( request.version() ) {
				case HTTP_1_0: {
					return false;
					/* break; */
				}
				case HTTP_1_1:
				case HTTP_2_0:
				default: {
					
					String host = request.headerListParser().host().orElse(null);
					
					if ( host == null ) {
						return true;
					}
					
					final Set<String> hostNames = config.apiServerConfig().acceptHostNames();
					
					if ( hostNames.isEmpty() ) {
						return false;
					}
					
					return ! hostNames.contains(host);
				}
				}
			}
			
			@Override
			public HttpResponseMessage receiveRequest(
					HttpRequestMessage request,
					HttpConnectionValue connectionValue,
					HttpServerConfig serverConfig)
							throws InterruptedException, HttpServerException {
				
				return HttpResponseMessage.build(request, HttpResponseCode.BadRequest);
			}
			
		});
		
		server.addApi(new SimplePreFlightApi());
		
		apis.forEach(server::addApi);
		
		server.addApi(new AbstractGeneralFileApi(config.generalFileApi()) {});
		
		return server;
	}
	
	protected SimpleHttpServerConfig createSimpleHttpServerConfig(SocketAddress address, Path rootPath) {
		
		final SimpleHttpServerConfig config = new SimpleHttpServerConfig();
		
		config.addDirectoryIndex("index.html");
		config.addDirectoryIndex("index.htm");
		
		config.addSocketAddress(address);
		config.rootPath(rootPath);
		
		return config;
	}
	
	
	public static AbstractHttpApiServer simpleHttpServer(SimpleHttpServerConfig config, List<? extends HttpApi> apis) {
		return SingletonHolder.inst.buildServer(config, apis);
	}
	
	public static AbstractHttpApiServer simpleHttpServer(SocketAddress address, Path rootPath, List<? extends HttpApi> apis) {
		HttpServers a = SingletonHolder.inst;
		return a.buildServer(a.createSimpleHttpServerConfig(address, rootPath), apis);
	}
	
	public static AbstractHttpApiServer simpleHttpServer(SimpleHttpServerConfig config, HttpApi... apis) {
		return SingletonHolder.inst.buildServer(config, Arrays.asList(apis));
	}
	
	public static AbstractHttpApiServer simpleHttpServer(SocketAddress address, Path rootPath, HttpApi... apis) {
		HttpServers a = SingletonHolder.inst;
		return a.buildServer(a.createSimpleHttpServerConfig(address, rootPath), Arrays.asList(apis));
	}
	
	public static AbstractHttpApiServer simpleHttpServer(SimpleHttpServerConfig config) {
		return SingletonHolder.inst.buildServer(config, Collections.emptyList());
	}
	
	public static AbstractHttpApiServer simpleHttpServer(SocketAddress address, Path rootPath) {
		HttpServers a = SingletonHolder.inst;
		return a.buildServer(a.createSimpleHttpServerConfig(address, rootPath), Collections.emptyList());
	}
	
	public static AbstractHttpApiServer jsonApiServer(
			SimpleHttpServerConfig config,
			List<? extends AbstractJsonApi> jsonApis) {
		
		return SingletonHolder.inst.buildServer(config, jsonApis);
	}
	
	public static AbstractHttpApiServer jsonApiServer(
			SocketAddress address,
			Path rootPath,
			List<? extends AbstractJsonApi> jsonApis) {
		
		HttpServers a = SingletonHolder.inst;
		return a.buildServer(a.createSimpleHttpServerConfig(address, rootPath), jsonApis);
	}
	
	public static AbstractHttpApiServer jsonApiServer(
			SimpleHttpServerConfig config,
			AbstractJsonApi... jsonApis) {
		
		return SingletonHolder.inst.buildServer(config, Arrays.asList(jsonApis));
	}
	
	public static AbstractHttpApiServer jsonApiServer(
			SocketAddress address,
			Path rootPath,
			AbstractJsonApi... jsonApis) {
		
		HttpServers a = SingletonHolder.inst;
		return a.buildServer(a.createSimpleHttpServerConfig(address, rootPath), Arrays.asList(jsonApis));
	}
	
	public static AbstractHttpCacheProxyServer wrapSimpleCacheProxyServer(
			HttpServer server,
			SocketAddress address
			) {
		
		final SimpleHttpCacheProxyServerConfig config = new SimpleHttpCacheProxyServerConfig();
		config.addServerAddress(address);
		
		return wrapSimpleCacheProxyServer(server, config);
	}
	
	public static AbstractHttpCacheProxyServer wrapSimpleCacheProxyServer(
			HttpServer server,
			AbstractHttpCacheProxyServerConfig config
			) {
		
		return new AbstractHttpCacheProxyServer(server, config) {};
	}
	
}
