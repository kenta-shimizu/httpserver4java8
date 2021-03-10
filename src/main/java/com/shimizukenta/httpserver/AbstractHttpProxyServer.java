package com.shimizukenta.httpserver;

import java.io.IOException;

public abstract class AbstractHttpProxyServer extends AbstractHttpServer {
	
	private final HttpServer in;
	
	public AbstractHttpProxyServer(
			HttpServer in,
			AbstractHttpServerConfig config) {
		
		super(config);
		this.in = in;
		
		this.in.addLogListener(this::putLog);
	}
	
	@Override
	public void open() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.isClosed() ) {
				throw new IOException("Alread closed");
			}
			
			if ( this.in.isOpen() ) {
				throw new IOException("Alread opened");
			}
			
			super.open();
		}
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
	public boolean isOpen() {
		
		if ( this.in.isOpen() ) {
			return true;
		}
		
		return super.isOpen();
	}
	
	@Override
	public boolean isClosed() {
		
		if ( this.in.isClosed() ) {
			return true;
		}
		
		return  super.isClosed();
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
