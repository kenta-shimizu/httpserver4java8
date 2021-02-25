package com.shimizukenta.httpserver.jsonapi;

import com.shimizukenta.httpserver.HttpApi;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpServerException;

/**
 * This interface is testing Request Acceptable and building Response.
 * 
 * @author kenta-shimizu
 *
 */
public interface JsonApi extends HttpApi {
	
	/**
	 * Build-JSON-String.
	 * 
	 * @param request
	 * @return JSON-String
	 * @throws InterruptedException
	 * @throws HttpServerException
	 */
	public String buildJson(HttpRequestMessage request)throws InterruptedException, HttpServerException;
	
}
