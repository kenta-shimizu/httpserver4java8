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
	
	/**
	 * Returns Http-Response-Message from Http HEAD METHOD.
	 * 
	 * @return Http-HEAD-only-Response-Message
	 */
	public byte[] getHeadBytes();
	
	public static HttpResponseMessage build(
			HttpRequestMessage request,
			HttpResponseCode responseCode) {
		
		return new AbstractHttpResponseMessage(
				new HttpResponseStatusLine(request.version(), responseCode),
				HttpHeaderListParser.of(Collections.emptyList()),
				Collections.singletonList(new byte[0])
				) {
			
					private static final long serialVersionUID = 2972199851752084860L;
		};
	}
	
}
