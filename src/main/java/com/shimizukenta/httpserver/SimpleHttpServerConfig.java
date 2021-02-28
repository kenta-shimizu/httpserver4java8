package com.shimizukenta.httpserver;

import java.net.SocketAddress;
import java.nio.file.Path;

import com.shimizukenta.httpserver.generalfileapi.AbstractGeneralFileApiConfig;

public class SimpleHttpServerConfig {
	
	private final AbstractHttpApiServerConfig apiServerConfig;
	private final AbstractGeneralFileApiConfig generalFileApiConfig;
	
	public SimpleHttpServerConfig() {
		
		this.apiServerConfig = new AbstractHttpApiServerConfig() {
			
			private static final long serialVersionUID = -1535658944119408907L;
		};
		
		this.generalFileApiConfig = new AbstractGeneralFileApiConfig() {
			
			private static final long serialVersionUID = -1535658944119408907L;
		};
	}
	
	public AbstractHttpApiServerConfig apiServerConfig() {
		return this.apiServerConfig;
	}
	
	public AbstractGeneralFileApiConfig generalFileApi() {
		return this.generalFileApiConfig;
	}
	
	public boolean addSocketAddress(SocketAddress address) {
		return this.apiServerConfig.addServerAddress(address);
	}
	
	public boolean removeSocketAddress(SocketAddress address) {
		return this.apiServerConfig.removeServerAddress(address);
	}
	
	public void rootPath(Path path) {
		this.generalFileApiConfig.rootPath(path);
	}
	
	public boolean addDirectoryIndex(CharSequence fileName) {
		return this.generalFileApiConfig.addDirectoryIndex(fileName);
	}
	
	public boolean removeDirectoryIndex(CharSequence fileName) {
		return this.generalFileApiConfig.removeDirectoryIndex(fileName);
	}
	
	public boolean addAcceptServerName(CharSequence hostName) {
		return this.apiServerConfig.addAcceptHostName(hostName);
	}
	
	public boolean removeAcceptServerName(CharSequence hostName) {
		return this.apiServerConfig.removeAcceptHostName(hostName);
	}
	
	public void serverName(CharSequence serverName) {
		this.apiServerConfig.serverName(serverName);
	}
	
}
