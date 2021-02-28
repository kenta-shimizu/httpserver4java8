package com.shimizukenta.httpserver;

import java.io.Serializable;

public final class HttpRequestLineParser implements Serializable {
	
	private static final long serialVersionUID = 3236966459617191407L;
	
	private final String line;
	private final String method;
	private final String uri;
	private final String version;
	
	private HttpRequestLineParser(String line, String method, String uri, String version) {
		this.line = line;
		this.method = method;
		this.uri = uri;
		this.version = version;
	}
	
	/**
	 * Returns line.
	 * 
	 * @return line
	 */
	public String line() {
		return this.line;
	}
	
	/**
	 * Returns Method-String.
	 * 
	 * @return Method-String
	 */
	public String method() {
		return this.method;
	}
	
	/**
	 * Returns URI-String.
	 * 
	 * @return URI-String
	 */
	public String uri() {
		return this.uri;
	}
	
	/**
	 * Returns Absolute-Path from URI.
	 * 
	 * @return Absolute-Path
	 */
	public String absPath() {
		String[] ss = this.uri.split("\\?", 2);
		return ss[0];
	}
	
	/**
	 * Returns HttpRequeryQuery from URI.
	 * 
	 * @return HttpRequestQuery from URI
	 * @throws HttpServerRequestMessageParseException
	 */
	public HttpRequestQuery getQueryFromUri() throws HttpServerRequestMessageParseException {
		String[] ss = this.uri.split("\\?", 2);
		String p = ss.length == 2 ? ss[1] : "";
		return HttpRequestQuery.from(p);
	}
	
	/**
	 * Returns Version-String.
	 * 
	 * @return Version-String
	 */
	public String version() {
		return this.version;
	}
	
	private static final String SP = " ";
	
	/**
	 * Instance static-factory-method from Request-Line-String.
	 * 
	 * @param line
	 * @return Request-Line-Parser
	 * @throws HttpServerRequestMessageParseException
	 */
	public static HttpRequestLineParser fromLine(CharSequence line) throws HttpServerRequestMessageParseException {
		
		if ( line == null ) {
			throw new HttpServerRequestMessageParseException("line require not null");
		}
		
		String s = line.toString().trim();
		
		String[] ss = s.split(SP, 3);
		
		if ( ss.length != 3 ) {
			throw new HttpServerRequestMessageParseException("Request-Line: \"" + line.toString() + "\"");
		}
		
		return new HttpRequestLineParser(s, ss[0], ss[1], ss[2]);
	}
	
}
