package com.shimizukenta.httpserver;

import java.io.IOException;

public abstract class AbstractHttpProxyServer extends AbstractHttpServer {
	
	private final AbstractHttpServer in;
	
	public AbstractHttpProxyServer(AbstractHttpServer in, AbstractHttpServerConfig config) {
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
	protected HttpResponseMessage receiveRequest(HttpRequestMessage message, HttpConnectionValue connectionValue,
			HttpServerConfig serverConfig) throws InterruptedException, HttpServerException {
		
		return this.in.receiveRequest(message, connectionValue, serverConfig);
	}
	
}
