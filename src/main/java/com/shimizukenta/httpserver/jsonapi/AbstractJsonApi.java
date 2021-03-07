package com.shimizukenta.httpserver.jsonapi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.shimizukenta.httpserver.AbstractHttpApi;
import com.shimizukenta.httpserver.AbstractHttpApiConfig;
import com.shimizukenta.httpserver.AbstractHttpResponseMessage;
import com.shimizukenta.httpserver.AbstractHttpResponseMessageBodyProxy;
import com.shimizukenta.httpserver.HttpConnectionValue;
import com.shimizukenta.httpserver.HttpContentType;
import com.shimizukenta.httpserver.HttpEncodingResult;
import com.shimizukenta.httpserver.HttpHeader;
import com.shimizukenta.httpserver.HttpHeaderListParser;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpResponseCode;
import com.shimizukenta.httpserver.HttpResponseMessage;
import com.shimizukenta.httpserver.HttpResponseMessageBodyProxy;
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
		
		final HttpResponseMessageBodyProxy bodyProxy = new AbstractHttpResponseMessageBodyProxy(
				this.buildJson(request).getBytes(StandardCharsets.UTF_8)
				) {
			
					private static final long serialVersionUID = -1857589382812430618L;
		};
		
		final HttpEncodingResult encResult;
		
		try {
			encResult = bodyProxy.get(request.headerListParser().acceptEncodings());
		}
		catch ( IOException giveup ) {
			return HttpResponseMessage.build(request, HttpResponseCode.InternalServerError);
		}
			
		final HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
				request.version(),
				HttpResponseCode.OK);
		
		final List<HttpHeader> headers = new ArrayList<>();
		
		headers.add(date());
		headers.add(server(serverConfig));
		headers.add(lastModified(nowZonedDateTime()));
		
		encResult.optionalEncoding()
		.map(x -> contentEncoding(x))
		.ifPresent(headers::add);
		
		headers.addAll(noCache(request));
		headers.add(acceptRanges());
		headers.add(contentLength(encResult.length()));
		headers.add(contentType(HttpContentType.JSON));
		headers.addAll(connectionKeeyAlive(request, connectionValue));
		
		return new AbstractHttpResponseMessage(
				statusLine,
				HttpHeaderListParser.of(headers),
				bodyProxy) {
			
			private static final long serialVersionUID = -6251284974505121306L;
		};
		
	}
	
}
