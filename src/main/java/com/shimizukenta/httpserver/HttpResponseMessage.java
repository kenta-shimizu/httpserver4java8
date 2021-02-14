package com.shimizukenta.httpserver;

public interface HttpResponseMessage extends HttpMessage {
	
	/**
	 * Returns HTTP-Response Status-Line.
	 * 
	 * <p>
	 * <strong>Not </strong> include CRLF.<br />
	 * </p>
	 * 
	 * @return
	 */
	public String statusLine();
	
	/**
	 * Returns Reference-HTTP-Request-Message.
	 * 
	 * @return Reference-HTTP-Request-Message
	 */
	public HttpRequestMessage referenceRequestMessage();
	
}
