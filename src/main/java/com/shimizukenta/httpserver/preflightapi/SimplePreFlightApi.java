package com.shimizukenta.httpserver.preflightapi;

import java.util.ArrayList;
import java.util.List;

import com.shimizukenta.httpserver.AbstractHttpResponseMessage;
import com.shimizukenta.httpserver.HttpConnectionValue;
import com.shimizukenta.httpserver.HttpHeader;
import com.shimizukenta.httpserver.HttpHeaderListParser;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpRequestMethod;
import com.shimizukenta.httpserver.HttpResponseCode;
import com.shimizukenta.httpserver.HttpResponseMessage;
import com.shimizukenta.httpserver.HttpResponseMessageBodyProxy;
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
		
		headers.add(date());
		headers.add(server(serverConfig));
		
		headers.add(accessControlAllowOrigin(request));
		
		headers.add(accessControlAllowMethods(
				HttpRequestMethod.OPTIONS,
				HttpRequestMethod.GET,
				HttpRequestMethod.HEAD,
				HttpRequestMethod.POST
				));
		
		headers.add(accessControlAllowCredentialsTrue());
		headers.addAll(noCache(request));
		headers.addAll(connectionKeeyAlive(request, connectionValue));
		
		return new AbstractHttpResponseMessage(
				statusLine,
				HttpHeaderListParser.of(headers),
				HttpResponseMessageBodyProxy.empty()) {
			
					private static final long serialVersionUID = -3248402289678462008L;
		};
	}
	
}
