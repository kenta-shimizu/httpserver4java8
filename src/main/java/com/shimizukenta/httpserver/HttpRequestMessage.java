package com.shimizukenta.httpserver;

public interface HttpRequestMessage extends HttpMessage {
	
	/**
	 * Returns Request-Line.
	 * 
	 * <p>
	 * <string>Not</strong> include CRLF.<br />
	 * </p>
	 * 
	 * @return HTTP-Request-Line-String
	 */
	public String requestLine();
	
	/**
	 * Returns URI.
	 * 
	 * @return URI
	 */
	public String uri();
	
	/**
	 * Returns Absolute-Path from URI.
	 * 
	 * @return Absolute-Path
	 */
	public String absPath();
	
	/**
	 * Returns Method.
	 * 
	 * @return Method
	 */
	public HttpRequestMethod method();
	
	/**
	 * Returns Method-String.
	 * 
	 * @return Method-String
	 */
	public String methodString();
	
}
