package com.shimizukenta.httpserver;

import java.io.IOException;
import java.util.List;

public interface HttpResponseMessageBodyProxy {
	
	/**
	 * Returns most high priority HttpEncodingResult.
	 * 
	 * @param acceptEncodings
	 * @return most high priority HttpEncodingResult
	 * @throws IOException
	 */
	public HttpEncodingResult get(List<HttpEncoding> acceptEncodings) throws IOException;
	
	/**
	 * Returns HttpEncodingResult.
	 * 
	 * @param encoding
	 * @return HttpEncodingResult
	 * @throws IOException
	 */
	public HttpEncodingResult get(HttpEncoding encoding) throws IOException;
	
	
	public static HttpResponseMessageBodyProxy empty() {
		
		return new AbstractHttpResponseMessageBodyProxy(new byte[0]) {

			private static final long serialVersionUID = -9121506528159702888L;
		};
	}
}
