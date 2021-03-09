package com.shimizukenta.httpserver;

import java.util.Collections;

public interface HttpResponseMessage extends HttpMessage {
	
	/**
	 * Returns HTTP-Response Status-Line.
	 * 
	 * @return HttpRresponseStatusLine
	 */
	public HttpResponseStatusLine statusLine();
	
	/**
	 * Returns Status-Code.
	 * 
	 * @return Status-Code
	 */
	public HttpResponseCode statusCode();
	
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
