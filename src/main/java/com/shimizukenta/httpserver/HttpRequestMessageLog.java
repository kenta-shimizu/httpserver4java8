package com.shimizukenta.httpserver;

public interface HttpRequestMessageLog extends HttpServerLog {
	
	/**
	 * Returns Http-Request-Message.
	 * 
	 * @return Http-Request-Message
	 */
	public HttpRequestMessage requestMessage();
}
