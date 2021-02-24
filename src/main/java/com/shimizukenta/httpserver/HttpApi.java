package com.shimizukenta.httpserver;

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
	 * @return Response-Message
	 * @throws InterruptedException
	 * @throws HttpServerException
	 */
	public HttpResponseMessage receiveRequest(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue)
					throws InterruptedException, HttpServerException;
	
}

