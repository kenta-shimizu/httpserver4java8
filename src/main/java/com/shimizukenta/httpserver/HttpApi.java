package com.shimizukenta.httpserver;

/**
 * This interface is testing Request Acceptable and building Response.
 * 
 * @author kenta-shimizu
 *
 */
public interface HttpApi {
	
	/**
	 * Returns {@code true} if Request-Message acceptable.
	 * 
	 * @param request
	 * @return {@code true} if Request-Message acceptable
	 */
	public boolean accept(HttpRequestMessage request);
	
	/**
	 * Returns Response-Message from Request-Message.
	 * 
	 * @param request
	 * @param connectionValue
	 * @param serverConfig
	 * @return Response-Message
	 * @throws InterruptedException
	 * @throws HttpServerException
	 */
	public HttpResponseMessage receiveRequest(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue,
			HttpServerConfig serverConfig)
					throws InterruptedException, HttpServerException;
	
}

