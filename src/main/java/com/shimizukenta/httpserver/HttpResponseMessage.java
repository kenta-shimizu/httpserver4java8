package com.shimizukenta.httpserver;

import java.util.Collections;

public interface HttpResponseMessage extends HttpMessage {
	
	/**
	 * Returns HTTP-Response Status-Line.
	 * 
	 * <p>
	 * <strong>Not</strong> include CRLF.<br />
	 * </p>
	 * 
	 * @return
	 */
	public String statusLine();
	
	
	public static HttpResponseMessage build(
			HttpRequestMessage request,
			HttpResponseCode responseCode) {
		
		return new AbstractHttpResponseMessage(
				new HttpResponseStatusLine(request.version(), responseCode),
				HttpHeaderListParser.of(Collections.emptyList()),
				new byte[0]
				) {};
	}
	
}
