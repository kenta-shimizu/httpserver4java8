package com.shimizukenta.httpserver;

public interface HttpServerRequestMessageLog extends HttpServerLog {
	
	/**
	 * Returns Http-Request-Message.
	 * 
	 * @return Http-Request-Message
	 */
	public HttpRequestMessage requestMessage();
}
