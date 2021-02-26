package com.shimizukenta.httpserver;

public interface HttpServerResponseMessageLog extends HttpServerLog {
	
	/**
	 * Returns Http-Response-Message.
	 * 
	 * @return Http-Response-Message
	 */
	public HttpResponseMessage responseMessage();
}
