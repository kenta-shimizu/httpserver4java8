package com.shimizukenta.httpserver;

import java.util.Objects;

public abstract class AbstractHttpApiConfig {
	
	private static final String defaultServerName = "HTTP-SERVER-NAME";
	
	private String serverName;
	
	public AbstractHttpApiConfig() {
		this.serverName = defaultServerName;
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
	 * @param serverName
	 */
	public void serverName(CharSequence serverName) {
		synchronized ( this ) {
			this.serverName = Objects.requireNonNull(serverName).toString();
		}
	}
	
}
