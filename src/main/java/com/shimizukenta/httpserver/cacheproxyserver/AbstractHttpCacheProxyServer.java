package com.shimizukenta.httpserver.cacheproxyserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.shimizukenta.httpserver.AbstractHttpProxyServer;
import com.shimizukenta.httpserver.AbstractHttpResponseMessage;
import com.shimizukenta.httpserver.HttpConnectionValue;
import com.shimizukenta.httpserver.HttpEncoding;
import com.shimizukenta.httpserver.HttpEncodingResult;
import com.shimizukenta.httpserver.HttpHeader;
import com.shimizukenta.httpserver.HttpHeaderBuilder;
import com.shimizukenta.httpserver.HttpHeaderListParser;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpResponseCode;
import com.shimizukenta.httpserver.HttpResponseMessage;
import com.shimizukenta.httpserver.HttpServer;
import com.shimizukenta.httpserver.HttpServerConfig;
import com.shimizukenta.httpserver.HttpServerException;
import com.shimizukenta.httpserver.HttpServerMessageHeaderParseException;

public abstract class AbstractHttpCacheProxyServer extends AbstractHttpProxyServer {
	
	private final AbstractHttpCacheProxyServerConfig config;
	
	public AbstractHttpCacheProxyServer(HttpServer in, AbstractHttpCacheProxyServerConfig config) {
		super(in, config);
		this.config = config;
	}
	
	@Override
	public HttpResponseMessage receiveRequest(
			HttpRequestMessage request,
			HttpConnectionValue connectionValue,
			HttpServerConfig serverConfig)
					throws InterruptedException, HttpServerException {
		
		switch ( request.version() ) {
		case HTTP_1_0: {
			/* Nothing */
			break;
		}
		case HTTP_1_1:
		case HTTP_2_0:
		default: {
			
			String host = request.headerListParser().host().orElse(null);
			
			if ( host == null ) {
				return HttpResponseMessage.build(request, HttpResponseCode.BadRequest);
			}
			
			final Set<String> hostNames = config.acceptHostNames();
			
			if ( ! hostNames.isEmpty() ) {
				
				if ( ! hostNames.contains(host) ) {
					
					return HttpResponseMessage.build(request, HttpResponseCode.BadRequest);
				}
			}
		}
		}
		
		if ( ! request.headerListParser().isNoCacheControl() ) {
			
			final HttpResponseMessage cacheRsp = getCache(request);
			
			if ( cacheRsp != null ) {
				
				try {
					return modifyContentEncoding(request, cacheRsp, connectionValue);
				}
				catch ( IOException e ) {
					putLog(e);
					return HttpResponseMessage.build(request, HttpResponseCode.InternalServerError);
				}
			}
		}
		
		{
			HttpResponseMessage rsp = super.receiveRequest(request, connectionValue, serverConfig);
			entryCache(request, rsp);
			return rsp;
		}
	}
	
	private final Collection<Inner> caches = new HashSet<>();
	
	private HttpResponseMessage getCache(HttpRequestMessage request) {
		
		final long sysTime = System.currentTimeMillis();
		
		synchronized ( this.caches ) {
			
			this.caches.removeIf(i -> {
				return i.untilMilliTime < sysTime;
			});
			
			return this.caches.stream()
					.filter(i -> i.equalsRequest(request))
					.findFirst()
					.map(i -> i.response)
					.orElse(null);
		}
	}
	
	private boolean entryCache(
			HttpRequestMessage request,
			HttpResponseMessage response) {
		
		if ( response.statusCode() != HttpResponseCode.OK ) {
			return false;
		}
		
		if ( response.headerListParser().isNoCacheControl() ) {
			return false;
		}
		
		if ( response.headerListParser().optionalValue("Authorization").isPresent() ) {
			return false;
		}
		
		switch ( request.method() ) {
		case HEAD:
		case GET: {
			/* Nothing */
			break;
		}
		default: {
			return false;
		}
		}
		
		synchronized ( this.caches ) {
			final Inner i = new Inner(request, response);
			this.caches.remove(i);
			return this.caches.add(i);
		}
	}
	
	private static HttpResponseMessage modifyContentEncoding(
			HttpRequestMessage request,
			HttpResponseMessage response,
			HttpConnectionValue connectionValue)
					throws HttpServerMessageHeaderParseException, IOException {
		
		final List<HttpEncoding> encs = request.headerListParser().acceptEncodings();
		
		HttpEncodingResult encResult = response.bodyProxy().get(encs);
		
		final HttpHeaderBuilder hb = HttpHeaderBuilder.getInstance();
		
		final List<HttpHeader> headers = new ArrayList<>();
		
		headers.add(hb.date());
		
		response.headers().stream()
		.filter(h -> {
			return ! h.fieldName().equalsIgnoreCase("Date");
		})
		.filter(h -> {
			return ! h.fieldName().equalsIgnoreCase("Content-Encoding");
		})
		.filter(h -> {
			return ! h.fieldName().equalsIgnoreCase("Content-Length");
		})
		.filter(h -> {
			return ! h.fieldName().equalsIgnoreCase("Connection");
		})
		.filter(h -> {
			return ! h.fieldName().equalsIgnoreCase("Keep-Alive");
		})
		.forEach(headers::add);
		
		encResult.optionalEncoding()
		.map(hb::contentEncoding)
		.ifPresent(headers::add);
		
		{
			int len = encResult.length();
			if ( len > 0 ) {
				headers.add(hb.contentLength(len));
			}
		}
		
		headers.addAll(hb.connectionKeeyAlive(request, connectionValue));
		
		
		return new AbstractHttpResponseMessage(
				response.statusLine(),
				HttpHeaderListParser.of(headers),
				response.bodyProxy()) {
			
					private static final long serialVersionUID = 4510404521037618728L;
		};
	}
	
	private final class Inner {
		
		private final HttpRequestMessage request;
		private final HttpResponseMessage response;
		private final long untilMilliTime;
		
		public Inner(
				HttpRequestMessage request,
				HttpResponseMessage response) {
			
			this.request = request;
			this.response = response;
			this.untilMilliTime = System.currentTimeMillis() + config.cacheAgeMilliSeconds();
		}
		
		public int hashCode() {
			return Objects.hash(request.uri(), request.version());
		}
		
		public boolean equals(Object other) {
			if ((other != null) && (other instanceof Inner)) {
				Inner x = (Inner)other;
				return equalsRequest(x.request);
			}
			return false;
		}
		
		public boolean equalsRequest(HttpRequestMessage request) {
			return (request.uri().equalsIgnoreCase(this.request.uri()))
					&& (request.version() == this.request.version());
		}
		
	}
	
}
