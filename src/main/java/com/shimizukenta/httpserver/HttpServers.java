package com.shimizukenta.httpserver;

import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.shimizukenta.httpserver.generalfileapi.AbstractGeneralFileApi;
import com.shimizukenta.httpserver.jsonapi.AbstractJsonApi;
import com.shimizukenta.httpserver.preflight.SimplePreFlightApi;

public class HttpServers {

	protected HttpServers() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static HttpServers inst = new HttpServers();
	}
	
	protected HttpServer buildServer(SimpleHttpServerConfig config, List<? extends HttpApi> apis) {
		
		final AbstractHttpApiServer server = new AbstractHttpApiServer(config.apiServerConfig()) {};
		
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
	
	
	public static HttpServer simpleHttpServer(SimpleHttpServerConfig config, List<? extends HttpApi> apis) {
		return SingletonHolder.inst.buildServer(config, apis);
	}
	
	public static HttpServer simpleHttpServer(SocketAddress address, Path rootPath, List<? extends HttpApi> apis) {
		HttpServers a = SingletonHolder.inst;
		return a.buildServer(a.createSimpleHttpServerConfig(address, rootPath), apis);
	}
	
	public static HttpServer simpleHttpServer(SimpleHttpServerConfig config, HttpApi... apis) {
		return SingletonHolder.inst.buildServer(config, Arrays.asList(apis));
	}
	
	public static HttpServer simpleHttpServer(SocketAddress address, Path rootPath, HttpApi... apis) {
		HttpServers a = SingletonHolder.inst;
		return a.buildServer(a.createSimpleHttpServerConfig(address, rootPath), Arrays.asList(apis));
	}
	
	public static HttpServer simpleHttpServer(SimpleHttpServerConfig config) {
		return SingletonHolder.inst.buildServer(config, Collections.emptyList());
	}
	
	public static HttpServer simpleHttpServer(SocketAddress address, Path rootPath) {
		HttpServers a = SingletonHolder.inst;
		return a.buildServer(a.createSimpleHttpServerConfig(address, rootPath), Collections.emptyList());
	}
	
	public static HttpServer jsonApiServer(
			SimpleHttpServerConfig config,
			List<? extends AbstractJsonApi> jsonApis) {
		
		return SingletonHolder.inst.buildServer(config, jsonApis);
	}
	
	public static HttpServer jsonApiServer(
			SocketAddress address,
			Path rootPath,
			List<? extends AbstractJsonApi> jsonApis) {
		
		HttpServers a = SingletonHolder.inst;
		return a.buildServer(a.createSimpleHttpServerConfig(address, rootPath), jsonApis);
	}
	
	public static HttpServer jsonApiServer(
			SimpleHttpServerConfig config,
			AbstractJsonApi... jsonApis) {
		
		return SingletonHolder.inst.buildServer(config, Arrays.asList(jsonApis));
	}
	
	public static HttpServer jsonApiServer(
			SocketAddress address,
			Path rootPath,
			AbstractJsonApi... jsonApis) {
		
		HttpServers a = SingletonHolder.inst;
		return a.buildServer(a.createSimpleHttpServerConfig(address, rootPath), Arrays.asList(jsonApis));
	}
	
}
