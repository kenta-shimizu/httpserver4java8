package com.shimizukenta.httpserver;

public abstract class AbstractHttpApi implements HttpApi {
	
	private final AbstractHttpApiConfig config;
	
	public AbstractHttpApi(AbstractHttpApiConfig config) {
		this.config = config;
	}
	
	protected HttpHeader serverNameHeader() {
		
		return new AbstractHttpHeader("Server", config.serverName()) {
			
			private static final long serialVersionUID = -5069387462307878250L;
		};
	}
	
}
