package com.shimizukenta.httpserver;

import java.io.Closeable;
import java.io.IOException;

/**
 * 
 * 
 * @author kenta-shimizu
 *
 */
public interface HttpServer extends Closeable {
	
	/**
	 * Open server.
	 * 
	 * @throws IOException
	 */
	public void open() throws IOException;
	
	/**
	 * Returns {@code true} if opened and <strong>not</strong> closed.
	 * 
	 * @return {@code true} if opened and <strong>not</strong> closed
	 */
	public boolean isOpen();
	
	/**
	 * Returns {@code true} if closed.
	 * 
	 * @return {@code true} if closed
	 */
	public boolean isClosed();
	
	/**
	 * Returns Response-Message from Request-Message.
	 * 
	 * @param message
	 * @param connectionValue
	 * @param serverConfig
	 * @return HttpResponseMessage
	 * @throws InterruptedException
	 * @throws HttpServerException
	 */
	public HttpResponseMessage receiveRequest(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue,
			HttpServerConfig serverConfig)
					throws  InterruptedException, HttpServerException;
	
	/**
	 * Add Http-Server-Log Listener.
	 * 
	 * @param l
	 * @return {@code true} if add success
	 */
	public boolean addLogListener(HttpServerLogListener l);
	
	/**
	 * Remove Http-Server-Log Listener.
	 * 
	 * @param l
	 * @return {@code true} if remove success
	 */
	public boolean removeLogListener(HttpServerLogListener l);
	
}
