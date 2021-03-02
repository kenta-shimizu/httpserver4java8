package com.shimizukenta.httpserver;

import java.util.List;

public interface HttpMessage {
	
	/**
	 * Returns Message HTTP-Version.
	 * 
	 * @return Message HTTP-Version
	 */
	public HttpVersion version();
	
	/**
	 * Returns HTTP-Version-String.
	 * 
	 * @return HTTP-Version-String
	 */
	public String versionString();
	
	/**
	 * Returns Header-Line-Strings.
	 * 
	 * @return Header-Line-Strings
	 */
	public List<String> headerLines();
	
	/**
	 * Returns HTTP-Headers.
	 * 
	 * @return HTTP-Headers
	 */
	public List<HttpHeader> headers();
	
	/**
	 * Returns HttpHeaderListParser.
	 * 
	 * @return HttpHeaderListParser
	 */
	public HttpHeaderListParser headerListParser();
	
	/**
	 * Returns body-bytes.
	 * 
	 * @return body-bytes
	 */
	public byte[] body();
	
	/**
	 * Returns Message bytes-data.
	 * 
	 * @return Message bytes-data.
	 */
	public List<byte[]> getBytes();
	
}
