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
	
	/**
	 * Returns Body-Proxy.
	 * 
	 * @return Body-Proxy
	 */
	public HttpResponseMessageBodyProxy bodyProxy();
	
	
	/**
	 * Response-code only instance static-factory-method.
	 * 
	 * @param request
	 * @param responseCode
	 * @return HttpResponseMessage
	 */
	public static HttpResponseMessage build(
			HttpRequestMessage request,
			HttpResponseCode responseCode) {
		
		return new AbstractHttpResponseMessage(
				new HttpResponseStatusLine(request.version(), responseCode),
				HttpHeaderListParser.of(Collections.emptyList()),
				HttpResponseMessageBodyProxy.empty()
				) {
			
					private static final long serialVersionUID = -4802809175264273416L;
		};
	}
	
}
