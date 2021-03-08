package com.shimizukenta.httpserver;

import java.io.IOException;

public abstract class AbstractHttpProxyServer extends AbstractHttpServer {
	
	private final HttpServer in;
	
	public AbstractHttpProxyServer(
			HttpServer in,
			AbstractHttpServerConfig config) {
		
		super(config);
		this.in = in;
		
		this.in.addLogListener(super::putLog);
	}
	
	@Override
	public void close() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.isClosed() ) {
				return;
			}
			
			IOException ioExcept = null;
			
			try {
				super.close();
			}
			catch ( IOException e ) {
				ioExcept = e;
			}
			
			try {
				this.in.close();
			}
			catch ( IOException e ) {
				ioExcept = e;
			}
			
			if ( ioExcept != null ) {
				throw ioExcept;
			}
		}
	}
	
	@Override
	public HttpResponseMessage receiveRequest(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue,
			HttpServerConfig serverConfig)
					throws InterruptedException, HttpServerException {
		
		return this.in.receiveRequest(request, connectionValue, serverConfig);
	}
	
}
