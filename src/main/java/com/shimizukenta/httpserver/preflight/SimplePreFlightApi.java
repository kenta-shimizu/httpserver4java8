package com.shimizukenta.httpserver.preflight;

import java.util.ArrayList;
import java.util.List;

import com.shimizukenta.httpserver.AbstractHttpResponseMessage;
import com.shimizukenta.httpserver.HttpConnectionValue;
import com.shimizukenta.httpserver.HttpContentEncoder;
import com.shimizukenta.httpserver.HttpHeader;
import com.shimizukenta.httpserver.HttpHeaderListParser;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpRequestMethod;
import com.shimizukenta.httpserver.HttpResponseCode;
import com.shimizukenta.httpserver.HttpResponseMessage;
import com.shimizukenta.httpserver.HttpResponseStatusLine;
import com.shimizukenta.httpserver.HttpServerConfig;
import com.shimizukenta.httpserver.HttpServerException;

public class SimplePreFlightApi extends AbstractPreFlightApi {
	
	public SimplePreFlightApi() {
		super();
	}
	
	@Override
	public HttpResponseMessage receiveRequest(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue,
			HttpServerConfig serverConfig)
					throws InterruptedException, HttpServerException {
		
		final HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
				request.version(),
				HttpResponseCode.NoContent);
		
		final List<HttpHeader> headers = new ArrayList<>();
		
		headers.add(accessControlAllowOrigin(request));
		
		headers.add(accessControlAllowMethods(
				HttpRequestMethod.HEAD,
				HttpRequestMethod.GET,
				HttpRequestMethod.POST,
				HttpRequestMethod.OPTIONS
				));
		
		headers.add(accessControlAllowCredentialsTrue());
		headers.addAll(connectionKeeyAlive(request, connectionValue));
		
		return new AbstractHttpResponseMessage(
				statusLine,
				HttpHeaderListParser.of(headers),
				HttpContentEncoder.empty()) {
			
			private static final long serialVersionUID = 1L;
		};
	}
	
}