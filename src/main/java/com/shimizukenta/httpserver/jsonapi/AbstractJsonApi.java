package com.shimizukenta.httpserver.jsonapi;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.shimizukenta.httpserver.AbstractHttpApi;
import com.shimizukenta.httpserver.AbstractHttpApiConfig;
import com.shimizukenta.httpserver.AbstractHttpResponseMessage;
import com.shimizukenta.httpserver.HttpConnectionValue;
import com.shimizukenta.httpserver.HttpContentEncoder;
import com.shimizukenta.httpserver.HttpEncodingResult;
import com.shimizukenta.httpserver.HttpHeader;
import com.shimizukenta.httpserver.HttpHeaderListParser;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpResponseCode;
import com.shimizukenta.httpserver.HttpResponseMessage;
import com.shimizukenta.httpserver.HttpResponseStatusLine;
import com.shimizukenta.httpserver.HttpServerConfig;
import com.shimizukenta.httpserver.HttpServerException;

public abstract class AbstractJsonApi extends AbstractHttpApi implements JsonApi {
	
	public AbstractJsonApi() {
		super(new AbstractHttpApiConfig() {
			
			private static final long serialVersionUID = 5758672036029477986L;
		});
	}
	
	public AbstractJsonApi(AbstractHttpApiConfig config) {
		super(config);
	}
	
	@Override
	public HttpResponseMessage receiveRequest(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue,
			HttpServerConfig serverConfig)
					throws InterruptedException, HttpServerException {
		
		final HttpEncodingResult encResult = HttpContentEncoder.encode(
				request,
				this.buildJson(request).getBytes(StandardCharsets.UTF_8)
				);
		
		final HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
				request.version(),
				HttpResponseCode.OK);
		
		final List<HttpHeader> headers = new ArrayList<>();
		
		headers.add(header("Server", serverConfig.serverName()));
		
		encResult.contentEncoding()
		.map(x -> contentEncoding(x))
		.ifPresent(headers::add);
		
		headers.add(contentLength(encResult.getBytes()));
		headers.add(header("Content-Type", "application/json"));
		headers.addAll(connectionKeeyAlive(request, connectionValue));
		
		return new AbstractHttpResponseMessage(
				statusLine,
				HttpHeaderListParser.of(headers),
				encResult.getBytes()) {
			
			private static final long serialVersionUID = -6251284974505121306L;
		};
	}
	
}
