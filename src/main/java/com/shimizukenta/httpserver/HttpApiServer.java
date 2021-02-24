package com.shimizukenta.httpserver;

public interface HttpApiServer extends HttpServer {
	
	/**
	 * Returns {@code true} if add success.
	 * 
	 * @param api
	 * @return {@code true} if add success
	 */
	public boolean addApi(HttpApi api);
	
	/**
	 * Returns {@code true} if remove success.
	 * 
	 * @param api
	 * @return {@code true} if remove success
	 */
	public boolean removeApi(HttpApi api) ;
	
}
